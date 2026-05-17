package com.financesystem.finance_api.modules.tenant.transactions.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.AdjustmentDirection;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAdjustmentTransactionRequest(
        @NotNull
        UUID accountId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,

        @NotNull
        CurrencyCode currency,

        @NotNull
        AdjustmentDirection direction,

        @NotBlank
        @Size(max = 255)
        String reason,

        @Size(max = 150)
        String externalReference,

        @NotBlank
        @Size(max = 150)
        String idempotencyKey
) {
}
