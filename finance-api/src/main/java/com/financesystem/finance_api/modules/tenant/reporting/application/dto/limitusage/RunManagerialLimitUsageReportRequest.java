package com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitusage;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialLimitUsageReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<LimitUsageReportGroupByKey> groupBy,
        @NotEmpty
        List<LimitUsageReportMetricKey> metrics,
        List<@Valid LimitUsageReportFilterRequest> filters,
        List<@Valid LimitUsageReportManagerialSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
