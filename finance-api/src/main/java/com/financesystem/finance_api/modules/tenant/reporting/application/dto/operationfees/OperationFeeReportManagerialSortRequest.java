package com.financesystem.finance_api.modules.tenant.reporting.application.dto.operationfees;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.operationfees.OperationFeeReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.operationfees.OperationFeeReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record OperationFeeReportManagerialSortRequest(
        @NotNull ReportSortTargetType targetType,
        OperationFeeReportFieldKey field,
        OperationFeeReportMetricKey metric,
        @NotNull SortDirection direction
) {
}
