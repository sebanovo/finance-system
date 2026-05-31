package com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitusage;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record LimitUsageReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        LimitUsageReportFieldKey field,
        LimitUsageReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
