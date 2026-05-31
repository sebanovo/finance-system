package com.financesystem.finance_api.modules.tenant.reporting.application.dto.auditevents;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.auditevents.AuditEventReportFieldKey;
import jakarta.validation.constraints.NotNull;

public record AuditEventReportFilterRequest(
        @NotNull AuditEventReportFieldKey field,
        @NotNull ReportOperator operator,
        JsonNode value,
        JsonNode to
) {
}
