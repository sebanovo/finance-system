package com.financesystem.finance_api.modules.tenant.reporting.application.dto.notificationdeliveries;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdeliveries.NotificationDeliveryReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticNotificationDeliveriesReportRequest(
        @NotNull ReportType reportType,
        @NotEmpty List<NotificationDeliveryReportColumnKey> columns,
        List<@Valid NotificationDeliveryReportFilterRequest> filters,
        List<@Valid NotificationDeliveryReportSortRequest> sort,
        @NotNull @Valid ReportPaginationRequest pagination,
        @NotEmpty List<ReportOutput> outputs
) {
}
