package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import java.time.Instant;

public record ReportExportSummaryResponse(
        String output,
        String fileName,
        String contentType,
        long fileSizeBytes,
        Instant createdAt
) {
}
