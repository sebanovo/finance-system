package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notificationdevices;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdevices.NotificationDeviceReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record NotificationDeviceReportSortRequest(
        @NotNull NotificationDeviceReportFieldKey field,
        @NotNull SortDirection direction
) {
}
