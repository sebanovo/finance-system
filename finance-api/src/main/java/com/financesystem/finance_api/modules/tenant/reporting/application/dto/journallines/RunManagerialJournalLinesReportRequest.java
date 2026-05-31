package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journallines;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journallines.JournalLineReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journallines.JournalLineReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialJournalLinesReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<JournalLineReportGroupByKey> groupBy,
        @NotEmpty
        List<JournalLineReportMetricKey> metrics,
        List<@Valid JournalLineReportFilterRequest> filters,
        List<@Valid JournalLineReportManagerialSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
