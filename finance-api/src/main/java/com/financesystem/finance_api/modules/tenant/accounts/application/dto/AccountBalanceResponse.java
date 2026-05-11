package com.financesystem.finance_api.modules.tenant.accounts.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceResponse(
        UUID accountId,
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
        boolean active
) {
}