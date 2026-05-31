package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReportPaginationRequest(
        @NotNull
        @Min(1)
        Integer limit,
        @NotNull
        @Min(0)
        Integer offset
) {
}
