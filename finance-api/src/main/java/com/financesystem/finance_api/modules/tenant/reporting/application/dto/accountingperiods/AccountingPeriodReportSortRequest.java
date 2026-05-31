package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accountingperiods;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record AccountingPeriodReportSortRequest(
        @NotNull
        AccountingPeriodReportFieldKey field,
        @NotNull
        SortDirection direction
) {
}
