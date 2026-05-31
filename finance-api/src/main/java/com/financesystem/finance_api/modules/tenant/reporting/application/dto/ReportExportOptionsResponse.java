package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

public record ReportExportOptionsResponse(
        String pdfOrientation,
        String pdfPageSize,
        boolean pdfIncludeHeader,
        boolean pdfIncludeAppliedFilters,
        boolean xlsxIncludeHeader,
        boolean xlsxIncludeAppliedFiltersSheet,
        boolean xlsxFreezeHeader,
        boolean xlsxAutoSizeColumns
) {
}
