package com.financesystem.finance_api.modules.tenant.transactions.domain.model;

public enum TransactionStatus {
    PENDING,
    PENDING_REVIEW,
    PROCESSING,
    AUTHORIZED,
    COMPLETED,
    FAILED,
    REVERSED,
    PARTIALLY_REFUNDED,
    CANCELLED,
    EXPIRED
}
