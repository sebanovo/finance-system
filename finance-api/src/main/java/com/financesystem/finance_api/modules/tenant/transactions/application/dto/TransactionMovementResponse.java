package com.financesystem.finance_api.modules.tenant.transactions.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionMovementResponse(
        UUID id,
        UUID accountId,
        String accountNumber,
        String accountDisplayName,
        String movementType,
        BigDecimal amount,
        String currency,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String description,
        Instant createdAt
) {
}
