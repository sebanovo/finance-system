package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderSummaryResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;

import java.time.Instant;
import java.util.UUID;

public record ServiceEnrollmentResponse(
        UUID enrollmentId,
        ServiceProviderSummaryResponse provider,
        String serviceCustomerCode,
        String serviceCustomerName,
        String alias,
        TenantServiceEnrollmentStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
