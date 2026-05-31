package com.financesystem.finance_api.modules.tenant.reporting.application.dto.fxexchangerates;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.fxexchangerates.FxExchangeRateReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record FxExchangeRateReportSortRequest(
        @NotNull
        FxExchangeRateReportFieldKey field,
        @NotNull
        SortDirection direction
) {
}
