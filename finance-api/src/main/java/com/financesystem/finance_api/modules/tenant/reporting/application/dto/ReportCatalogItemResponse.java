package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;

public record ReportCatalogItemResponse(
        ReportType key,
        String label,
        String description
) {
}
