package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journallines;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journallines.JournalLineReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journallines.JournalLineReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record JournalLineReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        JournalLineReportFieldKey field,
        JournalLineReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
