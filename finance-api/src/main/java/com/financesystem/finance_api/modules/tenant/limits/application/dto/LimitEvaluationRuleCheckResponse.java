package com.financesystem.finance_api.modules.tenant.limits.application.dto;

import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitPeriod;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRuleType;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitScopeType;

import java.math.BigDecimal;
import java.util.UUID;

public record LimitEvaluationRuleCheckResponse(
        UUID ruleId,
        String code,
        String name,
        LimitRuleType limitType,
        LimitScopeType scopeType,
        LimitPeriod period,
        boolean matched,
        boolean allowed,
        boolean requiresReview,
        String reason,
        BigDecimal currentAmount,
        Long currentCount,
        BigDecimal remainingAmount,
        Long remainingCount,
        BigDecimal maxAmount,
        Long maxCount
) {
}
