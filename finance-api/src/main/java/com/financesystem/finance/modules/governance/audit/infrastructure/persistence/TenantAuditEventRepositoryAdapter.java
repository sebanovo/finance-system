package com.financesystem.finance.modules.governance.audit.infrastructure.persistence;

import com.financesystem.finance.modules.governance.audit.domain.model.TenantAuditEvent;
import com.financesystem.finance.modules.governance.audit.domain.repository.TenantAuditEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TenantAuditEventRepositoryAdapter implements TenantAuditEventRepository {

    private final TenantAuditEventJpaRepository jpaRepository;

    public TenantAuditEventRepositoryAdapter(TenantAuditEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TenantAuditEvent save(TenantAuditEvent event) {
        TenantAuditEventEntity entity = toEntity(event);
        TenantAuditEventEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<TenantAuditEvent> findRecent(int limit) {
        return jpaRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private TenantAuditEventEntity toEntity(TenantAuditEvent event) {
        TenantAuditEventEntity entity = new TenantAuditEventEntity();
        entity.setId(event.id());
        entity.setActorSubject(event.actorSubject());
        entity.setEventType(event.eventType());
        entity.setResourceType(event.resourceType());
        entity.setResourceId(event.resourceId());
        entity.setEventDetails(event.eventDetails());
        return entity;
    }

    private TenantAuditEvent toDomain(TenantAuditEventEntity entity) {
        return new TenantAuditEvent(
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