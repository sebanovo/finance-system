package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodStatus;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportColumnKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportMetricKey;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AccountingPeriodsReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(AccountingPeriodReportFieldKey.PERIOD_ID.name(), field(
                AccountingPeriodReportFieldKey.PERIOD_ID,
                "Period ID",
                ReportFieldType.UUID,
                "p.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountingPeriodReportFieldKey.PERIOD_CODE.name(), field(
                AccountingPeriodReportFieldKey.PERIOD_CODE,
                "Period code",
                ReportFieldType.TEXT,
                "p.period_code",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountingPeriodReportFieldKey.PERIOD_TYPE.name(), field(
                AccountingPeriodReportFieldKey.PERIOD_TYPE,
                "Period type",
                ReportFieldType.ENUM,
                "p.period_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(AccountingPeriodType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountingPeriodReportFieldKey.STATUS.name(), field(
                AccountingPeriodReportFieldKey.STATUS,
                "Status",
                ReportFieldType.ENUM,
                "p.status",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(AccountingPeriodStatus.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(AccountingPeriodReportFieldKey.START_DATE.name(), field(
                AccountingPeriodReportFieldKey.START_DATE,
                "Start date",
                ReportFieldType.DATE,
                "p.start_date",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountingPeriodReportFieldKey.END_DATE.name(), field(
                AccountingPeriodReportFieldKey.END_DATE,
                "End date",
                ReportFieldType.DATE,
                "p.end_date",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountingPeriodReportFieldKey.CLOSED_AT.name(), field(
                AccountingPeriodReportFieldKey.CLOSED_AT,
                "Closed at",
                ReportFieldType.DATETIME,
                "p.closed_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountingPeriodReportFieldKey.DESCRIPTION.name(), field(
                AccountingPeriodReportFieldKey.DESCRIPTION,
                "Description",
                ReportFieldType.TEXT,
                "p.description",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountingPeriodReportFieldKey.CREATED_AT.name(), field(
                AccountingPeriodReportFieldKey.CREATED_AT,
                "Created at",
                ReportFieldType.DATETIME,
                "p.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(AccountingPeriodReportFieldKey.UPDATED_AT.name(), field(
                AccountingPeriodReportFieldKey.UPDATED_AT,
                "Updated at",
                ReportFieldType.DATETIME,
                "p.updated_at",
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
        metrics.put(AccountingPeriodReportMetricKey.TOTAL_PERIODS.name(), metric(
                AccountingPeriodReportMetricKey.TOTAL_PERIODS,
                "Total periods",
                ReportFieldType.NUMBER,
                "COUNT(p.id)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountingPeriodReportMetricKey.OPEN_PERIODS.name(), metric(
                AccountingPeriodReportMetricKey.OPEN_PERIODS,
                "Open periods",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE p.status = 'OPEN')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountingPeriodReportMetricKey.CLOSED_PERIODS.name(), metric(
                AccountingPeriodReportMetricKey.CLOSED_PERIODS,
                "Closed periods",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE p.status = 'CLOSED')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountingPeriodReportMetricKey.ARCHIVED_PERIODS.name(), metric(
                AccountingPeriodReportMetricKey.ARCHIVED_PERIODS,
                "Archived periods",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE p.status = 'ARCHIVED')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountingPeriodReportMetricKey.TOTAL_RANGE_DAYS.name(), metric(
                AccountingPeriodReportMetricKey.TOTAL_RANGE_DAYS,
                "Total range days",
                ReportFieldType.NUMBER,
                "COALESCE(SUM((p.end_date - p.start_date) + 1), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountingPeriodReportMetricKey.MAX_RANGE_DAYS.name(), metric(
                AccountingPeriodReportMetricKey.MAX_RANGE_DAYS,
                "Max range days",
                ReportFieldType.NUMBER,
                "COALESCE(MAX((p.end_date - p.start_date) + 1), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(AccountingPeriodReportMetricKey.MIN_RANGE_DAYS.name(), metric(
                AccountingPeriodReportMetricKey.MIN_RANGE_DAYS,
                "Min range days",
                ReportFieldType.NUMBER,
                "COALESCE(MIN((p.end_date - p.start_date) + 1), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        AccountingPeriodReportColumnKey.PERIOD_CODE,
                        AccountingPeriodReportColumnKey.PERIOD_TYPE,
                        AccountingPeriodReportColumnKey.STATUS,
                        AccountingPeriodReportColumnKey.START_DATE,
                        AccountingPeriodReportColumnKey.END_DATE,
                        AccountingPeriodReportColumnKey.CLOSED_AT,
                        AccountingPeriodReportColumnKey.DESCRIPTION,
                        AccountingPeriodReportColumnKey.CREATED_AT,
                        AccountingPeriodReportColumnKey.UPDATED_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(
                        ReportSortTargetType.FIELD,
                        AccountingPeriodReportFieldKey.CREATED_AT,
                        null,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        AccountingPeriodReportGroupByKey.PERIOD_TYPE,
                        AccountingPeriodReportGroupByKey.STATUS
                ),
                List.of(
                        AccountingPeriodReportMetricKey.TOTAL_PERIODS,
                        AccountingPeriodReportMetricKey.OPEN_PERIODS,
                        AccountingPeriodReportMetricKey.CLOSED_PERIODS,
                        AccountingPeriodReportMetricKey.ARCHIVED_PERIODS
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
                        AccountingPeriodReportMetricKey.TOTAL_PERIODS,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.ACCOUNTING_PERIODS,
                "Reporte de periodos contables",
                "Permite generar reportes analiticos y gerenciales sobre los periodos contables.",
                new ReportRoot("accounting_period", "tenant_accounting_periods", "p"),
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
                new ReportLimits(5000, 10, 2, 4)
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
        return new ReportField(key, label, type, sqlExpression, operators, options, relationKey, allowedModes, filterable, columnable, groupable, sortable);
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
