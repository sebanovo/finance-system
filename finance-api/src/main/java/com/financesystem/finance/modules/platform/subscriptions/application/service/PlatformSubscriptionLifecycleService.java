package com.financesystem.finance.modules.platform.subscriptions.application.service;

import com.financesystem.finance.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance.modules.platform.subscriptions.domain.model.PlatformSubscriptionStatus;
import com.financesystem.finance.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PlatformSubscriptionLifecycleService {

    private final PlatformSubscriptionRepository platformSubscriptionRepository;

    public PlatformSubscriptionLifecycleService(
            PlatformSubscriptionRepository platformSubscriptionRepository
    ) {
        this.platformSubscriptionRepository = platformSubscriptionRepository;
    }

    @Transactional
    public void refreshExpiredSubscriptions() {
        List<PlatformSubscription> subscriptions = platformSubscriptionRepository.findAll();

        for (PlatformSubscription subscription : subscriptions) {
            if (!subscription.currentSubscription()) {
                continue;
            }

            if (subscription.expiresAt() == null) {
                continue;
            }

            if (subscription.status() == PlatformSubscriptionStatus.EXPIRED
                    || subscription.status() == PlatformSubscriptionStatus.CANCELLED) {
                continue;
            }

            if (subscription.expiresAt().isBefore(Instant.now())) {
                PlatformSubscription expired = new PlatformSubscription(
                        subscription.id(),
                        subscription.tenantId(),
                        subscription.planId(),
                        PlatformSubscriptionStatus.EXPIRED,
                        subscription.trial(),
                        subscription.currentSubscription(),
                        subscription.startedAt(),
                        subscription.expiresAt(),
                        subscription.createdAt(),
                        subscription.updatedAt()
                );

                platformSubscriptionRepository.save(expired);
            }
        }
    }
}
