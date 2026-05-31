package com.financesystem.finance_api.modules.tenant.reporting.application.dto.qrtransactionintents;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.qrtransactionintents.QrTransactionIntentReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record QrTransactionIntentReportSortRequest(
        @NotNull
        QrTransactionIntentReportFieldKey field,
        @NotNull
        SortDirection direction
) {
}
