package com.financesystem.finance_api.modules.tenant.reporting.application.dto.auditevents;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.auditevents.AuditEventReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.auditevents.AuditEventReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record AuditEventReportManagerialSortRequest(
        @NotNull ReportSortTargetType targetType,
        AuditEventReportFieldKey field,
        AuditEventReportMetricKey metric,
        @NotNull SortDirection direction
) {
}
