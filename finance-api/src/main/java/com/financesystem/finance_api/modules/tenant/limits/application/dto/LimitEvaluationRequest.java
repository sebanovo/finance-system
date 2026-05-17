package com.financesystem.finance_api.modules.tenant.limits.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record LimitEvaluationRequest(
        UUID actorUserId,
        UUID sourceAccountId,
        UUID targetAccountId,
        AccountType sourceAccountType,
        AccountType targetAccountType,
        BigDecimal sourceAvailableBalance,
        BigDecimal targetAvailableBalance,
        TransactionType transactionType,
        CurrencyCode currency,
        BigDecimal amount
) {
}
