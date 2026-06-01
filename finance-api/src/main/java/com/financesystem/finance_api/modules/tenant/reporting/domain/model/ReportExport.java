package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ReportExport(
        UUID id,
        UUID executionId,
        String output,
        String fileName,
        String contentType,
        long fileSizeBytes,
        byte[] fileContent,
        Instant createdAt
) {
}
