package com.financesystem.finance_api.modules.tenant.transactions.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionMovementJpaRepository extends JpaRepository<TransactionMovementEntity, UUID> {

    List<TransactionMovementEntity> findAllByTransactionIdOrderByCreatedAtAsc(UUID transactionId);
}
