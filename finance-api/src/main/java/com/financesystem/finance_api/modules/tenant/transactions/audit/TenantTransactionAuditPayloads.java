package com.financesystem.finance_api.modules.tenant.transactions.audit;

import com.financesystem.finance_api.modules.tenant.transactions.domain.model.QrTransactionIntent;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.Transaction;

import java.util.LinkedHashMap;
import java.util.Map;

public final class TenantTransactionAuditPayloads {

    private TenantTransactionAuditPayloads() {
    }

    public static Map<String, Object> details(Object... keyValues) {
        if (keyValues == null || keyValues.length == 0) {
            return Map.of();
        }

        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Audit details must contain key-value pairs");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            payload.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
        }
        return payload;
    }

    public static Map<String, Object> transactionState(Transaction transaction) {
        return details(
                "id", transaction.id(),
                "type", transaction.type(),
                "status", transaction.status(),
                "channel", transaction.channel(),
                "amount", transaction.amount(),
                "currency", transaction.currency(),
                "sourceAccountId", transaction.sourceAccountId(),
                "targetAccountId", transaction.targetAccountId(),
                "externalReference", transaction.externalReference(),
                "idempotencyKey", transaction.idempotencyKey(),
                "description", transaction.description(),
                "failureReason", transaction.failureReason(),
                "parentTransactionId", transaction.parentTransactionId(),
                "reversedTransactionId", transaction.reversedTransactionId(),
                "requestedByUserId", transaction.requestedByUserId(),
                "approvedByUserId", transaction.approvedByUserId(),
                "processedAt", transaction.processedAt(),
                "createdAt", transaction.createdAt(),
                "updatedAt", transaction.updatedAt()
        );
    }

    public static Map<String, Object> intentState(QrTransactionIntent intent) {
        return details(
                "id", intent.id(),
                "status", intent.status(),
                "channel", intent.channel(),
                "amount", intent.amount(),
                "currency", intent.currency(),
                "targetAccountId", intent.targetAccountId(),
                "externalReference", intent.externalReference(),
                "description", intent.description(),
                "idempotencyKey", intent.idempotencyKey(),
                "confirmedTransactionId", intent.confirmedTransactionId(),
                "requestedByUserId", intent.requestedByUserId(),
                "confirmedAt", intent.confirmedAt(),
                "createdAt", intent.createdAt(),
                "updatedAt", intent.updatedAt()
        );
    }
}
