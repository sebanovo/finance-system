package com.financesystem.finance_api.modules.platform.billing.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.billing.domain.model.StripeWebhookEvent;
import com.financesystem.finance_api.modules.platform.billing.domain.repository.StripeWebhookEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StripeWebhookEventRepositoryAdapter implements StripeWebhookEventRepository {

    private final StripeWebhookEventJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public StripeWebhookEventRepositoryAdapter(
            StripeWebhookEventJpaRepository jpaRepository,
            ObjectMapper objectMapper
    ) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public StripeWebhookEvent save(StripeWebhookEvent event) {
        return toDomain(jpaRepository.save(toEntity(event)));
    }

    @Override
    public Optional<StripeWebhookEvent> findByStripeEventId(String stripeEventId) {
        return jpaRepository.findByStripeEventId(stripeEventId).map(this::toDomain);
    }

    @Override
    public boolean existsByStripeEventId(String stripeEventId) {
        return jpaRepository.existsByStripeEventId(stripeEventId);
    }

    private StripeWebhookEventEntity toEntity(StripeWebhookEvent event) {
        StripeWebhookEventEntity entity = new StripeWebhookEventEntity();
        entity.setId(event.id());
        entity.setStripeEventId(event.stripeEventId());
        entity.setEventType(event.eventType());
        entity.setProcessed(event.processed());
        entity.setProcessedAt(event.processedAt());
        entity.setProcessingAttempts(event.processingAttempts());
        entity.setLastError(event.lastError());
        entity.setPayload(event.payload() == null ? null : event.payload().toString());
        return entity;
    }

    private StripeWebhookEvent toDomain(StripeWebhookEventEntity entity) {
        JsonNode payload = null;
        if (entity.getPayload() != null && !entity.getPayload().isBlank()) {
            try {
                payload = objectMapper.readTree(entity.getPayload());
            } catch (Exception ignored) {
                payload = null;
            }
        }
        return new StripeWebhookEvent(
                entity.getId(),
                entity.getStripeEventId(),
                entity.getEventType(),
                entity.isProcessed(),
                entity.getProcessedAt(),
                entity.getProcessingAttempts(),
                entity.getLastError(),
                payload,
                entity.getReceivedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
