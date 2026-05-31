package com.financesystem.finance_api.modules.tenant.reporting.application.dto.fxexchangerates;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.fxexchangerates.FxExchangeRateReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record FxExchangeRateReportFilterRequest(
        @NotNull
        FxExchangeRateReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
