package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateServiceProviderRequest(
        @NotBlank
        @Size(max = 80)
        String code,

        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        ServiceProviderCategory category,

        @Size(max = 100)
        String serviceCustomerCodeLabel
) {
}
