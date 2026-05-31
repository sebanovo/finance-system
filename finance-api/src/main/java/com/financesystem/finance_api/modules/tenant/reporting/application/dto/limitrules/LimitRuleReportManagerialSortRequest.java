package com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitrules;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportSortTargetType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.SortDirection;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitrules.LimitRuleReportMetricKey;
import jakarta.validation.constraints.NotNull;

public record LimitRuleReportManagerialSortRequest(
        @NotNull
        ReportSortTargetType targetType,
        LimitRuleReportFieldKey field,
        LimitRuleReportMetricKey metric,
        @NotNull
        SortDirection direction
) {
}
