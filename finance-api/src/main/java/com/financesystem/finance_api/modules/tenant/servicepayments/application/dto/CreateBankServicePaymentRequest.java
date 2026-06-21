package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateBankServicePaymentRequest(
        @NotBlank
        @Size(max = 50)
        String sourceAccountNumber,

        @NotNull
        UUID providerId,

        @NotBlank
        @Size(max = 100)
        String serviceCustomerCode,

        @NotNull
        UUID billId,

        @NotBlank
        @Size(max = 150)
        String idempotencyKey
) {
}
