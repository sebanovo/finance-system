package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_providers")
public class ServiceProviderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ServiceProviderCategory category;

    @Column(name = "service_customer_code_label", nullable = false, length = 100)
    private String serviceCustomerCodeLabel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceProviderStatus status;

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
            status = ServiceProviderStatus.ACTIVE;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceProviderCategory getCategory() {
        return category;
    }

    public void setCategory(ServiceProviderCategory category) {
        this.category = category;
    }

    public String getServiceCustomerCodeLabel() {
        return serviceCustomerCodeLabel;
    }

    public void setServiceCustomerCodeLabel(String serviceCustomerCodeLabel) {
        this.serviceCustomerCodeLabel = serviceCustomerCodeLabel;
    }

    public ServiceProviderStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceProviderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
