package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactions;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record TransactionReportSortRequest(
        @NotNull
        TransactionReportFieldKey field,
        @NotNull
        SortDirection direction
) {
}
