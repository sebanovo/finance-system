package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;

import java.util.UUID;

public record MyServiceEnrollmentFilter(
        UUID providerId,
        ServiceProviderCategory category,
        TenantServiceEnrollmentStatus status,
        String search
) {
}
