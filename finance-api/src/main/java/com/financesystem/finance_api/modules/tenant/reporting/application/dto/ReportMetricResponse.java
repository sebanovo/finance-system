package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportFieldType;

public record ReportMetricResponse(
        Enum<?> key,
        String label,
        ReportFieldType type
) {
}
