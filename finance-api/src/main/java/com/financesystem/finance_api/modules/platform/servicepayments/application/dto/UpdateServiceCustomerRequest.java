package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import jakarta.validation.constraints.Size;

public record UpdateServiceCustomerRequest(
        @Size(max = 150)
        String customerName,

        ServiceCustomerStatus status
) {
}
