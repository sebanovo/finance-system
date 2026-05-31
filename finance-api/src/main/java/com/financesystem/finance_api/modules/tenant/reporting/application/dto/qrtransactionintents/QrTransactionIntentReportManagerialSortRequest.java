package com.financesystem.finance_api.modules.tenant.reporting.application.dto.qrtransactionintents;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.qrtransactionintents.QrTransactionIntentReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.qrtransactionintents.QrTransactionIntentReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record QrTransactionIntentReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        QrTransactionIntentReportFieldKey field,
        QrTransactionIntentReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
