package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderSummaryResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServicePaymentResponse(
        UUID paymentId,
        UUID billId,
        UUID transactionId,
        String receiptNumber,
        ServiceProviderSummaryResponse provider,
        String serviceCustomerCode,
        String serviceCustomerName,
        String billingPeriod,
        BigDecimal amount,
        String currency,
        String sourceAccountNumber,
        ServiceBillStatus status,
        Instant paidAt
) {
}
