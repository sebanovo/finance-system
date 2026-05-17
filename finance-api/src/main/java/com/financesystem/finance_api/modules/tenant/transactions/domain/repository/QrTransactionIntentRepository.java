package com.financesystem.finance_api.modules.tenant.transactions.domain.repository;

import com.financesystem.finance_api.modules.tenant.transactions.domain.model.QrTransactionIntent;

import java.util.Optional;
import java.util.UUID;

public interface QrTransactionIntentRepository {

    QrTransactionIntent save(QrTransactionIntent intent);

    Optional<QrTransactionIntent> findById(UUID id);

    Optional<QrTransactionIntent> findByRequestedByUserIdAndIdempotencyKey(UUID requestedByUserId, String idempotencyKey);
}
