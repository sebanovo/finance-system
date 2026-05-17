package com.financesystem.finance_api.modules.tenant.fx.application.dto;

import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeCalculationMode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeType;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateOperationFeeRequest(
        @NotNull FxOperationCode operationCode,
        @NotNull FeeType feeType,
        @NotNull @DecimalMin(value = "0") BigDecimal feeValue,
        @NotNull FeeCalculationMode calculationMode,
        @NotNull boolean active,
        String description
) {
}
