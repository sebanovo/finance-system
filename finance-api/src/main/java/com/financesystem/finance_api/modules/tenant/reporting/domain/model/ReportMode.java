package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportMode {
    ANALYTIC,
    MANAGERIAL;

    @JsonCreator
    public static ReportMode fromValue(String value) {
        return value == null ? null : ReportMode.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
