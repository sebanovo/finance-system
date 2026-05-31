package com.financesystem.finance_api.modules.tenant.reporting.application.dto.operationfees;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.operationfees.OperationFeeReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record OperationFeeReportFilterRequest(
        @NotNull
        OperationFeeReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
