package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record TransactionReportFilterRequest(
        @NotNull
        TransactionReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
