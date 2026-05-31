package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactions;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialTransactionReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<TransactionReportGroupByKey> groupBy,
        @NotEmpty
        List<TransactionReportMetricKey> metrics,
        List<@Valid TransactionReportFilterRequest> filters,
        List<@Valid TransactionReportManagerialSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
