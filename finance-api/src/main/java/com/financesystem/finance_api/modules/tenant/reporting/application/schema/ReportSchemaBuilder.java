package com.financesystem.finance_api.modules.tenant.reporting.application.schema;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReportSchemaBuilder {

    public ReportSchemaResponse build(ReportTemplate template, ReportMode mode) {
        Objects.requireNonNull(template, "template must not be null");
        Objects.requireNonNull(mode, "mode must not be null");

        if (!template.supportedModes().contains(mode)) {
            throw new ReportValidationException(
                    "Report type " + template.reportType().name() + " does not support mode " + mode.name()
            );
        }

        List<ReportFieldResponse> filters = template.fields().values().stream()
                .filter(field -> supportsMode(field.allowedModes(), mode))
                .filter(field -> field.filterable())
                .map(this::toFieldResponse)
                .toList();

        List<ReportFieldResponse> columns = template.fields().values().stream()
                .filter(field -> supportsMode(field.allowedModes(), mode))
                .filter(field -> field.columnable())
                .map(this::toFieldResponse)
                .toList();

        List<ReportFieldResponse> groupBy = template.fields().values().stream()
                .filter(field -> supportsMode(field.allowedModes(), mode))
                .filter(field -> field.groupable())
                .map(this::toFieldResponse)
                .toList();

        List<ReportMetricResponse> metrics = template.metrics().values().stream()
                .filter(metric -> supportsMode(metric.allowedModes(), mode))
                .map(metric -> new ReportMetricResponse(metric.key(), metric.label(), metric.type()))
                .toList();

        List<ReportModeDefaultsResponse> defaults = buildDefaults(template.defaults(), mode);
        ReportExportOptionsResponse exportOptions = toExportOptionsResponse(template.exportOptions());

        return new ReportSchemaResponse(
                template.reportType(),
                mode,
                template.title(),
                template.description(),
                filters,
                columns,
                groupBy,
                metrics,
                new ArrayList<>(template.visualizations()),
                defaults,
                new ArrayList<>(template.outputs()),
                exportOptions,
                toLimitsResponse(template.limits())
        );
    }

    public ReportCatalogResponse buildCatalog(ReportMode mode, Collection<ReportTemplate> templates) {
        Objects.requireNonNull(mode, "mode must not be null");
        Objects.requireNonNull(templates, "templates must not be null");

        List<ReportCatalogItemResponse> items = templates.stream()
                .filter(template -> template.supportedModes().contains(mode))
                .map(template -> new ReportCatalogItemResponse(
                        template.reportType(),
                        template.title(),
                        template.description()
                ))
                .toList();

        return new ReportCatalogResponse(mode, items);
    }

    private boolean supportsMode(Set<ReportMode> allowedModes, ReportMode mode) {
        return allowedModes == null || allowedModes.isEmpty() || allowedModes.contains(mode);
    }

    private ReportFieldResponse toFieldResponse(ReportField field) {
        return new ReportFieldResponse(
                field.key(),
                field.label(),
                field.type(),
                field.operators(),
                field.options()
        );
    }

    private List<ReportModeDefaultsResponse> buildDefaults(
            Map<ReportMode, ReportModeDefaults> defaults,
            ReportMode mode
    ) {
        if (defaults == null || defaults.isEmpty()) {
            return List.of();
        }

        ReportModeDefaults modeDefaults = defaults.get(mode);
        if (modeDefaults == null) {
            return List.of();
        }

        return List.of(new ReportModeDefaultsResponse(
                modeDefaults.columns(),
                modeDefaults.groupBy(),
                modeDefaults.metrics(),
                modeDefaults.visualizations(),
                modeDefaults.sort().stream()
                        .map(sort -> new ReportSortResponse(
                                sort.targetType(),
                                sort.field(),
                                sort.metric(),
                                sort.direction()
                        ))
                        .toList(),
                modeDefaults.outputs()
        ));
    }

    private ReportLimitsResponse toLimitsResponse(ReportLimits limits) {
        return new ReportLimitsResponse(
                limits.maxRows(),
                limits.maxColumns(),
                limits.maxGroupBy(),
                limits.maxMetrics()
        );
    }

    private ReportExportOptionsResponse toExportOptionsResponse(ReportExportOptions exportOptions) {
        ReportPdfExportOptions pdf = exportOptions.pdf();
        ReportXlsxExportOptions xlsx = exportOptions.xlsx();

        return new ReportExportOptionsResponse(
                pdf.orientation(),
                pdf.pageSize(),
                pdf.includeHeader(),
                pdf.includeAppliedFilters(),
                xlsx.includeHeader(),
                xlsx.includeAppliedFiltersSheet(),
                xlsx.freezeHeader(),
                xlsx.autoSizeColumns()
        );
    }
}
