package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactionmovements;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record TransactionMovementReportSortRequest(
        @NotNull
        TransactionMovementReportFieldKey field,
        @NotNull
        SortDirection direction
) {
}
