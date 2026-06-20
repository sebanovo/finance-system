package com.financesystem.finance_api.modules.tenant.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_service_enrollments")
public class TenantServiceEnrollmentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "provider_code", nullable = false, length = 80)
    private String providerCode;

    @Column(name = "provider_name", nullable = false, length = 150)
    private String providerName;

    @Column(name = "provider_category", nullable = false, length = 40)
    private String providerCategory;

    @Column(name = "service_customer_code", nullable = false, length = 100)
    private String serviceCustomerCode;

    @Column(name = "service_customer_name", nullable = false, length = 150)
    private String serviceCustomerName;

    @Column(length = 100)
    private String alias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TenantServiceEnrollmentStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = TenantServiceEnrollmentStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderCategory() {
        return providerCategory;
    }

    public void setProviderCategory(String providerCategory) {
        this.providerCategory = providerCategory;
    }

    public String getServiceCustomerCode() {
        return serviceCustomerCode;
    }

    public void setServiceCustomerCode(String serviceCustomerCode) {
        this.serviceCustomerCode = serviceCustomerCode;
    }

    public String getServiceCustomerName() {
        return serviceCustomerName;
    }

    public void setServiceCustomerName(String serviceCustomerName) {
        this.serviceCustomerName = serviceCustomerName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public TenantServiceEnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(TenantServiceEnrollmentStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
