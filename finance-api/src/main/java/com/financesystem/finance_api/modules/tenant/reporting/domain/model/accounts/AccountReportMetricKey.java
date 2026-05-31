package com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountReportMetricKey {
    TOTAL_ACCOUNTS,
    ACTIVE_ACCOUNTS,
    INACTIVE_ACCOUNTS,
    PRIMARY_ACCOUNTS,
    TOTAL_AVAILABLE_BALANCE,
    TOTAL_HELD_BALANCE,
    TOTAL_BALANCE,
    AVERAGE_AVAILABLE_BALANCE,
    MAX_AVAILABLE_BALANCE,
    MIN_AVAILABLE_BALANCE;

    @JsonCreator
    public static AccountReportMetricKey fromValue(String value) {
        return value == null ? null : AccountReportMetricKey.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
