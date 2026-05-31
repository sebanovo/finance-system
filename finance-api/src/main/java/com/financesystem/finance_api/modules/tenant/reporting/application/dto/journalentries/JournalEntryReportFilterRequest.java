package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journalentries;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journalentries.JournalEntryReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record JournalEntryReportFilterRequest(
        @NotNull
        JournalEntryReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
