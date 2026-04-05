package com.financesystem.finance.modules.platform.subscriptions.infrastructure.persistence;

import com.financesystem.finance.modules.platform.subscriptions.domain.model.PlatformSubscriptionStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "platform_subscriptions")
public class PlatformSubscriptionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID planId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PlatformSubscriptionStatus status;

    @Column(nullable = false)
    private boolean isTrial;

    @Column(nullable = false)
    private boolean currentSubscription;

    @Column(nullable = false)
    private Instant startedAt;

    @Column
    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public PlatformSubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(PlatformSubscriptionStatus status) {
        this.status = status;
    }

    public boolean isTrial() {
        return isTrial;
    }

    public void setTrial(boolean trial) {
        isTrial = trial;
    }

    public boolean isCurrentSubscription() {
        return currentSubscription;
    }

    public void setCurrentSubscription(boolean currentSubscription) {
        this.currentSubscription = currentSubscription;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
