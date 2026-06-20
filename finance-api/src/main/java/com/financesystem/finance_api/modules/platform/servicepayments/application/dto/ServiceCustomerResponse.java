package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record ServiceCustomerResponse(
        UUID id,
        ServiceProviderSummaryResponse provider,
        String serviceCustomerCode,
        String customerName,
        ServiceCustomerStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
