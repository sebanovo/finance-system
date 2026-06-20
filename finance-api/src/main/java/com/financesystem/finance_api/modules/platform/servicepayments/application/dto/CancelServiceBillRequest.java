package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import jakarta.validation.constraints.Size;

public record CancelServiceBillRequest(
        @Size(max = 255)
        String reason
) {
}
