package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeCalculationMode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeType;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.operationfees.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class OperationFeesReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(OperationFeeReportFieldKey.OPERATION_FEE_ID.name(), field(OperationFeeReportFieldKey.OPERATION_FEE_ID, "Operation fee ID", ReportFieldType.UUID, "f.id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(OperationFeeReportFieldKey.OPERATION_CODE.name(), field(OperationFeeReportFieldKey.OPERATION_CODE, "Operation code", ReportFieldType.ENUM, "f.operation_code", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(FxOperationCode.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(OperationFeeReportFieldKey.FEE_TYPE.name(), field(OperationFeeReportFieldKey.FEE_TYPE, "Fee type", ReportFieldType.ENUM, "f.fee_type", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(FeeType.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(OperationFeeReportFieldKey.FEE_VALUE.name(), field(OperationFeeReportFieldKey.FEE_VALUE, "Fee value", ReportFieldType.MONEY, "f.fee_value", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(OperationFeeReportFieldKey.CALCULATION_MODE.name(), field(OperationFeeReportFieldKey.CALCULATION_MODE, "Calculation mode", ReportFieldType.ENUM, "f.calculation_mode", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(FeeCalculationMode.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(OperationFeeReportFieldKey.ACTIVE.name(), field(OperationFeeReportFieldKey.ACTIVE, "Active", ReportFieldType.BOOLEAN, "f.active", List.of(ReportOperator.EQ), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(OperationFeeReportFieldKey.DESCRIPTION.name(), field(OperationFeeReportFieldKey.DESCRIPTION, "Description", ReportFieldType.TEXT, "f.description", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(OperationFeeReportFieldKey.CREATED_AT.name(), field(OperationFeeReportFieldKey.CREATED_AT, "Created at", ReportFieldType.DATETIME, "f.created_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(OperationFeeReportFieldKey.UPDATED_AT.name(), field(OperationFeeReportFieldKey.UPDATED_AT, "Updated at", ReportFieldType.DATETIME, "f.updated_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(OperationFeeReportMetricKey.TOTAL_FEES.name(), metric(OperationFeeReportMetricKey.TOTAL_FEES, "Total fees", ReportFieldType.NUMBER, "COUNT(f.id)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(OperationFeeReportMetricKey.ACTIVE_FEES.name(), metric(OperationFeeReportMetricKey.ACTIVE_FEES, "Active fees", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE f.active = TRUE)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(OperationFeeReportMetricKey.INACTIVE_FEES.name(), metric(OperationFeeReportMetricKey.INACTIVE_FEES, "Inactive fees", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE f.active = FALSE)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(OperationFeeReportMetricKey.NONE_FEES.name(), metric(OperationFeeReportMetricKey.NONE_FEES, "None fees", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE f.fee_type = 'NONE')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(OperationFeeReportMetricKey.FIXED_FEES.name(), metric(OperationFeeReportMetricKey.FIXED_FEES, "Fixed fees", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE f.fee_type = 'FIXED')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(OperationFeeReportMetricKey.PERCENTAGE_FEES.name(), metric(OperationFeeReportMetricKey.PERCENTAGE_FEES, "Percentage fees", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE f.fee_type = 'PERCENTAGE')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(OperationFeeReportMetricKey.AVERAGE_FEE_VALUE.name(), metric(OperationFeeReportMetricKey.AVERAGE_FEE_VALUE, "Average fee value", ReportFieldType.MONEY, "COALESCE(AVG(f.fee_value), 0)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(OperationFeeReportMetricKey.MAX_FEE_VALUE.name(), metric(OperationFeeReportMetricKey.MAX_FEE_VALUE, "Max fee value", ReportFieldType.MONEY, "COALESCE(MAX(f.fee_value), 0)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(OperationFeeReportMetricKey.MIN_FEE_VALUE.name(), metric(OperationFeeReportMetricKey.MIN_FEE_VALUE, "Min fee value", ReportFieldType.MONEY, "COALESCE(MIN(f.fee_value), 0)", Set.of(ReportMode.MANAGERIAL)));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        OperationFeeReportColumnKey.OPERATION_FEE_ID,
                        OperationFeeReportColumnKey.OPERATION_CODE,
                        OperationFeeReportColumnKey.FEE_TYPE,
                        OperationFeeReportColumnKey.FEE_VALUE,
                        OperationFeeReportColumnKey.CALCULATION_MODE,
                        OperationFeeReportColumnKey.ACTIVE,
                        OperationFeeReportColumnKey.DESCRIPTION,
                        OperationFeeReportColumnKey.CREATED_AT,
                        OperationFeeReportColumnKey.UPDATED_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(ReportSortTargetType.FIELD, OperationFeeReportFieldKey.CREATED_AT, null, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        OperationFeeReportGroupByKey.OPERATION_CODE,
                        OperationFeeReportGroupByKey.FEE_TYPE,
                        OperationFeeReportGroupByKey.CALCULATION_MODE,
                        OperationFeeReportGroupByKey.ACTIVE
                ),
                List.of(
                        OperationFeeReportMetricKey.TOTAL_FEES,
                        OperationFeeReportMetricKey.ACTIVE_FEES,
                        OperationFeeReportMetricKey.INACTIVE_FEES,
                        OperationFeeReportMetricKey.NONE_FEES,
                        OperationFeeReportMetricKey.FIXED_FEES,
                        OperationFeeReportMetricKey.PERCENTAGE_FEES,
                        OperationFeeReportMetricKey.AVERAGE_FEE_VALUE,
                        OperationFeeReportMetricKey.MAX_FEE_VALUE,
                        OperationFeeReportMetricKey.MIN_FEE_VALUE
                ),
                List.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                List.of(new ReportSort(ReportSortTargetType.METRIC, null, OperationFeeReportMetricKey.TOTAL_FEES, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.OPERATION_FEES,
                "Comisiones por operación",
                "Permite consultar y consolidar las comisiones configuradas por operación.",
                new ReportRoot("operation_fee", "tenant_operation_fees", "f"),
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                fields,
                Map.of(),
                metrics,
                defaults,
                Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX),
                Set.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                new ReportExportOptions(new ReportPdfExportOptions("landscape", "A4", true, true, 8), new ReportXlsxExportOptions(true, true, true, true)),
                new ReportLimits(4000, 9, 4, 6)
        );
    }

    private static ReportField field(Enum<?> key, String label, ReportFieldType type, String sqlExpression, List<ReportOperator> operators, List<Enum<?>> options, String relationKey, Set<ReportMode> allowedModes, boolean filterable, boolean columnable, boolean groupable, boolean sortable) {
        return new ReportField(key, label, type, sqlExpression, operators, options, relationKey, allowedModes, filterable, columnable, groupable, sortable);
    }

    private static ReportMetric metric(Enum<?> key, String label, ReportFieldType type, String sqlExpression, Set<ReportMode> allowedModes) {
        return new ReportMetric(key, label, type, sqlExpression, allowedModes);
    }

    private static List<Enum<?>> enumOptions(Enum<?>[] values) {
        return List.of(values);
    }
}
