package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.accounting.domain.model.JournalEntryStatus;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.JournalEntryType;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.JournalLineType;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journallines.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class JournalLinesReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(JournalLineReportFieldKey.JOURNAL_LINE_ID.name(), field(
                JournalLineReportFieldKey.JOURNAL_LINE_ID,
                "Journal line ID",
                ReportFieldType.UUID,
                "l.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalLineReportFieldKey.JOURNAL_ENTRY_ID.name(), field(
                JournalLineReportFieldKey.JOURNAL_ENTRY_ID,
                "Journal entry ID",
                ReportFieldType.UUID,
                "l.journal_entry_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalLineReportFieldKey.ENTRY_NUMBER.name(), field(
                JournalLineReportFieldKey.ENTRY_NUMBER,
                "Entry number",
                ReportFieldType.TEXT,
                "j.entry_number",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "journal_entry",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalLineReportFieldKey.PERIOD_ID.name(), field(
                JournalLineReportFieldKey.PERIOD_ID,
                "Period ID",
                ReportFieldType.UUID,
                "j.period_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                "journal_entry",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalLineReportFieldKey.PERIOD_CODE.name(), field(
                JournalLineReportFieldKey.PERIOD_CODE,
                "Period code",
                ReportFieldType.TEXT,
                "p.period_code",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "period",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalLineReportFieldKey.PERIOD_TYPE.name(), field(
                JournalLineReportFieldKey.PERIOD_TYPE,
                "Period type",
                ReportFieldType.ENUM,
                "p.period_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(AccountingPeriodType.values()),
                "period",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalLineReportFieldKey.ENTRY_TYPE.name(), field(
                JournalLineReportFieldKey.ENTRY_TYPE,
                "Entry type",
                ReportFieldType.ENUM,
                "j.entry_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(JournalEntryType.values()),
                "journal_entry",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalLineReportFieldKey.ENTRY_STATUS.name(), field(
                JournalLineReportFieldKey.ENTRY_STATUS,
                "Entry status",
                ReportFieldType.ENUM,
                "j.status",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(JournalEntryStatus.values()),
                "journal_entry",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalLineReportFieldKey.LINE_NO.name(), field(
                JournalLineReportFieldKey.LINE_NO,
                "Line no",
                ReportFieldType.NUMBER,
                "l.line_no",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalLineReportFieldKey.ACCOUNT_CODE.name(), field(
                JournalLineReportFieldKey.ACCOUNT_CODE,
                "Account code",
                ReportFieldType.TEXT,
                "l.account_code",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalLineReportFieldKey.ACCOUNT_NAME.name(), field(
                JournalLineReportFieldKey.ACCOUNT_NAME,
                "Account name",
                ReportFieldType.TEXT,
                "l.account_name",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalLineReportFieldKey.LINE_TYPE.name(), field(
                JournalLineReportFieldKey.LINE_TYPE,
                "Line type",
                ReportFieldType.ENUM,
                "l.line_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(JournalLineType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalLineReportFieldKey.AMOUNT.name(), field(
                JournalLineReportFieldKey.AMOUNT,
                "Amount",
                ReportFieldType.MONEY,
                "l.amount",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalLineReportFieldKey.CURRENCY.name(), field(
                JournalLineReportFieldKey.CURRENCY,
                "Currency",
                ReportFieldType.ENUM,
                "l.currency",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalLineReportFieldKey.DESCRIPTION.name(), field(
                JournalLineReportFieldKey.DESCRIPTION,
                "Description",
                ReportFieldType.TEXT,
                "l.description",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalLineReportFieldKey.CREATED_AT.name(), field(
                JournalLineReportFieldKey.CREATED_AT,
                "Created at",
                ReportFieldType.DATETIME,
                "l.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalLineReportFieldKey.POSTED_AT.name(), field(
                JournalLineReportFieldKey.POSTED_AT,
                "Posted at",
                ReportFieldType.DATETIME,
                "j.posted_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                "journal_entry",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));

        Map<String, ReportRelation> relations = new java.util.LinkedHashMap<>();
        relations.put("journal_entry", new ReportRelation(
                "journal_entry",
                "LEFT JOIN tenant_journal_entries j ON j.id = l.journal_entry_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));
        relations.put("period", new ReportRelation(
                "period",
                "LEFT JOIN tenant_accounting_periods p ON p.id = j.period_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(JournalLineReportMetricKey.TOTAL_LINES.name(), metric(
                JournalLineReportMetricKey.TOTAL_LINES,
                "Total lines",
                ReportFieldType.NUMBER,
                "COUNT(l.id)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalLineReportMetricKey.DEBIT_LINES.name(), metric(
                JournalLineReportMetricKey.DEBIT_LINES,
                "Debit lines",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE l.line_type = 'DEBIT')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalLineReportMetricKey.CREDIT_LINES.name(), metric(
                JournalLineReportMetricKey.CREDIT_LINES,
                "Credit lines",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE l.line_type = 'CREDIT')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalLineReportMetricKey.TOTAL_AMOUNT.name(), metric(
                JournalLineReportMetricKey.TOTAL_AMOUNT,
                "Total amount",
                ReportFieldType.MONEY,
                "COALESCE(SUM(l.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalLineReportMetricKey.AVERAGE_AMOUNT.name(), metric(
                JournalLineReportMetricKey.AVERAGE_AMOUNT,
                "Average amount",
                ReportFieldType.MONEY,
                "COALESCE(AVG(l.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalLineReportMetricKey.MAX_AMOUNT.name(), metric(
                JournalLineReportMetricKey.MAX_AMOUNT,
                "Max amount",
                ReportFieldType.MONEY,
                "COALESCE(MAX(l.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalLineReportMetricKey.MIN_AMOUNT.name(), metric(
                JournalLineReportMetricKey.MIN_AMOUNT,
                "Min amount",
                ReportFieldType.MONEY,
                "COALESCE(MIN(l.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalLineReportMetricKey.TOTAL_POSTED_ENTRIES.name(), metric(
                JournalLineReportMetricKey.TOTAL_POSTED_ENTRIES,
                "Posted entries",
                ReportFieldType.NUMBER,
                "COUNT(DISTINCT j.id) FILTER (WHERE j.status = 'POSTED')",
                Set.of(ReportMode.MANAGERIAL)
        ));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        JournalLineReportColumnKey.ENTRY_NUMBER,
                        JournalLineReportColumnKey.PERIOD_CODE,
                        JournalLineReportColumnKey.PERIOD_TYPE,
                        JournalLineReportColumnKey.ENTRY_TYPE,
                        JournalLineReportColumnKey.ENTRY_STATUS,
                        JournalLineReportColumnKey.LINE_NO,
                        JournalLineReportColumnKey.ACCOUNT_CODE,
                        JournalLineReportColumnKey.ACCOUNT_NAME,
                        JournalLineReportColumnKey.LINE_TYPE,
                        JournalLineReportColumnKey.AMOUNT,
                        JournalLineReportColumnKey.CURRENCY,
                        JournalLineReportColumnKey.CREATED_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(
                        ReportSortTargetType.FIELD,
                        JournalLineReportFieldKey.CREATED_AT,
                        null,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        JournalLineReportGroupByKey.PERIOD_CODE,
                        JournalLineReportGroupByKey.PERIOD_TYPE,
                        JournalLineReportGroupByKey.ENTRY_TYPE,
                        JournalLineReportGroupByKey.LINE_TYPE,
                        JournalLineReportGroupByKey.CURRENCY
                ),
                List.of(
                        JournalLineReportMetricKey.TOTAL_LINES,
                        JournalLineReportMetricKey.DEBIT_LINES,
                        JournalLineReportMetricKey.CREDIT_LINES,
                        JournalLineReportMetricKey.TOTAL_AMOUNT
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
                        JournalLineReportMetricKey.TOTAL_LINES,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.JOURNAL_LINES,
                "Reporte de lineas contables",
                "Permite generar reportes analiticos y gerenciales sobre lineas de asiento contable.",
                new ReportRoot("journal_line", "tenant_journal_lines", "l"),
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
                new ReportLimits(5000, 17, 5, 8)
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
