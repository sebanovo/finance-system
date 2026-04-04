package com.financesystem.finance.modules.governance.audit.application.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(
        UUID id,
        String actorSubject,
        String eventType,
        String resourceType,
        String resourceId,
        String eventDetails,
        Instant createdAt
) {
}