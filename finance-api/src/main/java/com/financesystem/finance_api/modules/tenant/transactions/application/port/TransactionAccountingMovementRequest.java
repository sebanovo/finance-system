package com.financesystem.finance_api.modules.tenant.transactions.application.port;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionAccountingMovementRequest(
        UUID accountId,
        String movementType,
        BigDecimal amount,
        String currency,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String description
) {
}
