package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notificationdeliveries;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdeliveries.NotificationDeliveryReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record NotificationDeliveryReportSortRequest(
        @NotNull NotificationDeliveryReportFieldKey field,
        @NotNull SortDirection direction
) {
}
