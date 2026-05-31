package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journalentries;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journalentries.JournalEntryReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticJournalEntriesReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<JournalEntryReportColumnKey> columns,
        List<@Valid JournalEntryReportFilterRequest> filters,
        List<@Valid JournalEntryReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
