package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;

import java.util.List;

public record ReportCatalogResponse(
        ReportMode mode,
        List<ReportCatalogItemResponse> reports
) {
}
