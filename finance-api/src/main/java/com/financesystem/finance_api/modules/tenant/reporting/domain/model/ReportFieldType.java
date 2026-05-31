package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportFieldType {
    UUID,
    TEXT,
    ENUM,
    BOOLEAN,
    MONEY,
    NUMBER,
    DATETIME,
    DATE;

    @JsonCreator
    public static ReportFieldType fromValue(String value) {
        return value == null ? null : ReportFieldType.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
