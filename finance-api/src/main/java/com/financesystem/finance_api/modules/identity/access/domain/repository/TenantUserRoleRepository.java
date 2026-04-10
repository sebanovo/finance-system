package com.financesystem.finance_api.modules.identity.access.domain.repository;

import java.util.List;
import java.util.UUID;

public interface TenantUserRoleRepository {

    List<UUID> findRoleIdsByUserId(UUID userId);

    List<String> findRoleNamesByUserId(UUID userId);

    void replaceUserRoles(UUID userId, List<UUID> roleIds);
}