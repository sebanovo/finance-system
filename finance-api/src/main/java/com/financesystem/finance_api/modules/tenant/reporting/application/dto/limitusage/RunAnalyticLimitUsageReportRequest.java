package com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitusage;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticLimitUsageReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<LimitUsageReportColumnKey> columns,
        List<@Valid LimitUsageReportFilterRequest> filters,
        List<@Valid LimitUsageReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
