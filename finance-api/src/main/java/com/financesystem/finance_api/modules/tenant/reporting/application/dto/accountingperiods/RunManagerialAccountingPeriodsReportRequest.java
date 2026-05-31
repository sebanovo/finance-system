package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accountingperiods;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialAccountingPeriodsReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<AccountingPeriodReportGroupByKey> groupBy,
        @NotEmpty
        List<AccountingPeriodReportMetricKey> metrics,
        List<@Valid AccountingPeriodReportFilterRequest> filters,
        List<@Valid AccountingPeriodReportManagerialSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
