package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderSummaryResponse;

import java.util.List;

public record QueryServiceBillsResponse(
        ServiceProviderSummaryResponse provider,
        String serviceCustomerCode,
        String serviceCustomerName,
        List<ServiceBillQueryItemResponse> bills
) {
}
