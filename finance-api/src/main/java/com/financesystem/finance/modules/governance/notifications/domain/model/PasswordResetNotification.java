package com.financesystem.finance.modules.governance.notifications.domain.model;

import java.time.Instant;

public record PasswordResetNotification(
        String email,
        String tenantSlug,
        String resetToken,
        Instant expiresAt
) {
}