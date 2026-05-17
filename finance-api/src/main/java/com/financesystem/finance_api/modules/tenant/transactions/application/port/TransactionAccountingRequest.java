package com.financesystem.finance_api.modules.tenant.transactions.application.port;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TransactionAccountingRequest(
        UUID transactionId,
        String transactionType,
        String transactionStatus,
        String currency,
        BigDecimal amount,
        UUID sourceAccountId,
        UUID targetAccountId,
        String externalReference,
        String description,
        Instant processedAt,
        String metadata,
        List<TransactionAccountingMovementRequest> movements
) {
}
