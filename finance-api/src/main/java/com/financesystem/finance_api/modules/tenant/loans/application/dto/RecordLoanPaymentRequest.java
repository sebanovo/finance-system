package com.financesystem.finance_api.modules.tenant.loans.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RecordLoanPaymentRequest(
        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 16, fraction = 2)
        BigDecimal amount,

        @Size(max = 150)
        String idempotencyKey
) {
}
