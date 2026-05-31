package com.financesystem.finance_api.modules.tenant.reporting.application.dto.qrtransactionintents;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.qrtransactionintents.QrTransactionIntentReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record QrTransactionIntentReportFilterRequest(
        @NotNull
        QrTransactionIntentReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
