package com.financesystem.finance_api.modules.reporting.application;

import java.util.Locale;
import java.util.Map;

/**
 * Turns raw SQL column names (snake_case / UPPER_CASE, e.g. {@code event_type},
 * {@code created_at}, {@code available_balance}) into readable, executive-grade
 * labels. Known columns get a curated Spanish label; anything else falls back to
 * Title Case so it is still readable. Single source of truth for the screen and
 * the PDF/XLSX exporters.
 */
public final class ReportLabels {

    private static final Map<String, String> EXACT = Map.ofEntries(
            Map.entry("id", "ID"),
            Map.entry("name", "Nombre"),
            Map.entry("description", "Descripción"),
            Map.entry("status", "Estado"),
            Map.entry("active", "Activo"),
            Map.entry("currency", "Moneda"),
            Map.entry("amount", "Monto"),
            Map.entry("balance", "Saldo"),
            Map.entry("total", "Total"),
            Map.entry("count", "Cantidad"),
            Map.entry("email", "Correo"),
            Map.entry("type", "Tipo"),
            Map.entry("date", "Fecha"),
            Map.entry("channel", "Canal"),
            Map.entry("direction", "Dirección"),
            Map.entry("outcome", "Resultado"),
            Map.entry("role", "Rol"),
            Map.entry("created_at", "Fecha de creación"),
            Map.entry("updated_at", "Fecha de actualización"),
            Map.entry("executed_at", "Fecha de ejecución"),
            Map.entry("occurred_at", "Fecha del evento"),
            Map.entry("opened_at", "Fecha de apertura"),
            Map.entry("closed_at", "Fecha de cierre"),
            Map.entry("created_by", "Creado por"),
            Map.entry("tenant_slug", "Tenant"),
            Map.entry("tenant_id", "Tenant"),
            Map.entry("event_type", "Tipo de evento"),
            Map.entry("resource_type", "Tipo de recurso"),
            Map.entry("resource_id", "Recurso"),
            Map.entry("account_id", "Cuenta"),
            Map.entry("account_number", "Número de cuenta"),
            Map.entry("account_name", "Nombre de cuenta"),
            Map.entry("account_type", "Tipo de cuenta"),
            Map.entry("user_id", "Usuario"),
            Map.entry("user_email", "Correo del usuario"),
            Map.entry("first_name", "Nombre"),
            Map.entry("last_name", "Apellido"),
            Map.entry("full_name", "Nombre completo"),
            Map.entry("available_balance", "Saldo disponible"),
            Map.entry("held_balance", "Saldo retenido"),
            Map.entry("total_balance", "Saldo total"),
            Map.entry("transaction_type", "Tipo de transacción"),
            Map.entry("transaction_status", "Estado"),
            Map.entry("transaction_id", "Transacción"),
            Map.entry("transaction_count", "Cantidad de transacciones"),
            Map.entry("total_amount", "Monto total"),
            Map.entry("actor_email", "Correo del actor"),
            Map.entry("source", "Origen"),
            Map.entry("is_primary", "Principal"),
            Map.entry("is_active", "Activo")
    );

    private ReportLabels() {
    }

    public static String humanize(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return "";
        }
        String key = rawName.trim().toLowerCase(Locale.ROOT);
        String exact = EXACT.get(key);
        if (exact != null) {
            return exact;
        }
        // Foreign-key columns: "subscription_id" -> "Subscription".
        String base = key.endsWith("_id") ? key.substring(0, key.length() - 3) : key;
        String[] parts = base.split("[_\\s]+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1));
            }
        }
        return sb.length() == 0 ? rawName.trim() : sb.toString();
    }

    /** Report title: prefer a curated definition title, else humanize the key. */
    public static String reportTitle(String definitionTitle, String definitionKey) {
        if (definitionTitle != null && !definitionTitle.isBlank()) {
            return definitionTitle;
        }
        if (definitionKey != null && !definitionKey.isBlank()) {
            return humanize(definitionKey);
        }
        return "Reporte IA";
    }
}
