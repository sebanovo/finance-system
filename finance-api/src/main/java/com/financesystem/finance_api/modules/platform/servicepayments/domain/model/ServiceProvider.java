package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ServiceProvider(
        UUID id,
        String code,
        String name,
        ServiceProviderCategory category,
        String serviceCustomerCodeLabel,
        ServiceProviderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
