package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accounts;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialAccountReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<AccountReportGroupByKey> groupBy,
        @NotEmpty
        List<AccountReportMetricKey> metrics,
        List<@Valid AccountReportFilterRequest> filters,
        List<@Valid AccountReportManagerialSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
