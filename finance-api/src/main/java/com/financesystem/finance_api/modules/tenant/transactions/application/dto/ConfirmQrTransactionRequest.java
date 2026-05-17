package com.financesystem.finance_api.modules.tenant.transactions.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ConfirmQrTransactionRequest(
        @NotNull
        UUID sourceAccountId,

        @NotBlank
        @Size(max = 150)
        String idempotencyKey
) {
}
