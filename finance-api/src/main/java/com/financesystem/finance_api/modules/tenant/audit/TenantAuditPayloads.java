package com.financesystem.finance_api.modules.tenant.audit;

import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriod;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRule;

import java.util.LinkedHashMap;
import java.util.Map;

public final class TenantAuditPayloads {

    private TenantAuditPayloads() {
    }

    public static Map<String, Object> details(Object... keyValues) {
        if (keyValues == null || keyValues.length == 0) {
            return Map.of();
        }

        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Audit details must contain key-value pairs");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            payload.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
        }
        return payload;
    }

    public static Map<String, Object> accountState(Account account) {
        return details(
                "id", account.id(),
                "userId", account.userId(),
                "accountNumber", account.accountNumber(),
                "accountName", account.accountName() == null ? null : account.accountName().name(),
                "customAlias", account.customAlias(),
                "accountType", account.accountType(),
                "currency", account.currency(),
                "availableBalance", account.availableBalance(),
                "heldBalance", account.heldBalance(),
                "status", account.status(),
                "statusReason", account.statusReason(),
                "active", account.active(),
                "primary", account.primary(),
                "openedAt", account.openedAt(),
                "closedAt", account.closedAt(),
                "createdAt", account.createdAt(),
                "updatedAt", account.updatedAt()
        );
    }

    public static Map<String, Object> limitRuleState(LimitRule rule) {
        return details(
                "id", rule.id(),
                "code", rule.code(),
                "name", rule.name(),
                "description", rule.description(),
                "limitType", rule.limitType(),
                "scopeType", rule.scopeType(),
                "period", rule.period(),
                "transactionType", rule.transactionType(),
                "accountType", rule.accountType(),
                "currency", rule.currency(),
                "minAmount", rule.minAmount(),
                "maxAmount", rule.maxAmount(),
                "maxCount", rule.maxCount(),
                "active", rule.active(),
                "requireReviewExceed", rule.requireReviewExceed(),
                "createdByUserId", rule.createdByUserId(),
                "updatedByUserId", rule.updatedByUserId(),
                "createdAt", rule.createdAt(),
                "updatedAt", rule.updatedAt()
        );
    }

    public static Map<String, Object> accountingPeriodState(AccountingPeriod period) {
        return details(
                "id", period.id(),
                "periodCode", period.periodCode(),
                "periodType", period.periodType(),
                "status", period.status(),
                "startDate", period.startDate(),
                "endDate", period.endDate(),
                "closedAt", period.closedAt(),
                "description", period.description(),
                "createdAt", period.createdAt(),
                "updatedAt", period.updatedAt()
        );
    }
}
