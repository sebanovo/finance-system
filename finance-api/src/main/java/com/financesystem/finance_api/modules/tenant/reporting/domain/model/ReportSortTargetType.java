package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportSortTargetType {
    FIELD,
    METRIC;

    @JsonCreator
    public static ReportSortTargetType fromValue(String value) {
        return value == null ? null : ReportSortTargetType.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
