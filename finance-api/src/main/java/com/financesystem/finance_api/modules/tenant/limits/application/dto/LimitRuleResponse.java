package com.financesystem.finance_api.modules.tenant.limits.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitPeriod;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRuleType;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitScopeType;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LimitRuleResponse(
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
        String scopeDescription,
        Instant createdAt,
        Instant updatedAt
) {
}
