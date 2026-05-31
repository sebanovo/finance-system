package com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactionmovements;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticTransactionMovementsReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<TransactionMovementReportColumnKey> columns,
        List<@Valid TransactionMovementReportFilterRequest> filters,
        List<@Valid TransactionMovementReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
