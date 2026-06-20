package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceProviderRepository {

    ServiceProvider save(ServiceProvider serviceProvider);

    Optional<ServiceProvider> findById(UUID id);

    Optional<ServiceProvider> findByCode(String code);

    boolean existsByCode(String code);

    Page<ServiceProvider> findAll(PlatformServiceProviderFilter filter, Pageable pageable);
}
