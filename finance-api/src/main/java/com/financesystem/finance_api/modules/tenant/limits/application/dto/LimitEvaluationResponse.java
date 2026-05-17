package com.financesystem.finance_api.modules.tenant.limits.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public record LimitEvaluationResponse(
        boolean allowed,
        boolean requiresReview,
        String reason,
        TransactionType transactionType,
        BigDecimal amount,
        CurrencyCode currency,
        List<LimitEvaluationRuleCheckResponse> checks
) {
}
