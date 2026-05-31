package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notifications;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notifications.NotificationReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notifications.NotificationReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record NotificationReportManagerialSortRequest(
        @NotNull ReportSortTargetType targetType,
        NotificationReportFieldKey field,
        NotificationReportMetricKey metric,
        @NotNull SortDirection direction
) {
}
