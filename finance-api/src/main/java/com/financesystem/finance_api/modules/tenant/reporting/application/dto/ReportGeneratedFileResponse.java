package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;

public record ReportGeneratedFileResponse(
        ReportOutput output,
        String fileName,
        String contentType,
        String base64,
        long fileSizeBytes
) {
}
