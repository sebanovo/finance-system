package com.financesystem.finance_api.modules.tenant.reporting.application.dto.fxexchangerates;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.fxexchangerates.FxExchangeRateReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.fxexchangerates.FxExchangeRateReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record FxExchangeRateReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        FxExchangeRateReportFieldKey field,
        FxExchangeRateReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
