package com.financesystem.finance_api.modules.tenant.reporting.application.dto.journallines;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.journallines.JournalLineReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record JournalLineReportFilterRequest(
        @NotNull
        JournalLineReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
