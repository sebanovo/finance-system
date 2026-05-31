package com.financesystem.finance_api.modules.tenant.reporting.application.export;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;

public record ReportExportArtifact(
        ReportOutput output,
        String fileName,
        String contentType,
        byte[] bytes
) {
}
