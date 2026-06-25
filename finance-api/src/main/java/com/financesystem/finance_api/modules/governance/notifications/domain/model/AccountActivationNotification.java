package com.financesystem.finance_api.modules.governance.notifications.domain.model;

import java.time.Instant;

public record AccountActivationNotification(
        String email,
        String tenantSlug,
        String activationToken,
        Instant expiresAt
) {
}
