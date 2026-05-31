package com.financesystem.finance_api.modules.tenant.reporting.application.dto.accountingperiods;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accountingperiods.AccountingPeriodReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record AccountingPeriodReportFilterRequest(
        @NotNull
        AccountingPeriodReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
