package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactions;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticTransactionReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<TransactionReportColumnKey> columns,
        List<@Valid TransactionReportFilterRequest> filters,
        List<@Valid TransactionReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
