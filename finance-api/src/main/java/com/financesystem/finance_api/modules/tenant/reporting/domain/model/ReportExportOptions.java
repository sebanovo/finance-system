package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

public record ReportExportOptions(
        ReportPdfExportOptions pdf,
        ReportXlsxExportOptions xlsx
) {
}
