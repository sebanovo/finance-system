package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitPeriod;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRuleType;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitScopeType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportColumnKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportMetricKey;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class LimitRulesReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(LimitRuleReportFieldKey.LIMIT_RULE_ID.name(), field(
                LimitRuleReportFieldKey.LIMIT_RULE_ID,
                "Rule ID",
                ReportFieldType.UUID,
                "r.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitRuleReportFieldKey.LIMIT_RULE_CODE.name(), field(
                LimitRuleReportFieldKey.LIMIT_RULE_CODE,
                "Code",
                ReportFieldType.TEXT,
                "r.code",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.LIMIT_RULE_NAME.name(), field(
                LimitRuleReportFieldKey.LIMIT_RULE_NAME,
                "Name",
                ReportFieldType.TEXT,
                "r.name",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.LIMIT_RULE_DESCRIPTION.name(), field(
                LimitRuleReportFieldKey.LIMIT_RULE_DESCRIPTION,
                "Description",
                ReportFieldType.TEXT,
                "r.description",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitRuleReportFieldKey.LIMIT_TYPE.name(), field(
                LimitRuleReportFieldKey.LIMIT_TYPE,
                "Limit type",
                ReportFieldType.ENUM,
                "r.limit_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(LimitRuleType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.SCOPE_TYPE.name(), field(
                LimitRuleReportFieldKey.SCOPE_TYPE,
                "Scope type",
                ReportFieldType.ENUM,
                "r.scope_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(LimitScopeType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.PERIOD.name(), field(
                LimitRuleReportFieldKey.PERIOD,
                "Period",
                ReportFieldType.ENUM,
                "r.period",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(LimitPeriod.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.TRANSACTION_TYPE.name(), field(
                LimitRuleReportFieldKey.TRANSACTION_TYPE,
                "Transaction type",
                ReportFieldType.ENUM,
                "r.transaction_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(TransactionType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.ACCOUNT_TYPE.name(), field(
                LimitRuleReportFieldKey.ACCOUNT_TYPE,
                "Account type",
                ReportFieldType.ENUM,
                "r.account_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(AccountType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.CURRENCY.name(), field(
                LimitRuleReportFieldKey.CURRENCY,
                "Currency",
                ReportFieldType.ENUM,
                "r.currency",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(CurrencyCode.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.MIN_AMOUNT.name(), field(
                LimitRuleReportFieldKey.MIN_AMOUNT,
                "Min amount",
                ReportFieldType.MONEY,
                "r.min_amount",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitRuleReportFieldKey.MAX_AMOUNT.name(), field(
                LimitRuleReportFieldKey.MAX_AMOUNT,
                "Max amount",
                ReportFieldType.MONEY,
                "r.max_amount",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitRuleReportFieldKey.MAX_COUNT.name(), field(
                LimitRuleReportFieldKey.MAX_COUNT,
                "Max count",
                ReportFieldType.NUMBER,
                "r.max_count",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitRuleReportFieldKey.ACTIVE.name(), field(
                LimitRuleReportFieldKey.ACTIVE,
                "Active",
                ReportFieldType.BOOLEAN,
                "r.active",
                List.of(ReportOperator.EQ),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.REQUIRE_REVIEW_EXCEED.name(), field(
                LimitRuleReportFieldKey.REQUIRE_REVIEW_EXCEED,
                "Require review exceed",
                ReportFieldType.BOOLEAN,
                "r.require_review_exceed",
                List.of(ReportOperator.EQ),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.CREATED_BY_USER_ID.name(), field(
                LimitRuleReportFieldKey.CREATED_BY_USER_ID,
                "Created by user ID",
                ReportFieldType.UUID,
                "r.created_by_user_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.UPDATED_BY_USER_ID.name(), field(
                LimitRuleReportFieldKey.UPDATED_BY_USER_ID,
                "Updated by user ID",
                ReportFieldType.UUID,
                "r.updated_by_user_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(LimitRuleReportFieldKey.CREATED_AT.name(), field(
                LimitRuleReportFieldKey.CREATED_AT,
                "Created at",
                ReportFieldType.DATETIME,
                "r.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(LimitRuleReportFieldKey.UPDATED_AT.name(), field(
                LimitRuleReportFieldKey.UPDATED_AT,
                "Updated at",
                ReportFieldType.DATETIME,
                "r.updated_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(LimitRuleReportMetricKey.TOTAL_RULES.name(), metric(
                LimitRuleReportMetricKey.TOTAL_RULES,
                "Total rules",
                ReportFieldType.NUMBER,
                "COUNT(r.id)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.ACTIVE_RULES.name(), metric(
                LimitRuleReportMetricKey.ACTIVE_RULES,
                "Active rules",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE r.active = TRUE)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.INACTIVE_RULES.name(), metric(
                LimitRuleReportMetricKey.INACTIVE_RULES,
                "Inactive rules",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE r.active = FALSE)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.REVIEW_REQUIRED_RULES.name(), metric(
                LimitRuleReportMetricKey.REVIEW_REQUIRED_RULES,
                "Rules requiring review",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE r.require_review_exceed = TRUE)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.AMOUNT_BASED_RULES.name(), metric(
                LimitRuleReportMetricKey.AMOUNT_BASED_RULES,
                "Amount based rules",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE r.limit_type IN ('PER_TRANSACTION_AMOUNT', 'DAILY_AMOUNT', 'MONTHLY_AMOUNT', 'MIN_AMOUNT', 'MAX_BALANCE'))",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.COUNT_BASED_RULES.name(), metric(
                LimitRuleReportMetricKey.COUNT_BASED_RULES,
                "Count based rules",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE r.limit_type IN ('DAILY_COUNT', 'MONTHLY_COUNT'))",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.TOTAL_MAX_COUNT.name(), metric(
                LimitRuleReportMetricKey.TOTAL_MAX_COUNT,
                "Total max count",
                ReportFieldType.NUMBER,
                "COALESCE(SUM(r.max_count), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.TOTAL_MAX_AMOUNT.name(), metric(
                LimitRuleReportMetricKey.TOTAL_MAX_AMOUNT,
                "Total max amount",
                ReportFieldType.MONEY,
                "COALESCE(SUM(r.max_amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.AVERAGE_MAX_AMOUNT.name(), metric(
                LimitRuleReportMetricKey.AVERAGE_MAX_AMOUNT,
                "Average max amount",
                ReportFieldType.MONEY,
                "COALESCE(AVG(r.max_amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.MAX_MAX_AMOUNT.name(), metric(
                LimitRuleReportMetricKey.MAX_MAX_AMOUNT,
                "Max max amount",
                ReportFieldType.MONEY,
                "COALESCE(MAX(r.max_amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(LimitRuleReportMetricKey.MIN_MAX_AMOUNT.name(), metric(
                LimitRuleReportMetricKey.MIN_MAX_AMOUNT,
                "Min max amount",
                ReportFieldType.MONEY,
                "COALESCE(MIN(r.max_amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        LimitRuleReportColumnKey.LIMIT_RULE_CODE,
                        LimitRuleReportColumnKey.LIMIT_RULE_NAME,
                        LimitRuleReportColumnKey.LIMIT_TYPE,
                        LimitRuleReportColumnKey.SCOPE_TYPE,
                        LimitRuleReportColumnKey.PERIOD,
                        LimitRuleReportColumnKey.TRANSACTION_TYPE,
                        LimitRuleReportColumnKey.ACCOUNT_TYPE,
                        LimitRuleReportColumnKey.CURRENCY,
                        LimitRuleReportColumnKey.MIN_AMOUNT,
                        LimitRuleReportColumnKey.MAX_AMOUNT,
                        LimitRuleReportColumnKey.MAX_COUNT,
                        LimitRuleReportColumnKey.ACTIVE,
                        LimitRuleReportColumnKey.REQUIRE_REVIEW_EXCEED,
                        LimitRuleReportColumnKey.CREATED_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(
                        ReportSortTargetType.FIELD,
                        LimitRuleReportFieldKey.CREATED_AT,
                        null,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        LimitRuleReportGroupByKey.LIMIT_TYPE,
                        LimitRuleReportGroupByKey.SCOPE_TYPE,
                        LimitRuleReportGroupByKey.PERIOD
                ),
                List.of(
                        LimitRuleReportMetricKey.TOTAL_RULES,
                        LimitRuleReportMetricKey.ACTIVE_RULES,
                        LimitRuleReportMetricKey.REVIEW_REQUIRED_RULES,
                        LimitRuleReportMetricKey.TOTAL_MAX_AMOUNT
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
                        LimitRuleReportMetricKey.TOTAL_RULES,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.LIMIT_RULES,
                "Reporte de reglas de limite",
                "Permite generar reportes analiticos y gerenciales sobre las reglas de limite del tenant.",
                new ReportRoot("limit_rule", "tenant_limit_rules", "r"),
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                fields,
                Map.of(),
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
                new ReportLimits(5000, 20, 8, 11)
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
