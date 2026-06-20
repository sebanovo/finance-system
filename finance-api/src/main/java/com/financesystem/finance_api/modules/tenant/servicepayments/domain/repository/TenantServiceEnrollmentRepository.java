package com.financesystem.finance_api.modules.tenant.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.MyServiceEnrollmentFilter;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TenantServiceEnrollmentRepository {

    TenantServiceEnrollment save(TenantServiceEnrollment enrollment);

    Optional<TenantServiceEnrollment> findById(UUID id);

    Optional<TenantServiceEnrollment> findByUserIdAndProviderIdAndServiceCustomerCode(UUID userId, UUID providerId, String serviceCustomerCode);

    Page<TenantServiceEnrollment> findAll(MyServiceEnrollmentFilter filter, UUID userId, Pageable pageable);
}
