package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;

import java.time.Instant;
import java.util.List;

public record ReportHeaderResponse(
        String title,
        ReportMode mode,
        String modeLabel,
        Instant generatedAt,
        String generatedBy,
        List<String> appliedFilters,
        List<String> selectedColumns,
        List<String> selectedMetrics,
        List<String> selectedGroupBy
) {
}
