package com.financesystem.finance_api.modules.platform.billing.domain.repository;

import com.financesystem.finance_api.modules.platform.billing.domain.model.SubscriptionCheckoutSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionCheckoutSessionRepository {

    SubscriptionCheckoutSession save(SubscriptionCheckoutSession session);

    Optional<SubscriptionCheckoutSession> findById(UUID id);

    Optional<SubscriptionCheckoutSession> findByStripeSessionId(String stripeSessionId);

    Optional<SubscriptionCheckoutSession> findByStripeSubscriptionId(String stripeSubscriptionId);

    Optional<UUID> findByTenantIdFromStripeSubscription(String stripeSubscriptionId);

    List<SubscriptionCheckoutSession> findByTenantId(UUID tenantId);
}
