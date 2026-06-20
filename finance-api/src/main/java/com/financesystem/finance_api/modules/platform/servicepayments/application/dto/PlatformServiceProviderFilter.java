package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;

public record PlatformServiceProviderFilter(
        ServiceProviderCategory category,
        ServiceProviderStatus status,
        String search
) {
}
