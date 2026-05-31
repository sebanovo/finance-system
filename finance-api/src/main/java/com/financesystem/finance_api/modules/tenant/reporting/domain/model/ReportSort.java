package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

public record ReportSort(
        ReportSortTargetType targetType,
        Enum<?> field,
        Enum<?> metric,
        SortDirection direction
) {
}
