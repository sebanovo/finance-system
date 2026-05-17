package com.financesystem.finance_api.modules.tenant.fx.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OperationFee(
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
