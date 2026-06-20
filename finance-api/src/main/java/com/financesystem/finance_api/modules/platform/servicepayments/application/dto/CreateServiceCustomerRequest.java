package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateServiceCustomerRequest(
        @NotNull
        UUID providerId,

        @NotBlank
        @Size(max = 100)
        String serviceCustomerCode,

        @NotBlank
        @Size(max = 150)
        String customerName
) {
}
