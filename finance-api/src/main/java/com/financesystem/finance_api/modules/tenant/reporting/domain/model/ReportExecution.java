package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ReportExecution(
        UUID id,
        String reportType,
        String reportTitle,
        String executionName,
        String mode,
        String requestedBySubject,
        String requestPayload,
        String outputs,
        int rowCount,
        UUID sourceExecutionId,
        ReportExecutionStatus status,
        String errorMessage,
        Instant createdAt
) {
}
