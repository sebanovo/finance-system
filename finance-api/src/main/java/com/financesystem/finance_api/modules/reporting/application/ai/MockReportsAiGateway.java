package com.financesystem.finance_api.modules.reporting.application.ai;

import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/**
 * Local mock brain used until the real {@code reports-ai} microservice is wired.
 * Instead of a single canned query, it interprets the natural-language prompt:
 * it normalizes the text and matches keywords (Spanish/English) to the most
 * relevant whitelisted reporting view, projecting a category + value column
 * first so the auto charts render. Active when {@code reports.ai.mode=mock}.
 */
@Component
@ConditionalOnProperty(name = "reports.ai.mode", havingValue = "mock", matchIfMissing = true)
public class MockReportsAiGateway implements ReportsAiGateway {

    private static final String GLOBAL_SQL =
            "SELECT tenant_slug, transaction_count, total_amount "
                    + "FROM tenant_movement_ranking ORDER BY total_amount DESC";

    private record Intent(List<String> keywords, String tenantSql, String label) {
    }

    private static final List<Intent> INTENTS = List.of(
            new Intent(
                    List.of("ingreso", "egreso", "gasto", "income", "expense", "financ", "resumen"),
                    "SELECT transaction_type, currency, transaction_count, total_amount "
                            + "FROM reporting_tenant_income_expenses ORDER BY total_amount DESC",
                    "ingresos y egresos por tipo"),
            new Intent(
                    List.of("saldo", "balance", "cuenta", "account"),
                    "SELECT account_name, account_type, currency, available_balance, total_balance "
                            + "FROM reporting_tenant_accounts_balances ORDER BY total_balance DESC",
                    "saldos de cuentas"),
            new Intent(
                    List.of("usuario", "user", "cliente", "actividad", "registrad"),
                    "SELECT email, first_name, last_name, status, active "
                            + "FROM reporting_tenant_users_activity ORDER BY created_at DESC",
                    "actividad de usuarios"),
            new Intent(
                    List.of("auditor", "evento", "event", "log", "acceso", "login", "seguridad"),
                    "SELECT event_type, actor_email, outcome, created_at "
                            + "FROM reporting_tenant_audit ORDER BY created_at DESC",
                    "eventos de auditoría"),
            new Intent(
                    List.of("movimiento", "transacc", "transaction", "transfer", "pago", "payment", "deposito", "retiro"),
                    "SELECT transaction_type, status, currency, amount, created_at "
                            + "FROM reporting_tenant_movements ORDER BY created_at DESC",
                    "movimientos financieros")
    );

    private static final Intent DEFAULT = INTENTS.get(0);

    @Override
    public AiSqlResponse generateFromText(String prompt, ReportScope scope, String schemaDescription) {
        Intent intent = match(prompt);
        return new AiSqlResponse(null, sqlFor(intent, scope),
                "Interpreté tu solicitud como: " + intent.label() + " (modo asistido).");
    }

    @Override
    public AiSqlResponse transcribeAndGenerate(byte[] audio, String mimeType, ReportScope scope,
                                               String schemaDescription) {
        String transcript = "Mostrar los movimientos recientes (transcripción simulada).";
        Intent intent = match(transcript);
        return new AiSqlResponse(transcript, sqlFor(intent, scope),
                "Interpreté tu solicitud como: " + intent.label() + " (modo asistido).");
    }

    private Intent match(String prompt) {
        String text = normalize(prompt);
        for (Intent intent : INTENTS) {
            for (String keyword : intent.keywords()) {
                if (text.contains(keyword)) {
                    return intent;
                }
            }
        }
        return DEFAULT;
    }

    private String sqlFor(Intent intent, ReportScope scope) {
        return scope == ReportScope.GLOBAL ? GLOBAL_SQL : intent.tenantSql();
    }

    /** Lowercase + strip accents so "auditoría" matches "auditor". */
    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String lowered = value.toLowerCase(Locale.ROOT);
        return Normalizer.normalize(lowered, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
