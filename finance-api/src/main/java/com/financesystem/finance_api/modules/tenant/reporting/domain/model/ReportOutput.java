package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportOutput {
    SCREEN,
    PDF,
    XLSX;

    @JsonCreator
    public static ReportOutput fromValue(String value) {
        return value == null ? null : ReportOutput.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
