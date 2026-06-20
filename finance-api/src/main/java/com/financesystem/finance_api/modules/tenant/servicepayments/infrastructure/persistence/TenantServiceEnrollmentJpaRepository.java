package com.financesystem.finance_api.modules.tenant.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TenantServiceEnrollmentJpaRepository extends JpaRepository<TenantServiceEnrollmentEntity, UUID>, JpaSpecificationExecutor<TenantServiceEnrollmentEntity> {

    Optional<TenantServiceEnrollmentEntity> findByUserIdAndProviderIdAndServiceCustomerCode(
            UUID userId,
            UUID providerId,
            String serviceCustomerCode
    );

    boolean existsByUserIdAndProviderIdAndServiceCustomerCodeAndStatus(
            UUID userId,
            UUID providerId,
            String serviceCustomerCode,
            TenantServiceEnrollmentStatus status
    );
}
