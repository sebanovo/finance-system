package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;

import java.util.List;
import java.util.UUID;

public record ServiceProviderCatalogResponse(
        UUID id,
        String code,
        String name,
        ServiceProviderCategory category,
        String serviceCustomerCodeLabel,
        ServiceProviderStatus status,
        List<ServiceCustomerCatalogResponse> serviceCustomers
) {
}
