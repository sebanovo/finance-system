package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accountingperiods;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record AccountingPeriodReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        AccountingPeriodReportFieldKey field,
        AccountingPeriodReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
