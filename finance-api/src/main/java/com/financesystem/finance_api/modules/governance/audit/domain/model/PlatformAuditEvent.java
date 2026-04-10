package com.financesystem.finance_api.modules.governance.audit.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PlatformAuditEvent(
        UUID id,
        String actorSubject,
        String eventType,
        String resourceType,
        String resourceId,
        String eventDetails,
        Instant createdAt
) {
}