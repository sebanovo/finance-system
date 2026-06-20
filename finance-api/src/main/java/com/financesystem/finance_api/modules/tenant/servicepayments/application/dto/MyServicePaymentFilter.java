package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import java.time.Instant;
import java.util.UUID;

public record MyServicePaymentFilter(
        UUID providerId,
        String receiptNumber,
        UUID billId,
        Instant paidAtFrom,
        Instant paidAtTo
) {
}
