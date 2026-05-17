package com.financesystem.finance_api.modules.tenant.transactions.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record QrTransactionIntent(
        UUID id,
        QrTransactionIntentStatus status,
        TransactionChannel channel,
        BigDecimal amount,
        String currency,
        UUID targetAccountId,
        String externalReference,
        String description,
        String idempotencyKey,
        UUID confirmedTransactionId,
        UUID requestedByUserId,
        Instant confirmedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
