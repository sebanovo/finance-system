package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;

import java.time.Instant;
import java.util.UUID;

public record ServiceProviderResponse(
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
