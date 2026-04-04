package com.financesystem.finance.modules.platform.tenants.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlatformTenantJpaRepository extends JpaRepository<PlatformTenantEntity, UUID> {

    boolean existsBySlug(String slug);

    boolean existsBySchemaName(String schemaName);
}