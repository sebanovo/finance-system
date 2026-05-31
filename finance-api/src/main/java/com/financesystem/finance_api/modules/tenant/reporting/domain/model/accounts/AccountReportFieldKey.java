package com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountReportFieldKey {
    ACCOUNT_ID,
    ACCOUNT_USER_ID,
    ACCOUNT_ACCOUNT_NUMBER,
    ACCOUNT_ACCOUNT_NAME,
    ACCOUNT_CUSTOM_ALIAS,
    ACCOUNT_ACCOUNT_TYPE,
    ACCOUNT_CURRENCY,
    ACCOUNT_AVAILABLE_BALANCE,
    ACCOUNT_HELD_BALANCE,
    ACCOUNT_TOTAL_BALANCE,
    ACCOUNT_STATUS,
    ACCOUNT_STATUS_REASON,
    ACCOUNT_ACTIVE,
    ACCOUNT_PRIMARY,
    ACCOUNT_OPENED_AT,
    ACCOUNT_CLOSED_AT,
    ACCOUNT_CREATED_AT,
    ACCOUNT_UPDATED_AT,
    USER_ID,
    USER_EMAIL,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_FULL_NAME,
    USER_ACTIVE,
    USER_STATUS,
    USER_CREATED_AT,
    USER_UPDATED_AT;

    @JsonCreator
    public static AccountReportFieldKey fromValue(String value) {
        return value == null ? null : AccountReportFieldKey.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
