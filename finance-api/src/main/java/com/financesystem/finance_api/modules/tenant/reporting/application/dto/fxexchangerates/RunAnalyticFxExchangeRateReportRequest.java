package com.financesystem.finance_api.modules.tenant.reporting.application.dto.fxexchangerates;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.fxexchangerates.FxExchangeRateReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticFxExchangeRateReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<FxExchangeRateReportColumnKey> columns,
        List<@Valid FxExchangeRateReportFilterRequest> filters,
        List<@Valid FxExchangeRateReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
