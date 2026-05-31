package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;

import java.util.List;

public record RerunReportExecutionRequest(
        List<ReportOutput> outputs
) {
}
