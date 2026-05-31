package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accounts;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.AccountReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record AccountReportFilterRequest(
        @NotNull
        AccountReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
