package com.financesystem.finance_api.modules.governance.notifications.application.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationCategory;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPriority;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationPublishRequest(
        UUID userId,
        NotificationType type,
        NotificationCategory category,
        NotificationPriority priority,
        String title,
        String body,
        JsonNode data,
        String imageUrl,
        String actionUrl,
        Instant expiresAt
) {
}
