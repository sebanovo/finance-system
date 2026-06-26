package com.financesystem.finance_api.modules.platform.tenants.domain.repository;

import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatformTenantRepository {

    PlatformTenant save(PlatformTenant tenant);

    Optional<PlatformTenant> findById(UUID id);

    Optional<PlatformTenant> findBySlug(String slug);

    Optional<PlatformTenant> findByStripeCustomerId(String stripeCustomerId);

    List<PlatformTenant> findAll();

    boolean existsBySlug(String slug);

    boolean existsBySchemaName(String schemaName);
}
