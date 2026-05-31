package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import java.util.Objects;

public record ReportRoot(
        String entity,
        String table,
        String alias
) {

    public ReportRoot {
        entity = Objects.requireNonNull(entity, "entity must not be null");
        table = Objects.requireNonNull(table, "table must not be null");
        alias = Objects.requireNonNull(alias, "alias must not be null");
    }
}
