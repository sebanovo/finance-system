package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.accounting.domain.model.JournalEntryStatus;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.JournalEntryType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journalentries.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class JournalEntriesReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(JournalEntryReportFieldKey.JOURNAL_ENTRY_ID.name(), field(
                JournalEntryReportFieldKey.JOURNAL_ENTRY_ID,
                "Journal entry ID",
                ReportFieldType.UUID,
                "j.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.ENTRY_NUMBER.name(), field(
                JournalEntryReportFieldKey.ENTRY_NUMBER,
                "Entry number",
                ReportFieldType.TEXT,
                "j.entry_number",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalEntryReportFieldKey.SOURCE_TRANSACTION_ID.name(), field(
                JournalEntryReportFieldKey.SOURCE_TRANSACTION_ID,
                "Source transaction ID",
                ReportFieldType.UUID,
                "j.source_transaction_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.PERIOD_ID.name(), field(
                JournalEntryReportFieldKey.PERIOD_ID,
                "Period ID",
                ReportFieldType.UUID,
                "j.period_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.PERIOD_CODE.name(), field(
                JournalEntryReportFieldKey.PERIOD_CODE,
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
        fields.put(JournalEntryReportFieldKey.PERIOD_TYPE.name(), field(
                JournalEntryReportFieldKey.PERIOD_TYPE,
                "Period type",
                ReportFieldType.ENUM,
                "p.period_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodType.values()),
                "period",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalEntryReportFieldKey.ENTRY_TYPE.name(), field(
                JournalEntryReportFieldKey.ENTRY_TYPE,
                "Entry type",
                ReportFieldType.ENUM,
                "j.entry_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(JournalEntryType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalEntryReportFieldKey.STATUS.name(), field(
                JournalEntryReportFieldKey.STATUS,
                "Status",
                ReportFieldType.ENUM,
                "j.status",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(JournalEntryStatus.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(JournalEntryReportFieldKey.DESCRIPTION.name(), field(
                JournalEntryReportFieldKey.DESCRIPTION,
                "Description",
                ReportFieldType.TEXT,
                "j.description",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.REFERENCE.name(), field(
                JournalEntryReportFieldKey.REFERENCE,
                "Reference",
                ReportFieldType.TEXT,
                "j.reference",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.TOTAL_DEBITS.name(), field(
                JournalEntryReportFieldKey.TOTAL_DEBITS,
                "Total debits",
                ReportFieldType.MONEY,
                "j.total_debits",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.TOTAL_CREDITS.name(), field(
                JournalEntryReportFieldKey.TOTAL_CREDITS,
                "Total credits",
                ReportFieldType.MONEY,
                "j.total_credits",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.BALANCE_DIFF.name(), field(
                JournalEntryReportFieldKey.BALANCE_DIFF,
                "Balance difference",
                ReportFieldType.MONEY,
                "(j.total_debits - j.total_credits)",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.POSTED_AT.name(), field(
                JournalEntryReportFieldKey.POSTED_AT,
                "Posted at",
                ReportFieldType.DATETIME,
                "j.posted_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.CREATED_AT.name(), field(
                JournalEntryReportFieldKey.CREATED_AT,
                "Created at",
                ReportFieldType.DATETIME,
                "j.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(JournalEntryReportFieldKey.UPDATED_AT.name(), field(
                JournalEntryReportFieldKey.UPDATED_AT,
                "Updated at",
                ReportFieldType.DATETIME,
                "j.updated_at",
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
        relations.put("period", new ReportRelation(
                "period",
                "LEFT JOIN tenant_accounting_periods p ON p.id = j.period_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(JournalEntryReportMetricKey.TOTAL_ENTRIES.name(), metric(
                JournalEntryReportMetricKey.TOTAL_ENTRIES,
                "Total entries",
                ReportFieldType.NUMBER,
                "COUNT(j.id)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.DRAFT_ENTRIES.name(), metric(
                JournalEntryReportMetricKey.DRAFT_ENTRIES,
                "Draft entries",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE j.status = 'DRAFT')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.POSTED_ENTRIES.name(), metric(
                JournalEntryReportMetricKey.POSTED_ENTRIES,
                "Posted entries",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE j.status = 'POSTED')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.REVERSED_ENTRIES.name(), metric(
                JournalEntryReportMetricKey.REVERSED_ENTRIES,
                "Reversed entries",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE j.status = 'REVERSED')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.VOID_ENTRIES.name(), metric(
                JournalEntryReportMetricKey.VOID_ENTRIES,
                "Void entries",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE j.status = 'VOID')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.TOTAL_DEBITS.name(), metric(
                JournalEntryReportMetricKey.TOTAL_DEBITS,
                "Total debits",
                ReportFieldType.MONEY,
                "COALESCE(SUM(j.total_debits), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.TOTAL_CREDITS.name(), metric(
                JournalEntryReportMetricKey.TOTAL_CREDITS,
                "Total credits",
                ReportFieldType.MONEY,
                "COALESCE(SUM(j.total_credits), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.NET_DIFFERENCE.name(), metric(
                JournalEntryReportMetricKey.NET_DIFFERENCE,
                "Net difference",
                ReportFieldType.MONEY,
                "COALESCE(SUM(j.total_debits - j.total_credits), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.AVERAGE_DEBITS.name(), metric(
                JournalEntryReportMetricKey.AVERAGE_DEBITS,
                "Average debits",
                ReportFieldType.MONEY,
                "COALESCE(AVG(j.total_debits), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.AVERAGE_CREDITS.name(), metric(
                JournalEntryReportMetricKey.AVERAGE_CREDITS,
                "Average credits",
                ReportFieldType.MONEY,
                "COALESCE(AVG(j.total_credits), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.MAX_DEBITS.name(), metric(
                JournalEntryReportMetricKey.MAX_DEBITS,
                "Max debits",
                ReportFieldType.MONEY,
                "COALESCE(MAX(j.total_debits), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(JournalEntryReportMetricKey.MAX_CREDITS.name(), metric(
                JournalEntryReportMetricKey.MAX_CREDITS,
                "Max credits",
                ReportFieldType.MONEY,
                "COALESCE(MAX(j.total_credits), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        JournalEntryReportColumnKey.ENTRY_NUMBER,
                        JournalEntryReportColumnKey.PERIOD_CODE,
                        JournalEntryReportColumnKey.PERIOD_TYPE,
                        JournalEntryReportColumnKey.ENTRY_TYPE,
                        JournalEntryReportColumnKey.STATUS,
                        JournalEntryReportColumnKey.TOTAL_DEBITS,
                        JournalEntryReportColumnKey.TOTAL_CREDITS,
                        JournalEntryReportColumnKey.BALANCE_DIFF,
                        JournalEntryReportColumnKey.POSTED_AT,
                        JournalEntryReportColumnKey.CREATED_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(
                        ReportSortTargetType.FIELD,
                        JournalEntryReportFieldKey.POSTED_AT,
                        null,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        JournalEntryReportGroupByKey.PERIOD_CODE,
                        JournalEntryReportGroupByKey.PERIOD_TYPE,
                        JournalEntryReportGroupByKey.ENTRY_TYPE,
                        JournalEntryReportGroupByKey.STATUS
                ),
                List.of(
                        JournalEntryReportMetricKey.TOTAL_ENTRIES,
                        JournalEntryReportMetricKey.POSTED_ENTRIES,
                        JournalEntryReportMetricKey.DRAFT_ENTRIES,
                        JournalEntryReportMetricKey.TOTAL_DEBITS,
                        JournalEntryReportMetricKey.TOTAL_CREDITS
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
                        JournalEntryReportMetricKey.TOTAL_ENTRIES,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.JOURNAL_ENTRIES,
                "Reporte de asientos contables",
                "Permite generar reportes analiticos y gerenciales sobre asientos contables.",
                new ReportRoot("journal_entry", "tenant_journal_entries", "j"),
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
                new ReportLimits(5000, 16, 4, 12)
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
