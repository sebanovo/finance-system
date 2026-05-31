package com.financesystem.finance_api.modules.tenant.reporting.application.dto.auditevents;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.auditevents.AuditEventReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticAuditEventsReportRequest(
        @NotNull ReportType reportType,
        @NotEmpty List<AuditEventReportColumnKey> columns,
        List<@Valid AuditEventReportFilterRequest> filters,
        List<@Valid AuditEventReportSortRequest> sort,
        @NotNull @Valid ReportPaginationRequest pagination,
        @NotEmpty List<ReportOutput> outputs
) {
}
