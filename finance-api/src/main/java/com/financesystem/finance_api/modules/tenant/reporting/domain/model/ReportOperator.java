package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportOperator {
    EQ,
    IN,
    CONTAINS,
    STARTS_WITH,
    ENDS_WITH,
    GT,
    GTE,
    LT,
    LTE,
    BETWEEN;

    @JsonCreator
    public static ReportOperator fromValue(String value) {
        return value == null ? null : ReportOperator.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
