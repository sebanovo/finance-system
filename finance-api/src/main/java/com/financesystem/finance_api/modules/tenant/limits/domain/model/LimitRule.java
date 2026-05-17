package com.financesystem.finance_api.modules.tenant.limits.domain.model;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LimitRule(
        UUID id,
        String code,
        String name,
        String description,
        LimitRuleType limitType,
        LimitScopeType scopeType,
        LimitPeriod period,
        TransactionType transactionType,
        AccountType accountType,
        CurrencyCode currency,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Long maxCount,
        boolean active,
        boolean requireReviewExceed,
        UUID createdByUserId,
        UUID updatedByUserId,
        Instant createdAt,
        Instant updatedAt
) {
}
