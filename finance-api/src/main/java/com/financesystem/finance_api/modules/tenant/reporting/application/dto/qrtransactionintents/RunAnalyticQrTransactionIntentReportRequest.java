package com.financesystem.finance_api.modules.tenant.reporting.application.dto.qrtransactionintents;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.qrtransactionintents.QrTransactionIntentReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticQrTransactionIntentReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<QrTransactionIntentReportColumnKey> columns,
        List<@Valid QrTransactionIntentReportFilterRequest> filters,
        List<@Valid QrTransactionIntentReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
