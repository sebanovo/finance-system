package com.financesystem.finance.modules.identity.access.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRoleJpaRepository extends JpaRepository<TenantRoleEntity, UUID> {

    Optional<TenantRoleEntity> findByName(String name);

    boolean existsByName(String name);
}