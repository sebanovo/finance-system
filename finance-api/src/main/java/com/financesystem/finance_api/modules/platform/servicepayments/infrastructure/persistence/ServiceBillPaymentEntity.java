package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_bill_payments")
public class ServiceBillPaymentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "bill_id", nullable = false)
    private UUID billId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "paid_by_tenant_id", nullable = false)
    private UUID paidByTenantId;

    @Column(name = "paid_by_tenant_slug", nullable = false, length = 100)
    private String paidByTenantSlug;

    @Column(name = "paid_by_user_id", nullable = false)
    private UUID paidByUserId;

    @Column(name = "paid_by_account_id", nullable = false)
    private UUID paidByAccountId;

    @Column(name = "paid_by_account_number", nullable = false, length = 50)
    private String paidByAccountNumber;

    @Column(name = "paid_transaction_id", nullable = false)
    private UUID paidTransactionId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "receipt_number", nullable = false, unique = true, length = 100)
    private String receiptNumber;

    @Column(name = "idempotency_key", nullable = false, length = 150)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceBillPaymentStatus status;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (paidAt == null) {
            paidAt = now;
        }
        if (status == null) {
            status = ServiceBillPaymentStatus.PAID;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBillId() {
        return billId;
    }

    public void setBillId(UUID billId) {
        this.billId = billId;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
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

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public ServiceBillPaymentStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceBillPaymentStatus status) {
        this.status = status;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
