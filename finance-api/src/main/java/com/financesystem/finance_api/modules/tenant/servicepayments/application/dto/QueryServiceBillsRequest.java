package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record QueryServiceBillsRequest(
        UUID providerId,

        @Size(max = 100)
        String serviceCustomerCode,

        UUID enrollmentId
) {
}
