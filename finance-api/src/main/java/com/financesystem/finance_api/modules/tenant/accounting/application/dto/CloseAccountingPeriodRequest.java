package com.financesystem.finance_api.modules.tenant.accounting.application.dto;

import jakarta.validation.constraints.Size;

public record CloseAccountingPeriodRequest(
        @Size(max = 255)
        String reason
) {
}
