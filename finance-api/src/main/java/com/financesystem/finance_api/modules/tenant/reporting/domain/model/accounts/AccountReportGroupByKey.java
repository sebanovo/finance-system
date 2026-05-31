package com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountReportGroupByKey {
    ACCOUNT_ACCOUNT_NAME,
    ACCOUNT_ACCOUNT_TYPE,
    ACCOUNT_CURRENCY,
    ACCOUNT_STATUS,
    ACCOUNT_ACTIVE,
    ACCOUNT_PRIMARY,
    USER_STATUS;

    @JsonCreator
    public static AccountReportGroupByKey fromValue(String value) {
        return value == null ? null : AccountReportGroupByKey.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
