package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceBillPaymentResponse(
        UUID id,
        UUID billId,
        ServiceProviderSummaryResponse provider,
        String serviceCustomerCode,
        String serviceCustomerName,
        String billingPeriod,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        BigDecimal amount,
        String currency,
        String receiptNumber,
        String idempotencyKey,
        ServiceBillPaymentStatus status,
        Instant paidAt,
        Instant createdAt
) {
}
