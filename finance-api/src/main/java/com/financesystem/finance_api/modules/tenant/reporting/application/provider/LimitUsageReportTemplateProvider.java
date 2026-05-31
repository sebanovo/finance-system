package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitPeriod;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRuleType;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitScopeType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportColumnKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportMetricKey;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class LimitUsageReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(LimitUsageReportFieldKey.LIMIT_USAGE_ID.name(), field(
                LimitUsageReportFieldKey.LIMIT_USAGE_ID,
                "Usage ID",
                ReportFieldType.UUID,
                "u.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitUsageReportFieldKey.LIMIT_RULE_ID.name(), field(
                LimitUsageReportFieldKey.LIMIT_RULE_ID,
                "Rule ID",
                ReportFieldType.UUID,
                "u.limit_rule_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitUsageReportFieldKey.LIMIT_RULE_CODE.name(), field(
                LimitUsageReportFieldKey.LIMIT_RULE_CODE,
                "Rule code",
                ReportFieldType.TEXT,
                "r.code",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "limit_rule",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.LIMIT_RULE_NAME.name(), field(
                LimitUsageReportFieldKey.LIMIT_RULE_NAME,
                "Rule name",
                ReportFieldType.TEXT,
                "r.name",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "limit_rule",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.LIMIT_TYPE.name(), field(
                LimitUsageReportFieldKey.LIMIT_TYPE,
                "Limit type",
                ReportFieldType.ENUM,
                "r.limit_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(LimitRuleType.values()),
                "limit_rule",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.SCOPE_TYPE.name(), field(
                LimitUsageReportFieldKey.SCOPE_TYPE,
                "Scope type",
                ReportFieldType.ENUM,
                "r.scope_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(LimitScopeType.values()),
                "limit_rule",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.PERIOD.name(), field(
                LimitUsageReportFieldKey.PERIOD,
                "Period",
                ReportFieldType.ENUM,
                "r.period",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(LimitPeriod.values()),
                "limit_rule",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.TRANSACTION_TYPE.name(), field(
                LimitUsageReportFieldKey.TRANSACTION_TYPE,
                "Transaction type",
                ReportFieldType.ENUM,
                "r.transaction_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType.values()),
                "limit_rule",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.ACCOUNT_TYPE.name(), field(
                LimitUsageReportFieldKey.ACCOUNT_TYPE,
                "Account type",
                ReportFieldType.ENUM,
                "r.account_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType.values()),
                "limit_rule",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.CURRENCY.name(), field(
                LimitUsageReportFieldKey.CURRENCY,
                "Currency",
                ReportFieldType.ENUM,
                "r.currency",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode.values()),
                "limit_rule",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.SCOPE_KEY.name(), field(
                LimitUsageReportFieldKey.SCOPE_KEY,
                "Scope key",
                ReportFieldType.TEXT,
                "u.scope_key",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.PERIOD_KEY.name(), field(
                LimitUsageReportFieldKey.PERIOD_KEY,
                "Period key",
                ReportFieldType.TEXT,
                "u.period_key",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitUsageReportFieldKey.TRANSACTION_COUNT.name(), field(
                LimitUsageReportFieldKey.TRANSACTION_COUNT,
                "Transaction count",
                ReportFieldType.NUMBER,
                "u.transaction_count",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitUsageReportFieldKey.TOTAL_AMOUNT.name(), field(
                LimitUsageReportFieldKey.TOTAL_AMOUNT,
                "Total amount",
                ReportFieldType.MONEY,
                "u.total_amount",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitUsageReportFieldKey.LAST_EVALUATED_AT.name(), field(
                LimitUsageReportFieldKey.LAST_EVALUATED_AT,
                "Last evaluated at",
                ReportFieldType.DATETIME,
                "u.last_evaluated_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitUsageReportFieldKey.CREATED_AT.name(), field(
                LimitUsageReportFieldKey.CREATED_AT,
                "Created at",
                ReportFieldType.DATETIME,
                "u.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitUsageReportFieldKey.UPDATED_AT.name(), field(
                LimitUsageReportFieldKey.UPDATED_AT,
                "Updated at",
                ReportFieldType.DATETIME,
                "u.updated_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));

        Map<String, ReportRelation> relations = new java.util.LinkedHashMap<>();
        relations.put("limit_rule", new ReportRelation(
                "limit_rule",
                "LEFT JOIN tenant_limit_rules r ON r.id = u.limit_rule_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(LimitUsageReportMetricKey.TOTAL_USAGES.name(), metric(
                LimitUsageReportMetricKey.TOTAL_USAGES,
                "Total usages",
                ReportFieldType.NUMBER,
                "COUNT(u.id)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitUsageReportMetricKey.TOTAL_TRANSACTION_COUNT.name(), metric(
                LimitUsageReportMetricKey.TOTAL_TRANSACTION_COUNT,
                "Total transaction count",
                ReportFieldType.NUMBER,
                "COALESCE(SUM(u.transaction_count), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitUsageReportMetricKey.TOTAL_AMOUNT.name(), metric(
                LimitUsageReportMetricKey.TOTAL_AMOUNT,
                "Total amount",
                ReportFieldType.MONEY,
                "COALESCE(SUM(u.total_amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitUsageReportMetricKey.AVERAGE_AMOUNT.name(), metric(
                LimitUsageReportMetricKey.AVERAGE_AMOUNT,
                "Average amount",
                ReportFieldType.MONEY,
                "COALESCE(AVG(u.total_amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitUsageReportMetricKey.MAX_AMOUNT.name(), metric(
                LimitUsageReportMetricKey.MAX_AMOUNT,
                "Max amount",
                ReportFieldType.MONEY,
                "COALESCE(MAX(u.total_amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitUsageReportMetricKey.MIN_AMOUNT.name(), metric(
                LimitUsageReportMetricKey.MIN_AMOUNT,
                "Min amount",
                ReportFieldType.MONEY,
                "COALESCE(MIN(u.total_amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitUsageReportMetricKey.AVERAGE_TRANSACTION_COUNT.name(), metric(
                LimitUsageReportMetricKey.AVERAGE_TRANSACTION_COUNT,
                "Average transaction count",
                ReportFieldType.NUMBER,
                "COALESCE(AVG(u.transaction_count), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitUsageReportMetricKey.MAX_TRANSACTION_COUNT.name(), metric(
                LimitUsageReportMetricKey.MAX_TRANSACTION_COUNT,
                "Max transaction count",
                ReportFieldType.NUMBER,
                "COALESCE(MAX(u.transaction_count), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitUsageReportMetricKey.MIN_TRANSACTION_COUNT.name(), metric(
                LimitUsageReportMetricKey.MIN_TRANSACTION_COUNT,
                "Min transaction count",
                ReportFieldType.NUMBER,
                "COALESCE(MIN(u.transaction_count), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        LimitUsageReportColumnKey.LIMIT_USAGE_ID,
                        LimitUsageReportColumnKey.LIMIT_RULE_CODE,
                        LimitUsageReportColumnKey.LIMIT_RULE_NAME,
                        LimitUsageReportColumnKey.LIMIT_TYPE,
                        LimitUsageReportColumnKey.SCOPE_TYPE,
                        LimitUsageReportColumnKey.PERIOD,
                        LimitUsageReportColumnKey.SCOPE_KEY,
                        LimitUsageReportColumnKey.PERIOD_KEY,
                        LimitUsageReportColumnKey.TRANSACTION_COUNT,
                        LimitUsageReportColumnKey.TOTAL_AMOUNT,
                        LimitUsageReportColumnKey.LAST_EVALUATED_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(
                        ReportSortTargetType.FIELD,
                        LimitUsageReportFieldKey.LAST_EVALUATED_AT,
                        null,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        LimitUsageReportGroupByKey.LIMIT_RULE_CODE,
                        LimitUsageReportGroupByKey.LIMIT_TYPE,
                        LimitUsageReportGroupByKey.SCOPE_TYPE,
                        LimitUsageReportGroupByKey.PERIOD
                ),
                List.of(
                        LimitUsageReportMetricKey.TOTAL_USAGES,
                        LimitUsageReportMetricKey.TOTAL_TRANSACTION_COUNT,
                        LimitUsageReportMetricKey.TOTAL_AMOUNT,
                        LimitUsageReportMetricKey.AVERAGE_AMOUNT
                ),
                List.of(
                        ReportVisualizationType.SUMMARY_CARDS,
                        ReportVisualizationType.TABLE,
                        ReportVisualizationType.BAR_CHART,
                        ReportVisualizationType.PIE_CHART
                ),
                List.of(new ReportSort(
                        ReportSortTargetType.METRIC,
                        null,
                        LimitUsageReportMetricKey.TOTAL_USAGES,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.LIMIT_USAGE,
                "Reporte de uso de limites",
                "Permite generar reportes analiticos y gerenciales sobre el uso de reglas de limite.",
                new ReportRoot("limit_usage", "tenant_limit_usages", "u"),
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
                new ReportLimits(5000, 17, 8, 9)
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
