package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceCustomerJpaRepository extends JpaRepository<ServiceCustomerEntity, UUID>, JpaSpecificationExecutor<ServiceCustomerEntity> {

    Optional<ServiceCustomerEntity> findByProviderIdAndServiceCustomerCode(UUID providerId, String serviceCustomerCode);

    boolean existsByProviderIdAndServiceCustomerCode(UUID providerId, String serviceCustomerCode);
}
