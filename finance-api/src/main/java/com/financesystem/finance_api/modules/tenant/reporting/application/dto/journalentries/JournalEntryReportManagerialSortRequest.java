package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journalentries;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journalentries.JournalEntryReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journalentries.JournalEntryReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record JournalEntryReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        JournalEntryReportFieldKey field,
        JournalEntryReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
