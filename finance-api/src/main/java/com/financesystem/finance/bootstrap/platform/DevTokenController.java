package com.financesystem.finance.bootstrap.platform;

import com.financesystem.finance.common.response.ApiResponse;
import com.financesystem.finance.common.security.jwt.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/dev-token")
@Profile("dev")
public class DevTokenController {

    private final JwtTokenService jwtTokenService;

    public DevTokenController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    public ApiResponse<DevTokenResponse> generateToken(@Valid @RequestBody DevTokenRequest request) {
        List<String> roles = request.roles() == null || request.roles().isEmpty()
                ? List.of("USER")
                : request.roles();

        String accessToken = jwtTokenService.generateAccessToken(
                request.email(),
                request.tenantSlug(),
                roles
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                request.email(),
                request.tenantSlug()
        );

        return ApiResponse.success(
                "Development token generated successfully",
                new DevTokenResponse("Bearer", accessToken, refreshToken)
        );
    }
}