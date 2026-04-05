package com.financesystem.finance.modules.platform.plans.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformPlanResponse(
        UUID id,
        String code,
        String name,
        String description,
        int maxUsers,
        int maxRoles,
        String planType,
        Integer trialDays,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
