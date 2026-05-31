package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notificationdeliveries;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdeliveries.NotificationDeliveryReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdeliveries.NotificationDeliveryReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record NotificationDeliveryReportManagerialSortRequest(
        @NotNull ReportSortTargetType targetType,
        NotificationDeliveryReportFieldKey field,
        NotificationDeliveryReportMetricKey metric,
        @NotNull SortDirection direction
) {
}
