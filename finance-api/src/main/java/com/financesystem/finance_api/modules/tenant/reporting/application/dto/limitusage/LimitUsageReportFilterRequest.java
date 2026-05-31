package com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitusage;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.LimitUsageReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record LimitUsageReportFilterRequest(
        @NotNull
        LimitUsageReportFieldKey field,
        @NotNull
        ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
