package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;

import java.util.UUID;

public record PlatformServiceCustomerFilter(
        UUID providerId,
        ServiceCustomerStatus status,
        String serviceCustomerCode,
        String search
) {
}
