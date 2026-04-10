package com.financesystem.finance_api.modules.governance.audit.application.mapper;

import com.financesystem.finance_api.modules.governance.audit.application.dto.AuditEventResponse;
import com.financesystem.finance_api.modules.governance.audit.domain.model.PlatformAuditEvent;
import com.financesystem.finance_api.modules.governance.audit.domain.model.TenantAuditEvent;
import org.springframework.stereotype.Component;

@Component
public class AuditEventMapper {

    public AuditEventResponse toResponse(PlatformAuditEvent event) {
        return new AuditEventResponse(
                event.id(),
                event.actorSubject(),
                event.eventType(),
                event.resourceType(),
                event.resourceId(),
                event.eventDetails(),
                event.createdAt()
        );
    }

    public AuditEventResponse toResponse(TenantAuditEvent event) {
        return new AuditEventResponse(
                event.id(),
                event.actorSubject(),
                event.eventType(),
                event.resourceType(),
                event.resourceId(),
                event.eventDetails(),
                event.createdAt()
        );
    }
}