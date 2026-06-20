package com.financesystem.finance_api.modules.identity.auth.application.dto;

public record CurrentTenantProfilePhotoResponse(
        byte[] bytes,
        String contentType,
        String filename
) {
}
