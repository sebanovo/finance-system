package com.financesystem.finance.modules.identity.auth.application.dto;

public record AuthTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long accessExpiresInMs
) {
}