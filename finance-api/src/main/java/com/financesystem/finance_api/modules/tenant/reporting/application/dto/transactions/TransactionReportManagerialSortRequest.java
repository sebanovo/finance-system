package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactions;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record TransactionReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        TransactionReportFieldKey field,
        TransactionReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
