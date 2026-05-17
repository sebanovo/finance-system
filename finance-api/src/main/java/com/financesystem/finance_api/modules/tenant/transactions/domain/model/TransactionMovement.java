package com.financesystem.finance_api.modules.tenant.transactions.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionMovement(
        UUID id,
        UUID transactionId,
        UUID accountId,
        TransactionMovementType movementType,
        BigDecimal amount,
        String currency,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String description,
        Instant createdAt
) {
}
