package com.financesystem.finance_api.modules.tenant.limits.domain.model;

public enum LimitRuleType {
    PER_TRANSACTION_AMOUNT,
    DAILY_AMOUNT,
    MONTHLY_AMOUNT,
    DAILY_COUNT,
    MONTHLY_COUNT,
    MIN_AMOUNT,
    MAX_BALANCE
}
