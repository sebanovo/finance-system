package com.financesystem.finance_api.modules.reporting.application.registry;

import com.financesystem.finance_api.modules.reporting.domain.ReportDataType;
import com.financesystem.finance_api.modules.reporting.domain.ReportDefinition;
import com.financesystem.finance_api.modules.reporting.domain.ReportParam;
import com.financesystem.finance_api.modules.reporting.domain.ReportParam.ReportParamOperator;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import com.financesystem.finance_api.modules.reporting.domain.ReportSort;
import com.financesystem.finance_api.modules.reporting.domain.SortDirection;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * In-code catalog of controlled report definitions plus the per-scope view
 * whitelist that also defines the AI capability surface.
 *
 * <p>Adding a report = one entry here + one view migration (no new classes).
 * Validated at startup ({@link #validateOnStartup()}).
 */
@Component
public class ReportDefinitionRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ReportDefinitionRegistry.class);

    /** Whitelisted views per scope — the surface both rails may read. */
    public static final Set<String> TENANT_VIEWS = Set.of(
            "reporting_tenant_executive_overview",
            "reporting_tenant_movements",
            "reporting_tenant_accounts_balances",
            "reporting_tenant_users_activity",
            "reporting_tenant_income_expenses",
            "reporting_tenant_transactions_analysis",
            "reporting_tenant_audit",
            "reporting_tenant_limits_alerts",
            "reporting_tenant_accounting_ledger",
            "reporting_tenant_fx_fees",
            "reporting_tenant_customer_activity"
    );
    public static final Set<String> GLOBAL_VIEWS = Set.of(
            "platform_overview",
            "tenant_movement_ranking",
            "tenant_users",
            "tenant_financial_activity",
            "tenant_operational_risk",
            "platform_audit",
            "platform_backups",
            "platform_subscriptions"
    );

    // Option sets for categorical filters (derived from the domain enums).
    private static final List<String> CURRENCIES = List.of("BOB", "USD", "EUR", "USDT");
    private static final List<String> TX_STATUSES = List.of(
            "PENDING", "PENDING_REVIEW", "PROCESSING", "AUTHORIZED", "COMPLETED",
            "FAILED", "REVERSED", "PARTIALLY_REFUNDED", "CANCELLED", "EXPIRED");
    private static final List<String> ACCOUNT_STATUSES = List.of(
            "ACTIVE", "BLOCKED", "SUSPENDED", "FROZEN", "CLOSED", "PENDING_VERIFICATION", "PENDING_APPROVAL");
    private static final List<String> USER_STATUSES = List.of("ACTIVE", "INACTIVE");
    private static final List<String> LEDGER_STATUSES = List.of("DRAFT", "POSTED", "REVERSED", "VOID");
    private static final List<String> ENTRY_TYPES = List.of(
            "TRANSFER", "DEPOSIT", "WITHDRAWAL", "PAYMENT", "REVERSAL", "REFUND",
            "HOLD", "RELEASE", "FEE", "ADJUSTMENT", "SETTLEMENT");
    private static final List<String> LIMIT_TYPES = List.of(
            "PER_TRANSACTION_AMOUNT", "DAILY_AMOUNT", "MONTHLY_AMOUNT",
            "DAILY_COUNT", "MONTHLY_COUNT", "MIN_AMOUNT", "MAX_BALANCE");
    private static final List<String> FX_ITEM_TYPES = List.of("RATE", "FEE");
    private static final List<String> BACKUP_STATUSES = List.of(
            "PENDING", "RUNNING", "COMPLETED", "FAILED", "RESTORING", "RESTORED", "RESTORE_FAILED");
    private static final List<String> BACKUP_SCOPES = List.of("TENANT_SCHEMA", "FULL_DATABASE");
    private static final List<String> SUBSCRIPTION_STATUSES = List.of("TRIAL", "ACTIVE", "EXPIRED", "CANCELLED");
    private static final List<String> AUDIT_OUTCOMES = List.of("SUCCESS", "FAILURE");

    private final Map<String, ReportDefinition> definitions = new LinkedHashMap<>();

    public ReportDefinitionRegistry() {
        register(new ReportDefinition(
                "TENANT_EXECUTIVE_OVERVIEW",
                "Resumen ejecutivo del tenant",
                "Indicadores clave: usuarios y cuentas activas, saldos, volumen completado.",
                ReportScope.TENANT,
                "reporting_tenant_executive_overview",
                null,
                List.of(),
                null,
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_MOVEMENTS",
                "Movimientos financieros",
                "Transacciones del tenant con filtros por fecha, estado y moneda.",
                ReportScope.TENANT,
                "reporting_tenant_movements",
                null,
                List.of(
                        new ReportParam("dateFrom", "created_at", ReportDataType.TIMESTAMP,
                                ReportParamOperator.GREATER_THAN_OR_EQUAL, false),
                        new ReportParam("dateTo", "created_at", ReportDataType.TIMESTAMP,
                                ReportParamOperator.LESS_THAN_OR_EQUAL, false),
                        select("status", "status", TX_STATUSES),
                        select("currency", "currency", CURRENCIES)
                ),
                new ReportSort("created_at", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_USERS_ACTIVITY",
                "Usuarios, roles y actividad",
                "Usuarios del tenant con su estado y actividad.",
                ReportScope.TENANT,
                "reporting_tenant_users_activity",
                null,
                List.of(select("status", "status", USER_STATUSES)),
                new ReportSort("created_at", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_ACCOUNTS_BALANCES",
                "Cuentas y saldos",
                "Cuentas del tenant con saldos disponibles y retenidos.",
                ReportScope.TENANT,
                "reporting_tenant_accounts_balances",
                null,
                List.of(
                        select("currency", "currency", CURRENCIES),
                        select("status", "status", ACCOUNT_STATUSES)
                ),
                new ReportSort("total_balance", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_INCOME_EXPENSES",
                "Ingresos vs egresos",
                "Montos completados agrupados por tipo de transacción y moneda.",
                ReportScope.TENANT,
                "reporting_tenant_income_expenses",
                null,
                List.of(select("currency", "currency", CURRENCIES)),
                new ReportSort("total_amount", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_TRANSACTIONS_ANALYSIS",
                "Transacciones por estado y tipo",
                "Conteo y monto de transacciones agrupadas por estado, tipo y canal.",
                ReportScope.TENANT,
                "reporting_tenant_transactions_analysis",
                null,
                List.of(
                        select("status", "status", TX_STATUSES),
                        select("currency", "currency", CURRENCIES)
                ),
                new ReportSort("total_amount", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_AUDIT",
                "Auditoría del tenant",
                "Eventos de auditoría del tenant (sin datos sensibles).",
                ReportScope.TENANT,
                "reporting_tenant_audit",
                null,
                List.of(
                        new ReportParam("eventType", "event_type", ReportDataType.TEXT,
                                ReportParamOperator.EQUALS, false),
                        select("outcome", "outcome", AUDIT_OUTCOMES)
                ),
                new ReportSort("created_at", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_LIMITS_AND_ALERTS",
                "Límites, alertas y revisión",
                "Reglas de límite del tenant y su configuración de revisión.",
                ReportScope.TENANT,
                "reporting_tenant_limits_alerts",
                null,
                List.of(
                        select("limitType", "limit_type", LIMIT_TYPES),
                        select("currency", "currency", CURRENCIES),
                        new ReportParam("active", "active", ReportDataType.BOOLEAN,
                                ReportParamOperator.EQUALS, false)
                ),
                new ReportSort("created_at", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_ACCOUNTING_LEDGER",
                "Contabilidad y ledger",
                "Líneas del libro contable con su asiento.",
                ReportScope.TENANT,
                "reporting_tenant_accounting_ledger",
                null,
                List.of(
                        select("entryType", "entry_type", ENTRY_TYPES),
                        select("status", "status", LEDGER_STATUSES),
                        select("currency", "currency", CURRENCIES)
                ),
                new ReportSort("posted_at", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_FX_AND_FEES",
                "Tasas, conversiones y comisiones",
                "Tasas de cambio y comisiones por operación del tenant.",
                ReportScope.TENANT,
                "reporting_tenant_fx_fees",
                null,
                List.of(
                        select("itemType", "item_type", FX_ITEM_TYPES),
                        new ReportParam("active", "active", ReportDataType.BOOLEAN,
                                ReportParamOperator.EQUALS, false)
                ),
                new ReportSort("created_at", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "TENANT_CUSTOMER_ACTIVITY",
                "Actividad de clientes",
                "Actividad por usuario: cantidad de transacciones, monto y última actividad.",
                ReportScope.TENANT,
                "reporting_tenant_customer_activity",
                null,
                List.of(),
                new ReportSort("total_amount", SortDirection.DESC),
                Set.of("reports.tenant.run")
        ));

        register(new ReportDefinition(
                "PLATFORM_EXECUTIVE_OVERVIEW",
                "Resumen general SaaS",
                "Agregados por tenant: usuarios y cuentas activas, saldos y volumen.",
                ReportScope.GLOBAL,
                "platform_overview",
                null,
                List.of(),
                null,
                Set.of("reports.platform.run")
        ));

        register(new ReportDefinition(
                "PLATFORM_TENANT_MOVEMENT_RANKING",
                "Ranking de tenants por movimiento",
                "Tenants ordenados por volumen y cantidad de transacciones.",
                ReportScope.GLOBAL,
                "tenant_movement_ranking",
                null,
                List.of(),
                new ReportSort("total_amount", SortDirection.DESC),
                Set.of("reports.platform.run")
        ));

        register(new ReportDefinition(
                "PLATFORM_TENANT_USERS",
                "Usuarios por tenant",
                "Total de usuarios y usuarios activos por organización.",
                ReportScope.GLOBAL,
                "tenant_users",
                null,
                List.of(),
                new ReportSort("total_users", SortDirection.DESC),
                Set.of("reports.platform.run")
        ));

        register(new ReportDefinition(
                "PLATFORM_TENANT_FINANCIAL_ACTIVITY",
                "Actividad financiera por tenant",
                "Transacciones y montos por tenant y estado.",
                ReportScope.GLOBAL,
                "tenant_financial_activity",
                null,
                List.of(),
                new ReportSort("total_amount", SortDirection.DESC),
                Set.of("reports.platform.run")
        ));

        register(new ReportDefinition(
                "PLATFORM_OPERATIONAL_RISK",
                "Riesgo operativo",
                "Por tenant: transacciones fallidas, reversas y cuentas bloqueadas.",
                ReportScope.GLOBAL,
                "tenant_operational_risk",
                null,
                List.of(),
                new ReportSort("failed_transactions", SortDirection.DESC),
                Set.of("reports.platform.run")
        ));

        register(new ReportDefinition(
                "PLATFORM_AUDIT",
                "Auditoría de plataforma",
                "Eventos de auditoría a nivel plataforma (sin datos sensibles).",
                ReportScope.GLOBAL,
                "platform_audit",
                null,
                List.of(
                        new ReportParam("eventType", "event_type", ReportDataType.TEXT,
                                ReportParamOperator.EQUALS, false),
                        select("outcome", "outcome", AUDIT_OUTCOMES)
                ),
                new ReportSort("created_at", SortDirection.DESC),
                Set.of("reports.platform.run")
        ));

        register(new ReportDefinition(
                "PLATFORM_BACKUPS",
                "Backups y restauraciones",
                "Trabajos de backup y restore de todos los tenants.",
                ReportScope.GLOBAL,
                "platform_backups",
                null,
                List.of(
                        select("status", "status", BACKUP_STATUSES),
                        select("scope", "scope", BACKUP_SCOPES)
                ),
                new ReportSort("created_at", SortDirection.DESC),
                Set.of("reports.platform.run")
        ));

        register(new ReportDefinition(
                "PLATFORM_SUBSCRIPTIONS",
                "Suscripciones y planes",
                "Suscripciones de tenants con su plan y estado.",
                ReportScope.GLOBAL,
                "platform_subscriptions",
                null,
                List.of(select("status", "status", SUBSCRIPTION_STATUSES)),
                new ReportSort("created_at", SortDirection.DESC),
                Set.of("reports.platform.run")
        ));
    }

    /** Categorical equality filter rendered as a dropdown in the UI. */
    private static ReportParam select(String name, String column, List<String> options) {
        return new ReportParam(name, column, ReportDataType.TEXT, ReportParamOperator.EQUALS, false, options);
    }

    private void register(ReportDefinition definition) {
        definitions.put(definition.key(), definition);
    }

    public Optional<ReportDefinition> find(String key) {
        return Optional.ofNullable(definitions.get(key));
    }

    public List<ReportDefinition> listByScope(ReportScope scope) {
        return definitions.values().stream().filter(d -> d.scope() == scope).toList();
    }

    public Set<String> allowedViews(ReportScope scope) {
        return scope == ReportScope.GLOBAL ? GLOBAL_VIEWS : TENANT_VIEWS;
    }

    @PostConstruct
    void validateOnStartup() {
        for (ReportDefinition d : definitions.values()) {
            if (d.key() == null || d.key().isBlank()) {
                throw new IllegalStateException("Report definition with blank key.");
            }
            if (d.scope() == null) {
                throw new IllegalStateException("Report '" + d.key() + "' has no scope.");
            }
            if (!d.usesSourceView() && !d.usesSqlTemplate()) {
                throw new IllegalStateException("Report '" + d.key() + "' needs a sourceView or sqlTemplate.");
            }
            if (d.usesSourceView() && !allowedViews(d.scope()).contains(d.sourceView())) {
                throw new IllegalStateException(
                        "Report '" + d.key() + "' sourceView '" + d.sourceView()
                                + "' is not whitelisted for scope " + d.scope() + ".");
            }
            if (d.defaultSort() != null && (d.defaultSort().field() == null || d.defaultSort().field().isBlank())) {
                throw new IllegalStateException("Report '" + d.key() + "' has an invalid defaultSort.");
            }
        }
        logger.info("Validated {} report definitions.", definitions.size());
    }
}
