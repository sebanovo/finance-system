package com.financesystem.finance_api.modules.platform.auth.application.dto;

public record PlatformAuthTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long accessExpiresInMs
) {
}
