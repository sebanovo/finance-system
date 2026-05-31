package com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitrules;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialLimitRulesReportRequest(
        @NotNull
        ReportType reportType,
        @NotEmpty
        List<LimitRuleReportGroupByKey> groupBy,
        @NotEmpty
        List<LimitRuleReportMetricKey> metrics,
        List<@Valid LimitRuleReportFilterRequest> filters,
        List<@Valid LimitRuleReportManagerialSortRequest> sort,
        @NotNull
        @Valid
        ReportPaginationRequest pagination,
        @NotEmpty
        List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
