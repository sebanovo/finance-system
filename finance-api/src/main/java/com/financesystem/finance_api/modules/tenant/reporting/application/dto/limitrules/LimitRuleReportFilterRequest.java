package com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitrules;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record LimitRuleReportFilterRequest(
        @NotNull
        LimitRuleReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
