package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import java.util.Objects;
import java.util.Set;

public record ReportRelation(
        String key,
        String joinSql,
        Set<ReportMode> allowedModes
) {

    public ReportRelation {
        key = Objects.requireNonNull(key, "key must not be null");
        joinSql = Objects.requireNonNull(joinSql, "joinSql must not be null");
        allowedModes = allowedModes == null ? Set.of() : Set.copyOf(allowedModes);
    }
}
