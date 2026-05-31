package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notifications;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notifications.NotificationReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notifications.NotificationReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialNotificationsReportRequest(
        @NotNull ReportType reportType,
        @NotEmpty List<NotificationReportGroupByKey> groupBy,
        @NotEmpty List<NotificationReportMetricKey> metrics,
        List<@Valid NotificationReportFilterRequest> filters,
        List<@Valid NotificationReportManagerialSortRequest> sort,
        @NotNull @Valid ReportPaginationRequest pagination,
        @NotEmpty List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
