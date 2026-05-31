package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accountingperiods;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticAccountingPeriodsReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<AccountingPeriodReportColumnKey> columns,
        List<@Valid AccountingPeriodReportFilterRequest> filters,
        List<@Valid AccountingPeriodReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
