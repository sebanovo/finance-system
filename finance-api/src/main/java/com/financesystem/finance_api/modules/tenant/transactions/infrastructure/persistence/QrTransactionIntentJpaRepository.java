package com.financesystem.finance_api.modules.tenant.transactions.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QrTransactionIntentJpaRepository extends JpaRepository<QrTransactionIntentEntity, UUID> {

    Optional<QrTransactionIntentEntity> findByRequestedByUserIdAndIdempotencyKey(UUID requestedByUserId, String idempotencyKey);
}
