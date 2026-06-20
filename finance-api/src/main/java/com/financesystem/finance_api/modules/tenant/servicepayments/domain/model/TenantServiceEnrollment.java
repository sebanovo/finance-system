package com.financesystem.finance_api.modules.tenant.servicepayments.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TenantServiceEnrollment(
        UUID id,
        UUID userId,
        UUID providerId,
        String providerCode,
        String providerName,
        String providerCategory,
        String serviceCustomerCode,
        String serviceCustomerName,
        String alias,
        TenantServiceEnrollmentStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
