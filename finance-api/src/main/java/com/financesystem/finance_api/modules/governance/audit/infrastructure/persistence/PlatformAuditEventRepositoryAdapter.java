package com.financesystem.finance_api.modules.governance.audit.infrastructure.persistence;

import com.financesystem.finance_api.modules.governance.audit.domain.model.PlatformAuditEvent;
import com.financesystem.finance_api.modules.governance.audit.domain.repository.PlatformAuditEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlatformAuditEventRepositoryAdapter implements PlatformAuditEventRepository {

    private final PlatformAuditEventJpaRepository jpaRepository;

    public PlatformAuditEventRepositoryAdapter(PlatformAuditEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PlatformAuditEvent save(PlatformAuditEvent event) {
        PlatformAuditEventEntity entity = toEntity(event);
        PlatformAuditEventEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<PlatformAuditEvent> findRecent(int limit) {
        return jpaRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private PlatformAuditEventEntity toEntity(PlatformAuditEvent event) {
        PlatformAuditEventEntity entity = new PlatformAuditEventEntity();
        entity.setId(event.id());
        entity.setActorSubject(event.actorSubject());
        entity.setEventType(event.eventType());
        entity.setResourceType(event.resourceType());
        entity.setResourceId(event.resourceId());
        entity.setEventDetails(event.eventDetails());
        return entity;
    }

    private PlatformAuditEvent toDomain(PlatformAuditEventEntity entity) {
        return new PlatformAuditEvent(
                entity.getId(),
                entity.getActorSubject(),
                entity.getEventType(),
                entity.getResourceType(),
                entity.getResourceId(),
                entity.getEventDetails(),
                entity.getCreatedAt()
        );
    }
}