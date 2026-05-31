package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journallines;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journallines.JournalLineReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record JournalLineReportSortRequest(
        @NotNull
        JournalLineReportFieldKey field,
        @NotNull
        SortDirection direction
) {
}
