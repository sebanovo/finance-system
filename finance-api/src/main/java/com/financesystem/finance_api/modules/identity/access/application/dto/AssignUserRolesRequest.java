package com.financesystem.finance_api.modules.identity.access.application.dto;

import java.util.List;
import java.util.UUID;

public record AssignUserRolesRequest(
        List<UUID> roleIds
) {
}