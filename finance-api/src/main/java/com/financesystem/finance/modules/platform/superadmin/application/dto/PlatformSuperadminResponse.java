package com.financesystem.finance.modules.platform.superadmin.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformSuperadminResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}