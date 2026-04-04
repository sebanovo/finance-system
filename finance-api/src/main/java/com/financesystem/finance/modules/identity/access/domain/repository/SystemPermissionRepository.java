package com.financesystem.finance.modules.identity.access.domain.repository;

import com.financesystem.finance.modules.identity.access.domain.model.SystemPermission;

import java.util.List;
import java.util.Set;

public interface SystemPermissionRepository {

    List<SystemPermission> findAllActive();

    Set<String> findActiveCodes();
}