package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notifications;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notifications.NotificationReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record NotificationReportSortRequest(
        @NotNull NotificationReportFieldKey field,
        @NotNull SortDirection direction
) {
}
