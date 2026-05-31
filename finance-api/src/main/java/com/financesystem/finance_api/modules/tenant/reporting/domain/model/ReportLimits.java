package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

public record ReportLimits(
        int maxRows,
        int maxColumns,
        int maxGroupBy,
        int maxMetrics
) {
}
