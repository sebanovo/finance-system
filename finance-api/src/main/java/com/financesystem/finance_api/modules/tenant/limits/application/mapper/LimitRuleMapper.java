package com.financesystem.finance_api.modules.tenant.limits.application.mapper;

import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitRuleResponse;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRule;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LimitRuleMapper {

    public LimitRuleResponse toResponse(LimitRule limitRule) {
        return new LimitRuleResponse(
                limitRule.id(),
                limitRule.code(),
                limitRule.name(),
                limitRule.description(),
                limitRule.limitType(),
                limitRule.scopeType(),
                limitRule.period(),
                limitRule.transactionType(),
                limitRule.accountType(),
                limitRule.currency(),
                limitRule.minAmount(),
                limitRule.maxAmount(),
                limitRule.maxCount(),
                limitRule.active(),
                limitRule.requireReviewExceed(),
                buildScopeDescription(limitRule),
                limitRule.createdAt(),
                limitRule.updatedAt()
        );
    }

    private String buildScopeDescription(LimitRule limitRule) {
        return switch (limitRule.scopeType()) {
            case TENANT -> "Applies to the whole tenant";
            case TRANSACTION_TYPE -> "Applies to transaction type " + safeValue(limitRule.transactionType() != null ? limitRule.transactionType().name() : null);
            case ACCOUNT_TYPE -> "Applies to account type " + safeValue(limitRule.accountType() != null ? limitRule.accountType().name() : null);
            case USER -> "Applies to a specific user";
            case ACCOUNT -> "Applies to a specific account";
        };
    }

    private String safeValue(String value) {
        if (value == null) {
            return "-";
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }
}
