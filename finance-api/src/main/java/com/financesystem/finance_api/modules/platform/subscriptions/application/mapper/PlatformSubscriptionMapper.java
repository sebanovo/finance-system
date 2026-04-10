package com.financesystem.finance_api.modules.platform.subscriptions.application.mapper;

import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class PlatformSubscriptionMapper {

    public PlatformSubscriptionResponse toResponse(
            PlatformSubscription subscription,
            PlatformTenant tenant,
            PlatformPlan plan
    ) {
        Long remainingDays = null;

        if (subscription.expiresAt() != null) {
            long days = Duration.between(Instant.now(), subscription.expiresAt()).toDays();
            remainingDays = Math.max(days, 0);
        }

        return new PlatformSubscriptionResponse(
                subscription.id(),
                subscription.tenantId(),
                tenant.name(),
                tenant.slug(),
                subscription.planId(),
                plan.code(),
                plan.name(),
                plan.planType(),
                plan.maxUsers(),
                plan.maxRoles(),
                subscription.status().name(),
                subscription.trial(),
                subscription.currentSubscription(),
                subscription.startedAt(),
                subscription.expiresAt(),
                remainingDays,
                subscription.createdAt(),
                subscription.updatedAt()
        );
    }
}
