package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notificationdeliveries;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdeliveries.NotificationDeliveryReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdeliveries.NotificationDeliveryReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialNotificationDeliveriesReportRequest(
        @NotNull ReportType reportType,
        @NotEmpty List<NotificationDeliveryReportGroupByKey> groupBy,
        @NotEmpty List<NotificationDeliveryReportMetricKey> metrics,
        List<@Valid NotificationDeliveryReportFilterRequest> filters,
        List<@Valid NotificationDeliveryReportManagerialSortRequest> sort,
        @NotNull @Valid ReportPaginationRequest pagination,
        @NotEmpty List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
