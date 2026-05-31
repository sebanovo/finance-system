package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

public record ReportXlsxExportOptions(
        boolean includeHeader,
        boolean includeAppliedFiltersSheet,
        boolean freezeHeader,
        boolean autoSizeColumns
) {
}
