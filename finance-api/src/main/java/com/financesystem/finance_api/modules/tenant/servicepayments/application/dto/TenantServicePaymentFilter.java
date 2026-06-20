package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import java.time.Instant;
import java.util.UUID;

public record TenantServicePaymentFilter(
        UUID providerId,
        UUID userId,
        String accountNumber,
        UUID billId,
        String receiptNumber,
        Instant paidAtFrom,
        Instant paidAtTo
) {
}
