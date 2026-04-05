package com.financesystem.finance.modules.identity.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantUserJpaRepository extends JpaRepository<TenantUserEntity, UUID> {

    Optional<TenantUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByActiveTrue();
}
