package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactionmovements;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialTransactionMovementsReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<TransactionMovementReportGroupByKey> groupBy,
        @NotEmpty
        List<TransactionMovementReportMetricKey> metrics,
        List<@Valid TransactionMovementReportFilterRequest> filters,
        List<@Valid TransactionMovementReportManagerialSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
