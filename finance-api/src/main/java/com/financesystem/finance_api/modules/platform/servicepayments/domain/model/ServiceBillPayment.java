package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceBillPayment(
        UUID id,
        UUID billId,
        UUID providerId,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        BigDecimal amount,
        String currency,
        String receiptNumber,
        String idempotencyKey,
        ServiceBillPaymentStatus status,
        Instant paidAt,
        Instant createdAt
) {
}
