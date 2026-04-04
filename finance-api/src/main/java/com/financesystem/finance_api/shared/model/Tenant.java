package com.financesystem.finance_api.shared.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "public")
public class Tenant {
    @Id
    private String id;

    private String name;
    private LocalDate paidUntil;
    private boolean onTrial;
    private boolean isActive;
    // Usamos OffsetDateTime para coincidir con 'TIMESTAMP WITH TIME ZONE'
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPaidUntil() {
        return paidUntil;
    }

    public void setPaidUntil(LocalDate paidUntil) {
        this.paidUntil = paidUntil;
    }

    public boolean isOnTrial() {
        return onTrial;
    }

    public void setOnTrial(boolean onTrial) {
        this.onTrial = onTrial;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}