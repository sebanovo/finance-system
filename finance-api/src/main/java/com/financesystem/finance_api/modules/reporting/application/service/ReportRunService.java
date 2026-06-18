package com.financesystem.finance_api.modules.reporting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.reporting.application.ai.AiRateLimiter;
import com.financesystem.finance_api.modules.reporting.application.ai.AiSqlResponse;
import com.financesystem.finance_api.modules.reporting.application.ai.ReportsAiGateway;
import com.financesystem.finance_api.modules.reporting.application.config.ReportingProperties;
import com.financesystem.finance_api.modules.reporting.application.exception.ReportNotFoundException;
import com.financesystem.finance_api.modules.reporting.application.executor.ReadOnlySqlExecutor;
import com.financesystem.finance_api.modules.reporting.application.guard.AiSqlGuard;
import com.financesystem.finance_api.modules.reporting.application.guard.GuardedSql;
import com.financesystem.finance_api.modules.reporting.application.registry.ReportDefinitionRegistry;
import com.financesystem.finance_api.modules.reporting.application.scope.ReportActor;
import com.financesystem.finance_api.modules.reporting.domain.ReportDefinition;
import com.financesystem.finance_api.modules.reporting.domain.ReportExecutionKind;
import com.financesystem.finance_api.modules.reporting.domain.ReportResult;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import com.financesystem.finance_api.modules.reporting.infrastructure.persistence.ReportExecutionEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Orchestrates both rails: controlled definitions and AI (text/voice), plus rerun. */
@Service
public class ReportRunService {

    private final ReportDefinitionRegistry registry;
    private final ReportSqlBuilder sqlBuilder;
    private final AiSqlGuard guard;
    private final ReadOnlySqlExecutor executor;
    private final ReportExecutionStore store;
    private final ReportsAiGateway aiGateway;
    private final ReportingProperties properties;
    private final ObjectMapper objectMapper;
    private final AiRateLimiter aiRateLimiter;
    private final AuditTrailService auditTrailService;

    public ReportRunService(ReportDefinitionRegistry registry, ReportSqlBuilder sqlBuilder, AiSqlGuard guard,
                            ReadOnlySqlExecutor executor, ReportExecutionStore store, ReportsAiGateway aiGateway,
                            ReportingProperties properties, ObjectMapper objectMapper,
                            AiRateLimiter aiRateLimiter, AuditTrailService auditTrailService) {
        this.registry = registry;
        this.sqlBuilder = sqlBuilder;
        this.guard = guard;
        this.executor = executor;
        this.store = store;
        this.aiGateway = aiGateway;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.aiRateLimiter = aiRateLimiter;
        this.auditTrailService = auditTrailService;
    }

    public record RunOutcome(ReportExecutionEntity execution, ReportResult result, String explanation) {
    }

    public RunOutcome runControlled(ReportActor actor, String key, Map<String, Object> params) {
        ReportDefinition definition = registry.find(key)
                .filter(d -> d.scope() == actor.scope())
                .orElseThrow(() -> new ReportNotFoundException("Reporte no encontrado: " + key));

        ReportSqlBuilder.BuiltSql built = sqlBuilder.build(definition, params, null);
        try {
            GuardedSql guarded = guardFor(actor, built.sql());
            ReportResult result = execute(actor, guarded, built.bindParams());
            ReportExecutionEntity execution = store.persistSuccess(
                    actor, ReportExecutionKind.CONTROLLED, key, params, null, null, guarded, result);
            audit(actor, execution, ReportAuditEvents.EXECUTED);
            return new RunOutcome(execution, result, null);
        } catch (RuntimeException e) {
            ReportExecutionEntity failed = store.persistFailure(actor, ReportExecutionKind.CONTROLLED, key, params,
                    null, null, built.sql(), actor.tenantSchema(), e.getMessage());
            audit(actor, failed, ReportAuditEvents.FAILED);
            throw e;
        }
    }

    public RunOutcome runAiText(ReportActor actor, String prompt) {
        aiRateLimiter.check(rateKey(actor));
        AiSqlResponse ai = aiGateway.generateFromText(prompt, actor.scope(), schemaDescription(actor.scope()));
        return runAi(actor, prompt, null, ai);
    }

    public RunOutcome runAiVoice(ReportActor actor, byte[] audio, String mimeType) {
        aiRateLimiter.check(rateKey(actor));
        validateAudio(audio, mimeType);
        AiSqlResponse ai = aiGateway.transcribeAndGenerate(audio, mimeType, actor.scope(),
                schemaDescription(actor.scope()));
        return runAi(actor, null, ai.transcript(), ai);
    }

    private void validateAudio(byte[] audio, String mimeType) {
        if (audio == null || audio.length == 0) {
            throw new IllegalArgumentException("El audio está vacío.");
        }
        if (audio.length > properties.getAi().getAudioMaxBytes()) {
            throw new IllegalArgumentException("El audio excede el tamaño máximo permitido.");
        }
        if (mimeType != null && !mimeType.isBlank()) {
            Set<String> allowed = Set.of(properties.getAi().getAudioAllowedMimes().split("\\s*,\\s*"));
            if (!allowed.contains(mimeType)) {
                throw new IllegalArgumentException("Formato de audio no soportado: " + mimeType);
            }
        }
    }

    public RunOutcome rerun(ReportActor actor, java.util.UUID executionId) {
        ReportExecutionEntity execution = store.getForActor(actor, executionId);
        if (execution.getKind() == ReportExecutionKind.CONTROLLED) {
            Map<String, Object> params = readParams(execution.getRequestParams());
            return runControlled(actor, execution.getDefinitionKey(), params);
        }
        // AI rerun: re-execute the stored, already-validated SQL (never re-queries the LLM).
        GuardedSql guarded = guardFor(actor, execution.getSql());
        ReportResult result = execute(actor, guarded, List.of());
        ReportExecutionEntity persisted = store.persistSuccess(actor, ReportExecutionKind.AI, null,
                null, execution.getPrompt(), execution.getTranscript(), guarded, result);
        audit(actor, persisted, ReportAuditEvents.EXECUTED);
        return new RunOutcome(persisted, result, null);
    }

    private RunOutcome runAi(ReportActor actor, String prompt, String transcript, AiSqlResponse ai) {
        try {
            GuardedSql guarded = guardFor(actor, ai.sql());
            ReportResult result = execute(actor, guarded, List.of());
            ReportExecutionEntity execution = store.persistSuccess(
                    actor, ReportExecutionKind.AI, null, null, prompt, transcript, guarded, result);
            audit(actor, execution, ReportAuditEvents.EXECUTED);
            return new RunOutcome(execution, result, ai.explanation());
        } catch (RuntimeException e) {
            ReportExecutionEntity failed = store.persistFailure(actor, ReportExecutionKind.AI, null, null,
                    prompt, transcript, ai.sql(), actor.tenantSchema(), e.getMessage());
            audit(actor, failed, ReportAuditEvents.FAILED);
            throw e;
        }
    }

    private String rateKey(ReportActor actor) {
        if (actor.userId() != null) {
            return actor.actorScope().name() + ":" + actor.userId();
        }
        return actor.actorScope().name() + ":" + (actor.email() != null ? actor.email() : "anonymous");
    }

    private void audit(ReportActor actor, ReportExecutionEntity execution, String eventType) {
        try {
            java.util.Map<String, Object> details = new java.util.LinkedHashMap<>();
            details.put("kind", execution.getKind().name());
            details.put("definitionKey", execution.getDefinitionKey());
            details.put("status", execution.getStatus().name());
            details.put("rowCount", execution.getRowCount());
            details.put("scope", actor.actorScope().name());
            String resourceId = execution.getId() != null ? execution.getId().toString() : null;
            if (actor.actorScope() == com.financesystem.finance_api.modules.reporting.domain.ReportActorScope.PLATFORM) {
                auditTrailService.recordPlatformEvent(eventType, ReportAuditEvents.RESOURCE_TYPE, resourceId, details);
            } else {
                auditTrailService.recordTenantEvent(eventType, ReportAuditEvents.RESOURCE_TYPE, resourceId, details);
            }
        } catch (Exception ignored) {
            // Auditing must never break the report execution.
        }
    }

    private GuardedSql guardFor(ReportActor actor, String sql) {
        Set<String> allowed = registry.allowedViews(actor.scope());
        return guard.guard(sql, actor.scope(), allowed, actor.tenantSchema(), properties.getSql().getMaxRows());
    }

    private ReportResult execute(ReportActor actor, GuardedSql guarded, List<Object> binds) {
        return executor.execute(actor.scope(), guarded, binds,
                properties.getSql().getMaxRows(), properties.getSql().getStatementTimeoutMs());
    }

    private Map<String, Object> readParams(JsonNode node) {
        if (node == null || node.isNull()) {
            return Map.of();
        }
        try {
            return objectMapper.convertValue(node, new com.fasterxml.jackson.core.type.TypeReference<>() {
            });
        } catch (RuntimeException e) {
            return Map.of();
        }
    }

    private String schemaDescription(ReportScope scope) {
        Set<String> views = registry.allowedViews(scope);
        if (scope == ReportScope.GLOBAL) {
            return "Vistas disponibles (schema reporting): " + String.join(", ", views)
                    + ". Agrupá por tenant_slug para comparar tenants. Devolvé una sola sentencia SELECT.";
        }
        return "Vistas disponibles (sin prefijo de schema): " + String.join(", ", views)
                + ". No uses prefijos de schema. Devolvé una sola sentencia SELECT.";
    }
}
