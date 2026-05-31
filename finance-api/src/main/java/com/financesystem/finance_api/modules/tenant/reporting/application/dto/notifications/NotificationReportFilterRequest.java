package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notifications;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notifications.NotificationReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record NotificationReportFilterRequest(
        @NotNull NotificationReportFieldKey field,
        @NotNull ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
