package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;

import java.util.List;

public record ReportModeDefaultsResponse(
        List<Enum<?>> columns,
        List<Enum<?>> groupBy,
        List<Enum<?>> metrics,
        List<ReportVisualizationType> visualizations,
        List<ReportSortResponse> sort,
        List<ReportOutput> outputs
) {
}
