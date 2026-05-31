package com.financesystem.finance_api.modules.tenant.reporting.application.dto.auditevents;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.auditevents.AuditEventReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.auditevents.AuditEventReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialAuditEventsReportRequest(
        @NotNull ReportType reportType,
        @NotEmpty List<AuditEventReportGroupByKey> groupBy,
        @NotEmpty List<AuditEventReportMetricKey> metrics,
        List<@Valid AuditEventReportFilterRequest> filters,
        List<@Valid AuditEventReportManagerialSortRequest> sort,
        @NotNull @Valid ReportPaginationRequest pagination,
        @NotEmpty List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
