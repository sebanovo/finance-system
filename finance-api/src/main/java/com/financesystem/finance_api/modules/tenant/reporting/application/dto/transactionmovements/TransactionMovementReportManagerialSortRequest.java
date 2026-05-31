package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactionmovements;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record TransactionMovementReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        TransactionMovementReportFieldKey field,
        TransactionMovementReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
