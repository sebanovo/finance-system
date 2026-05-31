package com.financesystem.finance_api.modules.tenant.reporting.application.dto.fxexchangerates;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.fxexchangerates.FxExchangeRateReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.fxexchangerates.FxExchangeRateReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialFxExchangeRateReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<FxExchangeRateReportGroupByKey> groupBy,
        @NotEmpty
        List<FxExchangeRateReportMetricKey> metrics,
        List<@Valid FxExchangeRateReportFilterRequest> filters,
        List<@Valid FxExchangeRateReportManagerialSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
