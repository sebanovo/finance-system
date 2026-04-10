package com.financesystem.finance_api.modules.identity.access.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SystemPermissionJpaRepository extends JpaRepository<SystemPermissionEntity, UUID> {

    List<SystemPermissionEntity> findByActiveTrueOrderByModuleAscCodeAsc();
}