package com.financesystem.finance.modules.identity.access.application.dto;

public record SystemPermissionResponse(
        String code,
        String module,
        String description
) {
}