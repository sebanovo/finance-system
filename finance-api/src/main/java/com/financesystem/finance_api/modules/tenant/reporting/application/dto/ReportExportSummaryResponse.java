package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ReportExportSummaryResponse(
        UUID id,
        String output,
        String fileName,
        String contentType,
        long fileSizeBytes,
        Instant createdAt
) {
}
