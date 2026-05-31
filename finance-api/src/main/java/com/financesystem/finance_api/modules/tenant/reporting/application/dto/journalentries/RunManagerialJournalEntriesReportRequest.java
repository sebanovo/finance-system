package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journalentries;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journalentries.JournalEntryReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journalentries.JournalEntryReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialJournalEntriesReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<JournalEntryReportGroupByKey> groupBy,
        @NotEmpty
        List<JournalEntryReportMetricKey> metrics,
        List<@Valid JournalEntryReportFilterRequest> filters,
        List<@Valid JournalEntryReportManagerialSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
