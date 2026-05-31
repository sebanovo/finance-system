package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record ReportTemplate(
        ReportType reportType,
        String title,
        String description,
        ReportRoot root,
        Set<ReportMode> supportedModes,
        Map<String, ReportField> fields,
        Map<String, ReportRelation> relations,
        Map<String, ReportMetric> metrics,
        Map<ReportMode, ReportModeDefaults> defaults,
        Set<ReportOutput> outputs,
        Set<ReportVisualizationType> visualizations,
        ReportExportOptions exportOptions,
        ReportLimits limits
) {

    public ReportTemplate {
        reportType = Objects.requireNonNull(reportType, "reportType must not be null");
        title = Objects.requireNonNull(title, "title must not be null");
        description = Objects.requireNonNull(description, "description must not be null");
        root = Objects.requireNonNull(root, "root must not be null");
        supportedModes = supportedModes == null ? Set.of() : Set.copyOf(supportedModes);
        fields = fields == null ? Map.of() : Map.copyOf(fields);
        relations = relations == null ? Map.of() : Map.copyOf(relations);
        metrics = metrics == null ? Map.of() : Map.copyOf(metrics);
        defaults = defaults == null ? Map.of() : Map.copyOf(defaults);
        outputs = outputs == null ? Set.of() : Set.copyOf(outputs);
        visualizations = visualizations == null ? Set.of() : Set.copyOf(visualizations);
        exportOptions = Objects.requireNonNull(exportOptions, "exportOptions must not be null");
        limits = Objects.requireNonNull(limits, "limits must not be null");
    }
}
