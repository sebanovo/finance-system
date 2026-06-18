package com.financesystem.finance_api.modules.reporting.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.reporting.application.exception.ReportAccessDeniedException;
import com.financesystem.finance_api.modules.reporting.application.exception.ReportNotFoundException;
import com.financesystem.finance_api.modules.reporting.application.guard.GuardedSql;
import com.financesystem.finance_api.modules.reporting.application.scope.ReportActor;
import com.financesystem.finance_api.modules.reporting.domain.ReportActorScope;
import com.financesystem.finance_api.modules.reporting.domain.ReportExecutionKind;
import com.financesystem.finance_api.modules.reporting.domain.ReportExecutionStatus;
import com.financesystem.finance_api.modules.reporting.domain.ReportResult;
import com.financesystem.finance_api.modules.reporting.infrastructure.persistence.ReportExecutionEntity;
import com.financesystem.finance_api.modules.reporting.infrastructure.persistence.ReportExecutionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Persists and retrieves audited executions, applying scope-based access control. */
@Service
public class ReportExecutionStore {

    private final ReportExecutionJpaRepository repository;
    private final ReportSnapshotMapper snapshotMapper;
    private final ObjectMapper objectMapper;

    public ReportExecutionStore(ReportExecutionJpaRepository repository,
                                ReportSnapshotMapper snapshotMapper,
                                ObjectMapper objectMapper) {
        this.repository = repository;
        this.snapshotMapper = snapshotMapper;
        this.objectMapper = objectMapper;
    }

    public ReportExecutionEntity persistSuccess(ReportActor actor, ReportExecutionKind kind, String definitionKey,
                                                Map<String, Object> requestParams, String prompt, String transcript,
                                                GuardedSql guarded, ReportResult result) {
        ReportExecutionEntity entity = baseEntity(actor, kind, definitionKey, requestParams, prompt, transcript);
        entity.setSql(guarded.safeSql());
        entity.setReferencedViews(objectMapper.valueToTree(guarded.referencedViews()));
        entity.setSchemaUsed(guarded.schemaUsed());
        entity.setResultJson(snapshotMapper.toSnapshot(result));
        entity.setRowCount(result.rowCount());
        entity.setTruncated(result.truncated());
        entity.setStatus(ReportExecutionStatus.SUCCESS);
        entity.setExecutedAt(Instant.now());
        return repository.save(entity);
    }

    public ReportExecutionEntity persistFailure(ReportActor actor, ReportExecutionKind kind, String definitionKey,
                                                Map<String, Object> requestParams, String prompt, String transcript,
                                                String sql, String schemaUsed, String error) {
        ReportExecutionEntity entity = baseEntity(actor, kind, definitionKey, requestParams, prompt, transcript);
        entity.setSql(sql);
        entity.setSchemaUsed(schemaUsed);
        entity.setStatus(ReportExecutionStatus.FAILED);
        entity.setErrorMessage(truncate(error, 4000));
        entity.setExecutedAt(Instant.now());
        return repository.save(entity);
    }

    public Page<ReportExecutionEntity> list(ReportActor actor, Pageable pageable) {
        if (actor.actorScope() == ReportActorScope.PLATFORM) {
            return repository.findByActorScopeOrderByCreatedAtDesc(ReportActorScope.PLATFORM, pageable);
        }
        return repository.findByActorScopeAndTenantSlugOrderByCreatedAtDesc(
                ReportActorScope.TENANT, actor.tenantSlug(), pageable);
    }

    public ReportExecutionEntity getForActor(ReportActor actor, UUID id) {
        ReportExecutionEntity entity = repository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Ejecución no encontrada: " + id));
        requireAccess(actor, entity);
        return entity;
    }

    public void requireAccess(ReportActor actor, ReportExecutionEntity entity) {
        if (entity.getActorScope() != actor.actorScope()) {
            throw new ReportAccessDeniedException("No autorizado a ver esta ejecución.");
        }
        if (actor.actorScope() == ReportActorScope.TENANT
                && (actor.tenantSlug() == null || !actor.tenantSlug().equals(entity.getTenantSlug()))) {
            throw new ReportAccessDeniedException("No autorizado a ver ejecuciones de otro tenant.");
        }
    }

    private ReportExecutionEntity baseEntity(ReportActor actor, ReportExecutionKind kind, String definitionKey,
                                             Map<String, Object> requestParams, String prompt, String transcript) {
        ReportExecutionEntity entity = new ReportExecutionEntity();
        entity.setRequestedByUserId(actor.userId());
        entity.setRequestedByEmail(actor.email());
        entity.setRequestedByDisplayName(actor.displayName());
        entity.setActorScope(actor.actorScope());
        entity.setTenantSlug(actor.tenantSlug());
        entity.setTenantSchema(actor.tenantSchema());
        entity.setKind(kind);
        entity.setDefinitionKey(definitionKey);
        if (requestParams != null && !requestParams.isEmpty()) {
            entity.setRequestParams(objectMapper.valueToTree(requestParams));
        }
        entity.setPrompt(truncate(prompt, 4000));
        entity.setTranscript(truncate(transcript, 4000));
        entity.setStatus(ReportExecutionStatus.PENDING);
        return entity;
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
