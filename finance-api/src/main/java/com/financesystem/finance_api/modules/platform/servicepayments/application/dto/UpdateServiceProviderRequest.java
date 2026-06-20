package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import jakarta.validation.constraints.Size;

public record UpdateServiceProviderRequest(
        @Size(max = 150)
        String name,

        ServiceProviderCategory category,

        @Size(max = 100)
        String serviceCustomerCodeLabel
) {
}
