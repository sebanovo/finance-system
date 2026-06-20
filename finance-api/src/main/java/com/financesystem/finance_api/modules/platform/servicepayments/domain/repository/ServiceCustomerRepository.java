package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceCustomerFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceCustomerRepository {

    ServiceCustomer save(ServiceCustomer serviceCustomer);

    Optional<ServiceCustomer> findById(UUID id);

    Optional<ServiceCustomer> findByProviderAndCode(UUID providerId, String serviceCustomerCode);

    boolean existsByProviderAndCode(UUID providerId, String serviceCustomerCode);

    Page<ServiceCustomer> findAll(PlatformServiceCustomerFilter filter, Pageable pageable);
}
