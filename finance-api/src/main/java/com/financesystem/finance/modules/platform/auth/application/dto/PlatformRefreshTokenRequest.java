package com.financesystem.finance.modules.platform.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record PlatformRefreshTokenRequest(
        @NotBlank
        String refreshToken
) {
}
