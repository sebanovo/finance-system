package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ServiceBillResponse(
        UUID id,
        ServiceProviderSummaryResponse provider,
        UUID serviceCustomerId,
        String serviceCustomerCode,
        String customerName,
        String billingPeriod,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        ServiceBillStatus status,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        Instant paidAt,
        UUID createdBySuperadminId,
        Instant createdAt,
        Instant updatedAt
) {
}
