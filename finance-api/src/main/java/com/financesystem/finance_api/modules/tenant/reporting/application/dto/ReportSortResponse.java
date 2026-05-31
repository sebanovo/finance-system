package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;

public record ReportSortResponse(
        ReportSortTargetType targetType,
        Enum<?> field,
        Enum<?> metric,
        SortDirection direction
) {
}
