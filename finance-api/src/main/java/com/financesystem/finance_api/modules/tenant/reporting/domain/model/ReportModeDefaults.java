package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import java.util.List;

public record ReportModeDefaults(
        List<Enum<?>> columns,
        List<Enum<?>> groupBy,
        List<Enum<?>> metrics,
        List<ReportVisualizationType> visualizations,
        List<ReportSort> sort,
        List<ReportOutput> outputs
) {

    public ReportModeDefaults {
        columns = columns == null ? List.of() : List.copyOf(columns);
        groupBy = groupBy == null ? List.of() : List.copyOf(groupBy);
        metrics = metrics == null ? List.of() : List.copyOf(metrics);
        visualizations = visualizations == null ? List.of() : List.copyOf(visualizations);
        sort = sort == null ? List.of() : List.copyOf(sort);
        outputs = outputs == null ? List.of() : List.copyOf(outputs);
    }
}
