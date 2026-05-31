package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExecutionStatus;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReportExecutionDetailResponse(
        UUID id,
        String reportType,
        String reportTitle,
        String executionName,
        String mode,
        String requestedBySubject,
        String requestPayload,
        List<ReportOutput> outputs,
        int rowCount,
        ReportExecutionStatus status,
        String errorMessage,
        UUID sourceExecutionId,
        List<ReportExportSummaryResponse> exports,
        Instant createdAt
) {
}
