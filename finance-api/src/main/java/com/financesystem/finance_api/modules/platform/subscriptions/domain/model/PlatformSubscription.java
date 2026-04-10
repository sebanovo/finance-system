package com.financesystem.finance_api.modules.platform.subscriptions.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PlatformSubscription(
        UUID id,
        UUID tenantId,
        UUID planId,
        PlatformSubscriptionStatus status,
        boolean trial,
        boolean currentSubscription,
        Instant startedAt,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt
) {
}
