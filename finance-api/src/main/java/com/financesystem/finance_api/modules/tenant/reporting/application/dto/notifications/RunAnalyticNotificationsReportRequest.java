package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notifications;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notifications.NotificationReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticNotificationsReportRequest(
        @NotNull ReportType reportType,
        @NotEmpty List<NotificationReportColumnKey> columns,
        List<@Valid NotificationReportFilterRequest> filters,
        List<@Valid NotificationReportSortRequest> sort,
        @NotNull @Valid ReportPaginationRequest pagination,
        @NotEmpty List<ReportOutput> outputs
) {
}
