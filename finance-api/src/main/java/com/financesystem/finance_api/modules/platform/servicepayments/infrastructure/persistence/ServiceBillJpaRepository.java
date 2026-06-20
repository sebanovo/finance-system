package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface ServiceBillJpaRepository extends JpaRepository<ServiceBillEntity, UUID>, JpaSpecificationExecutor<ServiceBillEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ServiceBillEntity> findLockedById(UUID id);

    Optional<ServiceBillEntity> findByProviderIdAndServiceCustomerCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);

    boolean existsByProviderIdAndServiceCustomerCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);
}
