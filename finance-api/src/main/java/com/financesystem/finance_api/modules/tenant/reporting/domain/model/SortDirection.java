package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SortDirection {
    ASC,
    DESC;

    @JsonCreator
    public static SortDirection fromValue(String value) {
        return value == null ? null : SortDirection.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
