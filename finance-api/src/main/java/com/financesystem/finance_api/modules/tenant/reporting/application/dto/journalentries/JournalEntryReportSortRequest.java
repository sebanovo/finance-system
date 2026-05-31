package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journalentries;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journalentries.JournalEntryReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record JournalEntryReportSortRequest(
        @NotNull
        JournalEntryReportFieldKey field,
        @NotNull
        SortDirection direction
) {
}
