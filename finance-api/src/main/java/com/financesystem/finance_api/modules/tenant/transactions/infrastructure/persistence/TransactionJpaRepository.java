package com.financesystem.finance_api.modules.tenant.transactions.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    Optional<TransactionEntity> findByRequestedByUserIdAndIdempotencyKey(UUID requestedByUserId, String idempotencyKey);

    List<TransactionEntity> findAllByOrderByCreatedAtDesc();

    List<TransactionEntity> findAllByRequestedByUserIdOrderByCreatedAtDesc(UUID requestedByUserId);

    List<TransactionEntity> findAllByParentTransactionIdOrderByCreatedAtDesc(UUID parentTransactionId);

    @Query("""
            select t from TransactionEntity t
            where t.sourceAccountId = :accountId or t.targetAccountId = :accountId
            order by t.createdAt desc
            """)
    List<TransactionEntity> findAllByAccountId(UUID accountId);
}
