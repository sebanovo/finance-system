package com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitrules;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportColumnKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunAnalyticLimitRulesReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<LimitRuleReportColumnKey> columns,
        List<@Valid LimitRuleReportFilterRequest> filters,
        List<@Valid LimitRuleReportSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs
) {
}
