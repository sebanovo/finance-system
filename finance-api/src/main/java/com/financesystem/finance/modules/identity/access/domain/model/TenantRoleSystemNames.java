package com.financesystem.finance.modules.identity.access.domain.model;

import java.util.List;

public final class TenantRoleSystemNames {

    public static final List<String> DEFAULT_SYSTEM_ROLES = List.of(
            "OWNER_ADMIN",
            "ADMIN",
            "USER"
    );

    private TenantRoleSystemNames() {
    }

    public static boolean isSystemRole(String roleName) {
        if (roleName == null) {
            return false;
        }

        return DEFAULT_SYSTEM_ROLES.contains(roleName.trim().toUpperCase());
    }
}
