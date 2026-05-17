package com.financesystem.finance_api.modules.tenant.limits.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LimitUsage(
        UUID id,
        UUID limitRuleId,
        String scopeKey,
        String periodKey,
        long transactionCount,
        BigDecimal totalAmount,
        Instant lastEvaluatedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
