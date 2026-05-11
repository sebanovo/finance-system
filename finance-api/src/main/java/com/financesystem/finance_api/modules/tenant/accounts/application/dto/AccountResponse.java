package com.financesystem.finance_api.modules.tenant.accounts.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        UUID userId,
        String accountNumber,
        String accountType,
        String currency,
        BigDecimal availableBalance,
        BigDecimal heldBalance,
        BigDecimal totalBalance,
        String status,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
