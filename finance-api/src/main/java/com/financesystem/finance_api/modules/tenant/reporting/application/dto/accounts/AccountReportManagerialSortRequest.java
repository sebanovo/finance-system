package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accounts;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record AccountReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        AccountReportFieldKey field,
        AccountReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
