package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

public record ReportLimitsResponse(
        int maxRows,
        int maxColumns,
        int maxGroupBy,
        int maxMetrics
) {
}
