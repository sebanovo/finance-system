package com.financesystem.finance_api.modules.tenant.accounts.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Account(
        UUID id,
        UUID userId,
        String accountNumber,
        AccountName accountName,
        String customAlias,
        AccountType accountType,
        CurrencyCode currency,
        BigDecimal availableBalance,
        BigDecimal heldBalance,
        AccountStatus status,
        String statusReason,
        boolean active,
        boolean primary,
        Instant openedAt,
        Instant closedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
