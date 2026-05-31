package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactionmovements;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record TransactionMovementReportFilterRequest(
        @NotNull
        TransactionMovementReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
