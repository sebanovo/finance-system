package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notificationdeliveries;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdeliveries.NotificationDeliveryReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record NotificationDeliveryReportFilterRequest(
        @NotNull NotificationDeliveryReportFieldKey field,
        @NotNull ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
