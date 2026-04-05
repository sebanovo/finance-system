package com.financesystem.finance.modules.identity.users.domain.repository;

import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantUserRepository {

    TenantUser save(TenantUser tenantUser);

    Optional<TenantUser> findById(UUID id);

    Optional<TenantUser> findByEmail(String email);

    List<TenantUser> findAll();

    boolean existsByEmail(String email);

    long countActiveUsers();
}
