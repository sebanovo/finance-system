package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;

import java.util.UUID;

public record ServiceProviderSummaryResponse(
        UUID id,
        String code,
        String name,
        ServiceProviderCategory category
) {
}
