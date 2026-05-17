package com.financesystem.finance_api.modules.tenant.transactions.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionMovement;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionMovementType;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionMovementRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TransactionMovementRepositoryAdapter implements TransactionMovementRepository {

    private final TransactionMovementJpaRepository jpaRepository;

    public TransactionMovementRepositoryAdapter(TransactionMovementJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TransactionMovement save(TransactionMovement transactionMovement) {
        TransactionMovementEntity entity = toEntity(transactionMovement);
        TransactionMovementEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<TransactionMovement> saveAll(List<TransactionMovement> transactionMovements) {
        List<TransactionMovementEntity> entities = transactionMovements.stream()
                .map(this::toEntity)
                .toList();

        return jpaRepository.saveAll(entities)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<TransactionMovement> findAllByTransactionId(UUID transactionId) {
        return jpaRepository.findAllByTransactionIdOrderByCreatedAtAsc(transactionId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private TransactionMovementEntity toEntity(TransactionMovement transactionMovement) {
        TransactionMovementEntity entity = new TransactionMovementEntity();
        entity.setId(transactionMovement.id());
        entity.setTransactionId(transactionMovement.transactionId());
        entity.setAccountId(transactionMovement.accountId());
        entity.setMovementType(transactionMovement.movementType().name());
        entity.setAmount(transactionMovement.amount());
        entity.setCurrency(transactionMovement.currency());
        entity.setBalanceBefore(transactionMovement.balanceBefore());
        entity.setBalanceAfter(transactionMovement.balanceAfter());
        entity.setDescription(transactionMovement.description());
        return entity;
    }

    private TransactionMovement toDomain(TransactionMovementEntity entity) {
        return new TransactionMovement(
                entity.getId(),
                entity.getTransactionId(),
                entity.getAccountId(),
                TransactionMovementType.valueOf(entity.getMovementType()),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getBalanceBefore(),
                entity.getBalanceAfter(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }
}
