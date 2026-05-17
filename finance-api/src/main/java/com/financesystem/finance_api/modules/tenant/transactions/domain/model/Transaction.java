package com.financesystem.finance_api.modules.tenant.transactions.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Transaction(
        UUID id,
        TransactionType type,
        TransactionStatus status,
        TransactionChannel channel,
        BigDecimal amount,
        String currency,
        UUID sourceAccountId,
        UUID targetAccountId,
        String externalReference,
        String idempotencyKey,
        String description,
        String failureReason,
        String metadata,
        UUID parentTransactionId,
        UUID reversedTransactionId,
        UUID requestedByUserId,
        UUID approvedByUserId,
        Instant processedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
