package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;

import java.util.List;
import java.util.Map;

public record ReportResultResponse(
        ReportType reportType,
        ReportMode mode,
        List<ReportOutput> outputs,
        ReportHeaderResponse header,
        List<ReportColumnResponse> columns,
        List<Map<String, Object>> rows,
        List<ReportGeneratedFileResponse> files,
        ReportResultMetadataResponse metadata
) {
}
