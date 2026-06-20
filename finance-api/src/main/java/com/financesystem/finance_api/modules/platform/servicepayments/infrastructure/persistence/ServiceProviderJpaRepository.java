package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceProviderJpaRepository extends JpaRepository<ServiceProviderEntity, UUID>, JpaSpecificationExecutor<ServiceProviderEntity> {

    Optional<ServiceProviderEntity> findByCode(String code);

    boolean existsByCode(String code);
}
