package com.financesystem.finance_api.modules.platform.tenants.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformTenantResponse(
        UUID id,
        String name,
        String slug,
        String schemaName,
        String status,
        UUID planId,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}