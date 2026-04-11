package com.financesystem.finance_api.bootstrap.platform;

public record DevTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken
) {
}