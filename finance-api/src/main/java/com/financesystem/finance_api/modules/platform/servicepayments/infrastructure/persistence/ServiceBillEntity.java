package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "service_bills")
public class ServiceBillEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "service_customer_id", nullable = false)
    private UUID serviceCustomerId;

    @Column(name = "service_customer_code", nullable = false, length = 100)
    private String serviceCustomerCode;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "billing_period", nullable = false, length = 20)
    private String billingPeriod;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceBillStatus status;

    @Column(name = "paid_by_tenant_id")
    private UUID paidByTenantId;

    @Column(name = "paid_by_tenant_slug", length = 100)
    private String paidByTenantSlug;

    @Column(name = "paid_by_user_id")
    private UUID paidByUserId;

    @Column(name = "paid_by_account_id")
    private UUID paidByAccountId;

    @Column(name = "paid_by_account_number", length = 50)
    private String paidByAccountNumber;

    @Column(name = "paid_transaction_id")
    private UUID paidTransactionId;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "created_by_superadmin_id")
    private UUID createdBySuperadminId;

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
            status = ServiceBillStatus.PENDING;
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

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public UUID getServiceCustomerId() {
        return serviceCustomerId;
    }

    public void setServiceCustomerId(UUID serviceCustomerId) {
        this.serviceCustomerId = serviceCustomerId;
    }

    public String getServiceCustomerCode() {
        return serviceCustomerCode;
    }

    public void setServiceCustomerCode(String serviceCustomerCode) {
        this.serviceCustomerCode = serviceCustomerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public ServiceBillStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceBillStatus status) {
        this.status = status;
    }

    public UUID getPaidByTenantId() {
        return paidByTenantId;
    }

    public void setPaidByTenantId(UUID paidByTenantId) {
        this.paidByTenantId = paidByTenantId;
    }

    public String getPaidByTenantSlug() {
        return paidByTenantSlug;
    }

    public void setPaidByTenantSlug(String paidByTenantSlug) {
        this.paidByTenantSlug = paidByTenantSlug;
    }

    public UUID getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(UUID paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public UUID getPaidByAccountId() {
        return paidByAccountId;
    }

    public void setPaidByAccountId(UUID paidByAccountId) {
        this.paidByAccountId = paidByAccountId;
    }

    public String getPaidByAccountNumber() {
        return paidByAccountNumber;
    }

    public void setPaidByAccountNumber(String paidByAccountNumber) {
        this.paidByAccountNumber = paidByAccountNumber;
    }

    public UUID getPaidTransactionId() {
        return paidTransactionId;
    }

    public void setPaidTransactionId(UUID paidTransactionId) {
        this.paidTransactionId = paidTransactionId;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public UUID getCreatedBySuperadminId() {
        return createdBySuperadminId;
    }

    public void setCreatedBySuperadminId(UUID createdBySuperadminId) {
        this.createdBySuperadminId = createdBySuperadminId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
