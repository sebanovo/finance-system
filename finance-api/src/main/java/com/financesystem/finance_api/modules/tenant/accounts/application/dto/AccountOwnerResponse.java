package com.financesystem.finance_api.modules.tenant.accounts.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOwnerResponse(
        UUID id,
        UUID userId,
        String userEmail,
        String userFirstName,
        String userLastName,
        String userFullName,
        String accountNumber,
        String accountName,
        String accountNameLabel,
        String customAlias,
        String displayName,
        String accountType,
        String currency,
        BigDecimal availableBalance,
        BigDecimal heldBalance,
        BigDecimal totalBalance,
        String status,
        String statusReason,
        boolean active,
        boolean primary,
        Instant openedAt,
        Instant closedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
