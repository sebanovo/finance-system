package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accounts;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticAccountReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<AccountReportColumnKey> columns,
        List<@Valid AccountReportFilterRequest> filters,
        List<@Valid AccountReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
