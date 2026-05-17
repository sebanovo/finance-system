package com.financesystem.finance_api.modules.tenant.transactions.application.dto;

import com.financesystem.finance_api.modules.tenant.fx.application.dto.TransactionFxDetailResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String type,
        String status,
        String channel,
        BigDecimal amount,
        String currency,
        UUID sourceAccountId,
        String sourceAccountNumber,
        String sourceAccountDisplayName,
        UUID targetAccountId,
        String targetAccountNumber,
        String targetAccountDisplayName,
        String externalReference,
        String idempotencyKey,
        String description,
        String failureReason,
        UUID requestedByUserId,
        UUID approvedByUserId,
        Instant processedAt,
        Instant createdAt,
        Instant updatedAt,
        TransactionFxDetailResponse fxDetail,
        List<TransactionMovementResponse> movements
) {
}
