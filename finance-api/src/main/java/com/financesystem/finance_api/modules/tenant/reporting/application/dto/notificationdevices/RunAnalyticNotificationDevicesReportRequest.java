package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notificationdevices;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdevices.NotificationDeviceReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticNotificationDevicesReportRequest(
        @NotNull ReportType reportType,
        @NotEmpty List<NotificationDeviceReportColumnKey> columns,
        List<@Valid NotificationDeviceReportFilterRequest> filters,
        List<@Valid NotificationDeviceReportSortRequest> sort,
        @NotNull @Valid ReportPaginationRequest pagination,
        @NotEmpty List<ReportOutput> outputs
) {
}
