package com.financesystem.finance_api.modules.tenant.reporting.application.dto.operationfees;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.operationfees.OperationFeeReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record OperationFeeReportSortRequest(
        @NotNull
        OperationFeeReportFieldKey field,
        @NotNull
        SortDirection direction
) {
}
