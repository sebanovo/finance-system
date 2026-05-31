package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accounts;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record AccountReportSortRequest(
        @NotNull
        AccountReportFieldKey field,
        @NotNull
        SortDirection direction
) {
}
