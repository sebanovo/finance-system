package com.financesystem.finance_api.modules.tenant.transactions.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReversalTransactionRequest(
        @NotBlank
        @Size(max = 255)
        String reason,

        @NotBlank
        @Size(max = 150)
        String idempotencyKey
) {
}
