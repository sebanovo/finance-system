package com.financesystem.finance_api.modules.platform.billing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionCheckoutSessionJpaRepository extends JpaRepository<SubscriptionCheckoutSessionEntity, UUID> {

    Optional<SubscriptionCheckoutSessionEntity> findByStripeSessionId(String stripeSessionId);

    Optional<SubscriptionCheckoutSessionEntity> findByStripeSubscriptionId(String stripeSubscriptionId);

    List<SubscriptionCheckoutSessionEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}
