package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

public record ReportPdfExportOptions(
        String orientation,
        String pageSize,
        boolean includeHeader,
        boolean includeAppliedFilters,
        int maxColumns
) {
}
