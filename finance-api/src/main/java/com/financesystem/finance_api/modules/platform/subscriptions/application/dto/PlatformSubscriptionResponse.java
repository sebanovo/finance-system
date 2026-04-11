package com.financesystem.finance_api.modules.platform.subscriptions.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformSubscriptionResponse(
        UUID id,
        UUID tenantId,
        String tenantName,
        String tenantSlug,
        UUID planId,
        String planCode,
        String planName,
        String planType,
        int maxUsers,
        int maxRoles,
        String status,
        boolean trial,
        boolean currentSubscription,
        Instant startedAt,
        Instant expiresAt,
        Long remainingDays,
        Instant createdAt,
        Instant updatedAt
) {
}
