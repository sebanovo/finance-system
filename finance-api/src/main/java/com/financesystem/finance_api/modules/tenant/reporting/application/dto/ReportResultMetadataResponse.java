package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

public record ReportResultMetadataResponse(
        int rowCount,
        Integer limit,
        Integer offset
) {
}
