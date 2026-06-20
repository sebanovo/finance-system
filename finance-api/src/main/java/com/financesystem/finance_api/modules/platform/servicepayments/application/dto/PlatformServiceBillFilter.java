package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PlatformServiceBillFilter(
        UUID providerId,
        String serviceCustomerCode,
        ServiceBillStatus status,
        String billingPeriod,
        LocalDate dueDateFrom,
        LocalDate dueDateTo,
        String paidByTenantSlug,
        String search
) {
}
