package com.financesystem.finance.modules.identity.auth.infrastructure.api;

import com.financesystem.finance.common.response.ApiResponse;
import com.financesystem.finance.modules.identity.auth.application.dto.*;
import com.financesystem.finance.modules.identity.auth.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginTenantUserUseCase loginTenantUserUseCase;
    private final RefreshTenantTokenUseCase refreshTenantTokenUseCase;
    private final GetCurrentAuthenticatedTenantUserUseCase getCurrentAuthenticatedTenantUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final LogoutTenantUserUseCase logoutTenantUserUseCase;

    public AuthController(
            LoginTenantUserUseCase loginTenantUserUseCase,
            RefreshTenantTokenUseCase refreshTenantTokenUseCase,
            GetCurrentAuthenticatedTenantUserUseCase getCurrentAuthenticatedTenantUserUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            LogoutTenantUserUseCase logoutTenantUserUseCase
    ) {
        this.loginTenantUserUseCase = loginTenantUserUseCase;
        this.refreshTenantTokenUseCase = refreshTenantTokenUseCase;
        this.getCurrentAuthenticatedTenantUserUseCase = getCurrentAuthenticatedTenantUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.logoutTenantUserUseCase = logoutTenantUserUseCase;
    }

    @PostMapping("/login")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(
                "Login successful",
                loginTenantUserUseCase.execute(request)
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(
                "Token refreshed successfully",
                refreshTenantTokenUseCase.execute(request)
        );
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Map<String, String>> logout() {
        logoutTenantUserUseCase.execute();

        return ApiResponse.success(
                "Logout successful",
                Map.of("status", "ok")
        );
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<AuthenticatedTenantUserResponse> me() {
        return ApiResponse.success(
                "Authenticated user retrieved successfully",
                getCurrentAuthenticatedTenantUserUseCase.execute()
        );
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        changePasswordUseCase.execute(request);

        return ApiResponse.success(
                "Password changed successfully",
                Map.of("status", "ok")
        );
    }
}