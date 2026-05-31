package com.financesystem.finance_api.modules.tenant.reporting.application.dto;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportFieldType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;

import java.util.List;

public record ReportFieldResponse(
        Enum<?> key,
        String label,
        ReportFieldType type,
        List<ReportOperator> operators,
        List<Enum<?>> options
) {
}
