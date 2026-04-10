package com.financesystem.finance_api.modules.platform.plans.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PlatformPlan(
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
