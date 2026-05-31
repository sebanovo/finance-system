package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record ReportField(
        Enum<?> key,
        String label,
        ReportFieldType type,
        String sqlExpression,
        List<ReportOperator> operators,
        List<Enum<?>> options,
        String relationKey,
        Set<ReportMode> allowedModes,
        boolean filterable,
        boolean columnable,
        boolean groupable,
        boolean sortable
) {

    public ReportField {
        key = Objects.requireNonNull(key, "key must not be null");
        label = Objects.requireNonNull(label, "label must not be null");
        type = Objects.requireNonNull(type, "type must not be null");
        sqlExpression = Objects.requireNonNull(sqlExpression, "sqlExpression must not be null");
        operators = operators == null ? List.of() : List.copyOf(operators);
        options = options == null ? List.of() : List.copyOf(options);
        allowedModes = allowedModes == null ? Set.of() : Set.copyOf(allowedModes);
    }
}
