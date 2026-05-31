package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportVisualizationType {
    SUMMARY_CARDS,
    TABLE,
    BAR_CHART,
    PIE_CHART;

    @JsonCreator
    public static ReportVisualizationType fromValue(String value) {
        return value == null ? null : ReportVisualizationType.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
