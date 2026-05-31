package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notificationdevices;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdevices.NotificationDeviceReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdevices.NotificationDeviceReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialNotificationDevicesReportRequest(
        @NotNull ReportType reportType,
        @NotEmpty List<NotificationDeviceReportGroupByKey> groupBy,
        @NotEmpty List<NotificationDeviceReportMetricKey> metrics,
        List<@Valid NotificationDeviceReportFilterRequest> filters,
        List<@Valid NotificationDeviceReportManagerialSortRequest> sort,
        @NotNull @Valid ReportPaginationRequest pagination,
        @NotEmpty List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
