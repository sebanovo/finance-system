package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notificationdevices;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdevices.NotificationDeviceReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdevices.NotificationDeviceReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record NotificationDeviceReportManagerialSortRequest(
        @NotNull ReportSortTargetType targetType,
        NotificationDeviceReportFieldKey field,
        NotificationDeviceReportMetricKey metric,
        @NotNull SortDirection direction
) {
}
