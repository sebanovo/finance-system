package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ServiceCustomer(
        UUID id,
        UUID providerId,
        String serviceCustomerCode,
        String customerName,
        ServiceCustomerStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
