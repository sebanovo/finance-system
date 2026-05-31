package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountName;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportColumnKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportMetricKey;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AccountsReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(AccountReportFieldKey.ACCOUNT_ID.name(), field(
                AccountReportFieldKey.ACCOUNT_ID,
                "Account ID",
                ReportFieldType.UUID,
                "a.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_USER_ID.name(), field(
                AccountReportFieldKey.ACCOUNT_USER_ID,
                "User ID",
                ReportFieldType.UUID,
                "a.user_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_ACCOUNT_NUMBER.name(), field(
                AccountReportFieldKey.ACCOUNT_ACCOUNT_NUMBER,
                "Número de cuenta",
                ReportFieldType.TEXT,
                "a.account_number",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_ACCOUNT_NAME.name(), field(
                AccountReportFieldKey.ACCOUNT_ACCOUNT_NAME,
                "Nombre de cuenta",
                ReportFieldType.ENUM,
                "a.account_name",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(AccountName.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_CUSTOM_ALIAS.name(), field(
                AccountReportFieldKey.ACCOUNT_CUSTOM_ALIAS,
                "Alias personalizado",
                ReportFieldType.TEXT,
                "a.custom_alias",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_ACCOUNT_TYPE.name(), field(
                AccountReportFieldKey.ACCOUNT_ACCOUNT_TYPE,
                "Tipo de cuenta",
                ReportFieldType.ENUM,
                "a.account_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(AccountType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_CURRENCY.name(), field(
                AccountReportFieldKey.ACCOUNT_CURRENCY,
                "Moneda",
                ReportFieldType.ENUM,
                "a.currency",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(CurrencyCode.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_AVAILABLE_BALANCE.name(), field(
                AccountReportFieldKey.ACCOUNT_AVAILABLE_BALANCE,
                "Saldo disponible",
                ReportFieldType.MONEY,
                "a.available_balance",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_HELD_BALANCE.name(), field(
                AccountReportFieldKey.ACCOUNT_HELD_BALANCE,
                "Saldo retenido",
                ReportFieldType.MONEY,
                "a.held_balance",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_TOTAL_BALANCE.name(), field(
                AccountReportFieldKey.ACCOUNT_TOTAL_BALANCE,
                "Saldo total",
                ReportFieldType.MONEY,
                "(a.available_balance + a.held_balance)",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_STATUS.name(), field(
                AccountReportFieldKey.ACCOUNT_STATUS,
                "Estado",
                ReportFieldType.ENUM,
                "a.status",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(AccountStatus.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_STATUS_REASON.name(), field(
                AccountReportFieldKey.ACCOUNT_STATUS_REASON,
                "Motivo de estado",
                ReportFieldType.TEXT,
                "a.status_reason",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_ACTIVE.name(), field(
                AccountReportFieldKey.ACCOUNT_ACTIVE,
                "Activo",
                ReportFieldType.BOOLEAN,
                "a.active",
                List.of(ReportOperator.EQ),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_PRIMARY.name(), field(
                AccountReportFieldKey.ACCOUNT_PRIMARY,
                "Principal",
                ReportFieldType.BOOLEAN,
                "a.is_primary",
                List.of(ReportOperator.EQ),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_OPENED_AT.name(), field(
                AccountReportFieldKey.ACCOUNT_OPENED_AT,
                "Fecha de apertura",
                ReportFieldType.DATETIME,
                "a.opened_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_CLOSED_AT.name(), field(
                AccountReportFieldKey.ACCOUNT_CLOSED_AT,
                "Fecha de cierre",
                ReportFieldType.DATETIME,
                "a.closed_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_CREATED_AT.name(), field(
                AccountReportFieldKey.ACCOUNT_CREATED_AT,
                "Fecha de creación",
                ReportFieldType.DATETIME,
                "a.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.ACCOUNT_UPDATED_AT.name(), field(
                AccountReportFieldKey.ACCOUNT_UPDATED_AT,
                "Fecha de actualización",
                ReportFieldType.DATETIME,
                "a.updated_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.USER_ID.name(), field(
                AccountReportFieldKey.USER_ID,
                "User ID",
                ReportFieldType.UUID,
                "u.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                "user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.USER_EMAIL.name(), field(
                AccountReportFieldKey.USER_EMAIL,
                "Correo usuario",
                ReportFieldType.TEXT,
                "u.email",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.USER_FIRST_NAME.name(), field(
                AccountReportFieldKey.USER_FIRST_NAME,
                "Nombre usuario",
                ReportFieldType.TEXT,
                "u.first_name",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.USER_LAST_NAME.name(), field(
                AccountReportFieldKey.USER_LAST_NAME,
                "Apellido usuario",
                ReportFieldType.TEXT,
                "u.last_name",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.USER_FULL_NAME.name(), field(
                AccountReportFieldKey.USER_FULL_NAME,
                "Nombre completo usuario",
                ReportFieldType.TEXT,
                "concat_ws(' ', u.first_name, u.last_name)",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.USER_ACTIVE.name(), field(
                AccountReportFieldKey.USER_ACTIVE,
                "Usuario activo",
                ReportFieldType.BOOLEAN,
                "u.active",
                List.of(ReportOperator.EQ),
                List.of(),
                "user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountReportFieldKey.USER_STATUS.name(), field(
                AccountReportFieldKey.USER_STATUS,
                "Estado usuario",
                ReportFieldType.ENUM,
                "u.status",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus.values()),
                "user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountReportFieldKey.USER_CREATED_AT.name(), field(
                AccountReportFieldKey.USER_CREATED_AT,
                "Usuario creado el",
                ReportFieldType.DATETIME,
                "u.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                "user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountReportFieldKey.USER_UPDATED_AT.name(), field(
                AccountReportFieldKey.USER_UPDATED_AT,
                "Usuario actualizado el",
                ReportFieldType.DATETIME,
                "u.updated_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                "user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));

        Map<String, ReportRelation> relations = new java.util.LinkedHashMap<>();
        relations.put("user", new ReportRelation(
                "user",
                "LEFT JOIN tenant_users u ON u.id = a.user_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(AccountReportMetricKey.TOTAL_ACCOUNTS.name(), metric(
                AccountReportMetricKey.TOTAL_ACCOUNTS,
                "Total cuentas",
                ReportFieldType.NUMBER,
                "COUNT(a.id)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountReportMetricKey.ACTIVE_ACCOUNTS.name(), metric(
                AccountReportMetricKey.ACTIVE_ACCOUNTS,
                "Cuentas activas",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE a.active = true)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountReportMetricKey.INACTIVE_ACCOUNTS.name(), metric(
                AccountReportMetricKey.INACTIVE_ACCOUNTS,
                "Cuentas inactivas",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE a.active = false)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountReportMetricKey.PRIMARY_ACCOUNTS.name(), metric(
                AccountReportMetricKey.PRIMARY_ACCOUNTS,
                "Cuentas principales",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE a.is_primary = true)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountReportMetricKey.TOTAL_AVAILABLE_BALANCE.name(), metric(
                AccountReportMetricKey.TOTAL_AVAILABLE_BALANCE,
                "Saldo disponible total",
                ReportFieldType.MONEY,
                "COALESCE(SUM(a.available_balance), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountReportMetricKey.TOTAL_HELD_BALANCE.name(), metric(
                AccountReportMetricKey.TOTAL_HELD_BALANCE,
                "Saldo retenido total",
                ReportFieldType.MONEY,
                "COALESCE(SUM(a.held_balance), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountReportMetricKey.TOTAL_BALANCE.name(), metric(
                AccountReportMetricKey.TOTAL_BALANCE,
                "Saldo total",
                ReportFieldType.MONEY,
                "COALESCE(SUM(a.available_balance + a.held_balance), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountReportMetricKey.AVERAGE_AVAILABLE_BALANCE.name(), metric(
                AccountReportMetricKey.AVERAGE_AVAILABLE_BALANCE,
                "Saldo disponible promedio",
                ReportFieldType.MONEY,
                "COALESCE(AVG(a.available_balance), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountReportMetricKey.MAX_AVAILABLE_BALANCE.name(), metric(
                AccountReportMetricKey.MAX_AVAILABLE_BALANCE,
                "Saldo disponible máximo",
                ReportFieldType.MONEY,
                "COALESCE(MAX(a.available_balance), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountReportMetricKey.MIN_AVAILABLE_BALANCE.name(), metric(
                AccountReportMetricKey.MIN_AVAILABLE_BALANCE,
                "Saldo disponible mínimo",
                ReportFieldType.MONEY,
                "COALESCE(MIN(a.available_balance), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        AccountReportColumnKey.ACCOUNT_ACCOUNT_NUMBER,
                        AccountReportColumnKey.ACCOUNT_ACCOUNT_NAME,
                        AccountReportColumnKey.ACCOUNT_CUSTOM_ALIAS,
                        AccountReportColumnKey.ACCOUNT_ACCOUNT_TYPE,
                        AccountReportColumnKey.ACCOUNT_CURRENCY,
                        AccountReportColumnKey.ACCOUNT_AVAILABLE_BALANCE,
                        AccountReportColumnKey.ACCOUNT_HELD_BALANCE,
                        AccountReportColumnKey.ACCOUNT_TOTAL_BALANCE,
                        AccountReportColumnKey.ACCOUNT_STATUS,
                        AccountReportColumnKey.ACCOUNT_ACTIVE,
                        AccountReportColumnKey.ACCOUNT_PRIMARY,
                        AccountReportColumnKey.ACCOUNT_OPENED_AT,
                        AccountReportColumnKey.USER_EMAIL,
                        AccountReportColumnKey.USER_FULL_NAME,
                        AccountReportColumnKey.USER_STATUS
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(
                        ReportSortTargetType.FIELD,
                        AccountReportFieldKey.ACCOUNT_OPENED_AT,
                        null,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        AccountReportGroupByKey.ACCOUNT_CURRENCY,
                        AccountReportGroupByKey.ACCOUNT_ACCOUNT_TYPE,
                        AccountReportGroupByKey.ACCOUNT_STATUS
                ),
                List.of(
                        AccountReportMetricKey.TOTAL_ACCOUNTS,
                        AccountReportMetricKey.ACTIVE_ACCOUNTS,
                        AccountReportMetricKey.PRIMARY_ACCOUNTS,
                        AccountReportMetricKey.TOTAL_AVAILABLE_BALANCE,
                        AccountReportMetricKey.TOTAL_HELD_BALANCE,
                        AccountReportMetricKey.TOTAL_BALANCE,
                        AccountReportMetricKey.AVERAGE_AVAILABLE_BALANCE
                ),
                List.of(
                        ReportVisualizationType.SUMMARY_CARDS,
                        ReportVisualizationType.TABLE
                ),
                List.of(new ReportSort(
                        ReportSortTargetType.METRIC,
                        null,
                        AccountReportMetricKey.TOTAL_BALANCE,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.ACCOUNTS,
                "Reporte de cuentas",
                "Permite generar reportes analíticos y gerenciales sobre cuentas financieras.",
                new ReportRoot("account", "tenant_accounts", "a"),
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                fields,
                relations,
                metrics,
                defaults,
                Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX),
                Set.of(
                        ReportVisualizationType.SUMMARY_CARDS,
                        ReportVisualizationType.TABLE,
                        ReportVisualizationType.BAR_CHART,
                        ReportVisualizationType.PIE_CHART
                ),
                new ReportExportOptions(
                        new ReportPdfExportOptions("landscape", "A4", true, true, 8),
                        new ReportXlsxExportOptions(true, true, true, true)
                ),
                new ReportLimits(5000, 15, 5, 10)
        );
    }

    private static ReportField field(
            Enum<?> key,
            String label,
            ReportFieldType type,
            String sqlExpression,
            List<ReportOperator> operators,
            List<Enum<?>> options,
            String relationKey,
            Set<ReportMode> allowedModes,
            boolean filterable,
            boolean columnable,
            boolean groupable,
            boolean sortable
    ) {
        return new ReportField(
                key,
                label,
                type,
                sqlExpression,
                operators,
                options,
                relationKey,
                allowedModes,
                filterable,
                columnable,
                groupable,
                sortable
        );
    }

    private static ReportMetric metric(
            Enum<?> key,
            String label,
            ReportFieldType type,
            String sqlExpression,
            Set<ReportMode> allowedModes
    ) {
        return new ReportMetric(key, label, type, sqlExpression, allowedModes);
    }

    private static List<Enum<?>> enumOptions(Enum<?>[] values) {
        return List.of(values);
    }
}
