package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;

import java.util.UUID;

public record ServiceCustomerCatalogResponse(
        UUID id,
        String serviceCustomerCode,
        String customerName,
        ServiceCustomerStatus status
) {
}
