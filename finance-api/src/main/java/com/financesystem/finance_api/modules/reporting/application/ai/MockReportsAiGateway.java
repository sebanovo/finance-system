package com.financesystem.finance_api.modules.reporting.application.ai;

import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Local mock brain used until the {@code reports-ai} microservice (Fase 3) is
 * wired. Returns a deterministic, whitelisted query so the AI rail works
 * end-to-end. Active when {@code reports.ai.mode=mock} (the default).
 */
@Component
@ConditionalOnProperty(name = "reports.ai.mode", havingValue = "mock", matchIfMissing = true)
public class MockReportsAiGateway implements ReportsAiGateway {

    @Override
    public AiSqlResponse generateFromText(String prompt, ReportScope scope, String schemaDescription) {
        return new AiSqlResponse(null, cannedSql(scope),
                "Respuesta simulada (modo mock). Reemplazar por el microservicio reports-ai.");
    }

    @Override
    public AiSqlResponse transcribeAndGenerate(byte[] audio, String mimeType, ReportScope scope,
                                               String schemaDescription) {
        String transcript = "Mostrar los movimientos recientes (transcripción simulada).";
        return new AiSqlResponse(transcript, cannedSql(scope),
                "Respuesta simulada (modo mock).");
    }

    private String cannedSql(ReportScope scope) {
        return scope == ReportScope.GLOBAL
                ? "SELECT tenant_slug, transaction_count, total_amount FROM tenant_movement_ranking ORDER BY total_amount DESC"
                : "SELECT transaction_id, created_at, transaction_type, status, amount, currency FROM reporting_tenant_movements ORDER BY created_at DESC";
    }
}
