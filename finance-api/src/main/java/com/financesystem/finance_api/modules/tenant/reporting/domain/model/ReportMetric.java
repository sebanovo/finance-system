package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import java.util.Objects;
import java.util.Set;

public record ReportMetric(
        Enum<?> key,
        String label,
        ReportFieldType type,
        String sqlExpression,
        Set<ReportMode> allowedModes
) {

    public ReportMetric {
        key = Objects.requireNonNull(key, "key must not be null");
        label = Objects.requireNonNull(label, "label must not be null");
        type = Objects.requireNonNull(type, "type must not be null");
        sqlExpression = Objects.requireNonNull(sqlExpression, "sqlExpression must not be null");
        allowedModes = allowedModes == null ? Set.of() : Set.copyOf(allowedModes);
    }
}
