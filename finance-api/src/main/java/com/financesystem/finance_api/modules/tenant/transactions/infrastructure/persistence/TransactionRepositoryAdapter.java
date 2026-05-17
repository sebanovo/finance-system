package com.financesystem.finance_api.modules.tenant.transactions.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.transactions.domain.model.Transaction;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionStatus;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = toEntity(transaction);
        TransactionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Transaction> findByRequestedByUserIdAndIdempotencyKey(UUID requestedByUserId, String idempotencyKey) {
        return jpaRepository.findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey).map(this::toDomain);
    }

    @Override
    public List<Transaction> findAllByParentTransactionId(UUID parentTransactionId) {
        return jpaRepository.findAllByParentTransactionIdOrderByCreatedAtDesc(parentTransactionId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAll() {
        return jpaRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByAccountId(UUID accountId) {
        return jpaRepository.findAllByAccountId(accountId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByRequestedByUserId(UUID requestedByUserId) {
        return jpaRepository.findAllByRequestedByUserIdOrderByCreatedAtDesc(requestedByUserId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private TransactionEntity toEntity(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(transaction.id());
        entity.setType(transaction.type().name());
        entity.setStatus(transaction.status().name());
        entity.setChannel(transaction.channel().name());
        entity.setAmount(transaction.amount());
        entity.setCurrency(transaction.currency());
        entity.setSourceAccountId(transaction.sourceAccountId());
        entity.setTargetAccountId(transaction.targetAccountId());
        entity.setExternalReference(transaction.externalReference());
        entity.setIdempotencyKey(transaction.idempotencyKey());
        entity.setDescription(transaction.description());
        entity.setFailureReason(transaction.failureReason());
        entity.setMetadata(toJsonNode(transaction.metadata()));
        entity.setParentTransactionId(transaction.parentTransactionId());
        entity.setReversedTransactionId(transaction.reversedTransactionId());
        entity.setRequestedByUserId(transaction.requestedByUserId());
        entity.setApprovedByUserId(transaction.approvedByUserId());
        entity.setProcessedAt(transaction.processedAt());
        return entity;
    }

    private Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                TransactionType.valueOf(entity.getType()),
                TransactionStatus.valueOf(entity.getStatus()),
                TransactionChannel.valueOf(entity.getChannel()),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getSourceAccountId(),
                entity.getTargetAccountId(),
                entity.getExternalReference(),
                entity.getIdempotencyKey(),
                entity.getDescription(),
                entity.getFailureReason(),
                toJsonString(entity.getMetadata()),
                entity.getParentTransactionId(),
                entity.getReversedTransactionId(),
                entity.getRequestedByUserId(),
                entity.getApprovedByUserId(),
                entity.getProcessedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private JsonNode toJsonNode(String metadata) {
        if (metadata == null || metadata.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readTree(metadata);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Invalid transaction metadata JSON", exception);
        }
    }

    private String toJsonString(JsonNode metadata) {
        if (metadata == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize transaction metadata JSON", exception);
        }
    }
}
