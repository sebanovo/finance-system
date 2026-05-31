package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journallines;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journallines.JournalLineReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticJournalLinesReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<JournalLineReportColumnKey> columns,
        List<@Valid JournalLineReportFilterRequest> filters,
        List<@Valid JournalLineReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
