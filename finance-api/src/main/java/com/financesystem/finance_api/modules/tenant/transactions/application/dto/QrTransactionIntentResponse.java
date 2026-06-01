package com.financesystem.finance_api.modules.tenant.transactions.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record QrTransactionIntentResponse(
        UUID id,
        String status,
        String channel,
        BigDecimal amount,
        String currency,
        UUID targetAccountId,
        String externalReference,
        String description,
        String idempotencyKey,
        UUID confirmedTransactionId,
        Instant expiresAt,
        Instant confirmedAt,
        Instant cancelledAt,
        UUID cancelledByUserId,
        UUID payerAccountId,
        BigDecimal paidAmount,
        String paidCurrency,
        String qrPayload,
        String qrSignature,
        Instant createdAt,
        Instant updatedAt
) {
}
