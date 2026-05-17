package com.financesystem.finance_api.modules.tenant.transactions.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.transactions.domain.model.QrTransactionIntent;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.QrTransactionIntentStatus;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.QrTransactionIntentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class QrTransactionIntentRepositoryAdapter implements QrTransactionIntentRepository {

    private final QrTransactionIntentJpaRepository jpaRepository;

    public QrTransactionIntentRepositoryAdapter(QrTransactionIntentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public QrTransactionIntent save(QrTransactionIntent intent) {
        return toDomain(jpaRepository.save(toEntity(intent)));
    }

    @Override
    public Optional<QrTransactionIntent> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<QrTransactionIntent> findByRequestedByUserIdAndIdempotencyKey(UUID requestedByUserId, String idempotencyKey) {
        return jpaRepository.findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey).map(this::toDomain);
    }

    private QrTransactionIntentEntity toEntity(QrTransactionIntent intent) {
        QrTransactionIntentEntity entity = new QrTransactionIntentEntity();
        entity.setId(intent.id());
        entity.setStatus(intent.status().name());
        entity.setChannel(intent.channel().name());
        entity.setAmount(intent.amount());
        entity.setCurrency(intent.currency());
        entity.setTargetAccountId(intent.targetAccountId());
        entity.setExternalReference(intent.externalReference());
        entity.setDescription(intent.description());
        entity.setIdempotencyKey(intent.idempotencyKey());
        entity.setConfirmedTransactionId(intent.confirmedTransactionId());
        entity.setRequestedByUserId(intent.requestedByUserId());
        entity.setConfirmedAt(intent.confirmedAt());
        return entity;
    }

    private QrTransactionIntent toDomain(QrTransactionIntentEntity entity) {
        return new QrTransactionIntent(
                entity.getId(),
                QrTransactionIntentStatus.valueOf(entity.getStatus()),
                TransactionChannel.valueOf(entity.getChannel()),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getTargetAccountId(),
                entity.getExternalReference(),
                entity.getDescription(),
                entity.getIdempotencyKey(),
                entity.getConfirmedTransactionId(),
                entity.getRequestedByUserId(),
                entity.getConfirmedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
