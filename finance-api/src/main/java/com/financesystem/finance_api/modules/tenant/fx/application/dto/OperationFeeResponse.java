package com.financesystem.finance_api.modules.tenant.fx.application.dto;

import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeCalculationMode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeType;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OperationFeeResponse(
        UUID id,
        FxOperationCode operationCode,
        FeeType feeType,
        BigDecimal feeValue,
        FeeCalculationMode calculationMode,
        boolean active,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
