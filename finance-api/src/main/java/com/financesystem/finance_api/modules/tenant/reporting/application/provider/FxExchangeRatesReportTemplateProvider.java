package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.fxexchangerates.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FxExchangeRatesReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(FxExchangeRateReportFieldKey.FX_RATE_ID.name(), field(
                FxExchangeRateReportFieldKey.FX_RATE_ID, "FX rate ID", ReportFieldType.UUID, "r.id",
                List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true
        ));
        fields.put(FxExchangeRateReportFieldKey.SOURCE_CURRENCY.name(), field(
                FxExchangeRateReportFieldKey.SOURCE_CURRENCY, "Source currency", ReportFieldType.ENUM, "r.source_currency",
                List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(CurrencyCode.values()), null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true
        ));
        fields.put(FxExchangeRateReportFieldKey.TARGET_CURRENCY.name(), field(
                FxExchangeRateReportFieldKey.TARGET_CURRENCY, "Target currency", ReportFieldType.ENUM, "r.target_currency",
                List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(CurrencyCode.values()), null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true
        ));
        fields.put(FxExchangeRateReportFieldKey.RATE.name(), field(
                FxExchangeRateReportFieldKey.RATE, "Rate", ReportFieldType.MONEY, "r.rate",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN), List.of(), null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true
        ));
        fields.put(FxExchangeRateReportFieldKey.ACTIVE.name(), field(
                FxExchangeRateReportFieldKey.ACTIVE, "Active", ReportFieldType.BOOLEAN, "r.active",
                List.of(ReportOperator.EQ), List.of(), null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true
        ));
        fields.put(FxExchangeRateReportFieldKey.DESCRIPTION.name(), field(
                FxExchangeRateReportFieldKey.DESCRIPTION, "Description", ReportFieldType.TEXT, "r.description",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true
        ));
        fields.put(FxExchangeRateReportFieldKey.CREATED_AT.name(), field(
                FxExchangeRateReportFieldKey.CREATED_AT, "Created at", ReportFieldType.DATETIME, "r.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN), List.of(), null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true
        ));
        fields.put(FxExchangeRateReportFieldKey.UPDATED_AT.name(), field(
                FxExchangeRateReportFieldKey.UPDATED_AT, "Updated at", ReportFieldType.DATETIME, "r.updated_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN), List.of(), null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true
        ));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(FxExchangeRateReportMetricKey.TOTAL_RATES.name(), metric(FxExchangeRateReportMetricKey.TOTAL_RATES, "Total rates", ReportFieldType.NUMBER, "COUNT(r.id)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(FxExchangeRateReportMetricKey.ACTIVE_RATES.name(), metric(FxExchangeRateReportMetricKey.ACTIVE_RATES, "Active rates", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE r.active = TRUE)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(FxExchangeRateReportMetricKey.INACTIVE_RATES.name(), metric(FxExchangeRateReportMetricKey.INACTIVE_RATES, "Inactive rates", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE r.active = FALSE)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(FxExchangeRateReportMetricKey.AVERAGE_RATE.name(), metric(FxExchangeRateReportMetricKey.AVERAGE_RATE, "Average rate", ReportFieldType.MONEY, "COALESCE(AVG(r.rate), 0)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(FxExchangeRateReportMetricKey.MAX_RATE.name(), metric(FxExchangeRateReportMetricKey.MAX_RATE, "Max rate", ReportFieldType.MONEY, "COALESCE(MAX(r.rate), 0)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(FxExchangeRateReportMetricKey.MIN_RATE.name(), metric(FxExchangeRateReportMetricKey.MIN_RATE, "Min rate", ReportFieldType.MONEY, "COALESCE(MIN(r.rate), 0)", Set.of(ReportMode.MANAGERIAL)));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        FxExchangeRateReportColumnKey.FX_RATE_ID,
                        FxExchangeRateReportColumnKey.SOURCE_CURRENCY,
                        FxExchangeRateReportColumnKey.TARGET_CURRENCY,
                        FxExchangeRateReportColumnKey.RATE,
                        FxExchangeRateReportColumnKey.ACTIVE,
                        FxExchangeRateReportColumnKey.DESCRIPTION,
                        FxExchangeRateReportColumnKey.CREATED_AT,
                        FxExchangeRateReportColumnKey.UPDATED_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(ReportSortTargetType.FIELD, FxExchangeRateReportFieldKey.CREATED_AT, null, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        FxExchangeRateReportGroupByKey.SOURCE_CURRENCY,
                        FxExchangeRateReportGroupByKey.TARGET_CURRENCY,
                        FxExchangeRateReportGroupByKey.ACTIVE
                ),
                List.of(
                        FxExchangeRateReportMetricKey.TOTAL_RATES,
                        FxExchangeRateReportMetricKey.ACTIVE_RATES,
                        FxExchangeRateReportMetricKey.INACTIVE_RATES,
                        FxExchangeRateReportMetricKey.AVERAGE_RATE,
                        FxExchangeRateReportMetricKey.MAX_RATE,
                        FxExchangeRateReportMetricKey.MIN_RATE
                ),
                List.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                List.of(new ReportSort(ReportSortTargetType.METRIC, null, FxExchangeRateReportMetricKey.TOTAL_RATES, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.FX_EXCHANGE_RATES,
                "Tipos de cambio",
                "Permite consultar y consolidar los tipos de cambio configurados.",
                new ReportRoot("fx_exchange_rate", "tenant_fx_exchange_rates", "r"),
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                fields,
                Map.of(),
                metrics,
                defaults,
                Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX),
                Set.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                new ReportExportOptions(new ReportPdfExportOptions("landscape", "A4", true, true, 8), new ReportXlsxExportOptions(true, true, true, true)),
                new ReportLimits(4000, 8, 4, 6)
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
