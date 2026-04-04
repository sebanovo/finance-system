package com.financesystem.finance.bootstrap.platform;

public record DevTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken
) {
}