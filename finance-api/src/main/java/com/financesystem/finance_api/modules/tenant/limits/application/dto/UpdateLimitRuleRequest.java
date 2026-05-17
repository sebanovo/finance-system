package com.financesystem.finance_api.modules.tenant.limits.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitPeriod;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRuleType;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitScopeType;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;

import java.math.BigDecimal;

public record UpdateLimitRuleRequest(
        @Size(max = 120)
        String code,

        @Size(max = 120)
        String name,

        @Size(max = 1000)
        String description,

        LimitRuleType limitType,

        LimitScopeType scopeType,

        LimitPeriod period,

        TransactionType transactionType,

        AccountType accountType,

        CurrencyCode currency,

        @DecimalMin("0.0")
        BigDecimal minAmount,

        @DecimalMin("0.0")
        BigDecimal maxAmount,

        Long maxCount,

        Boolean active,

        Boolean requireReviewExceed
) {
}
