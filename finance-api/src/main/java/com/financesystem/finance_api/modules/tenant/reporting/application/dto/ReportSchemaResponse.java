package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;

import java.util.List;

public record ReportSchemaResponse(
        ReportType reportType,
        ReportMode mode,
        String title,
        String description,
        List<ReportFieldResponse> filters,
        List<ReportFieldResponse> columns,
        List<ReportFieldResponse> groupBy,
        List<ReportMetricResponse> metrics,
        List<ReportVisualizationType> visualizations,
        List<ReportModeDefaultsResponse> defaults,
        List<ReportOutput> outputs,
        ReportExportOptionsResponse exportOptions,
        ReportLimitsResponse limits
) {
}
