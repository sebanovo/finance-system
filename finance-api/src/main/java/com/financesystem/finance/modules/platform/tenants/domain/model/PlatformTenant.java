package com.financesystem.finance.modules.platform.tenants.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PlatformTenant(
        UUID id,
        String name,
        String slug,
        String schemaName,
        PlatformTenantStatus status,
        UUID planId,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}