package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;

import java.util.UUID;

public record ServicePaymentProcessingRequest(
        UUID actorUserId,
        UUID accountOwnerUserId,
        String tenantSlug,
        UUID providerId,
        String serviceCustomerCode,
        UUID billId,
        UUID enrollmentId,
        String sourceAccountNumber,
        String idempotencyKey,
        TransactionChannel transactionChannel,
        String createdAuditEventType,
        String paymentMode
) {
}
