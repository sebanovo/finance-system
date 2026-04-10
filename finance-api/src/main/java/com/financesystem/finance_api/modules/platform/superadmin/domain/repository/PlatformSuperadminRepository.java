package com.financesystem.finance_api.modules.platform.superadmin.domain.repository;

import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;

import java.util.Optional;

public interface PlatformSuperadminRepository {

    PlatformSuperadmin save(PlatformSuperadmin superadmin);

    Optional<PlatformSuperadmin> findByEmail(String email);

    boolean existsByEmail(String email);
}