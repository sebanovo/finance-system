package com.financesystem.finance.modules.identity.auth.application.usecase;

import com.financesystem.finance.common.security.JwtProperties;
import com.financesystem.finance.common.security.jwt.JwtTokenService;
import com.financesystem.finance.common.tenancy.context.TenantContext;
import com.financesystem.finance.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance.modules.identity.auth.application.dto.AuthTokenResponse;
import com.financesystem.finance.modules.identity.auth.application.dto.LoginRequest;
import com.financesystem.finance.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;

    public LoginTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
    }

    public AuthTokenResponse execute(LoginRequest request) {
        TenantContext tenantContext = TenantContextHolder.getRequired();
        String normalizedEmail = request.email().trim().toLowerCase();

        TenantUser tenantUser = tenantUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        if (!tenantUser.active()) {
            throw new AuthenticationFailedException("User is inactive");
        }

        if (!passwordEncoder.matches(request.password(), tenantUser.passwordHash())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        List<String> roles = tenantUserRoleRepository.findRoleNamesByUserId(tenantUser.id());

        String accessToken = jwtTokenService.generateAccessToken(
                tenantUser.email(),
                tenantContext.tenantSlug(),
                roles
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                tenantUser.email(),
                tenantContext.tenantSlug()
        );

        return new AuthTokenResponse(
                "Bearer",
                accessToken,
                refreshToken,
                jwtProperties.getAccessExpirationMs()
        );
    }
}