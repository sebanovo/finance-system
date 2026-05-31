package com.financesystem.finance_api.modules.tenant.reporting.application.query;

import java.util.List;

public record ReportSqlQuery(
        String sql,
        List<Object> parameters
) {

    public ReportSqlQuery {
        parameters = parameters == null ? List.of() : List.copyOf(parameters);
    }
}
