package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformServiceBillPaymentFilter(
        UUID providerId,
        String tenantSlug,
        UUID userId,
        String accountNumber,
        UUID billId,
        String receiptNumber,
        Instant paidAtFrom,
        Instant paidAtTo
) {
}
