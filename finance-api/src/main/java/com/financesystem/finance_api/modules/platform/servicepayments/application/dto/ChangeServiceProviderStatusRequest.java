package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeServiceProviderStatusRequest(
        @NotNull
        ServiceProviderStatus status
) {
}
