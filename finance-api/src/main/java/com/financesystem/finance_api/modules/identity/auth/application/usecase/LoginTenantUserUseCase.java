package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.auth.application.dto.AuthTokenResponse;
import com.financesystem.finance_api.modules.identity.auth.application.dto.LoginRequest;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LoginTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuditTrailService auditTrailService;

    public LoginTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.auditTrailService = auditTrailService;
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

        List<String> roles = tenantUserRoleRepository.findRoleNamesByUserId(tenantUser.id()).stream()
                .filter(this::isAuthorizableRole)
                .toList();
        List<String> permissions = tenantUserRoleRepository.findPermissionCodesByUserId(tenantUser.id());
        String subject = tenantUser.id().toString();

        String accessToken = jwtTokenService.generateAccessToken(
                subject,
                tenantContext.tenantSlug(),
                roles,
                permissions
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                subject,
                tenantContext.tenantSlug()
        );

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LOGIN,
                "USER",
                tenantUser.id().toString(),
                Map.of(
                        "email", tenantUser.email(),
                        "roles", roles
                )
        );

        return new AuthTokenResponse(
                "Bearer",
                accessToken,
                refreshToken,
                jwtProperties.getAccessExpirationMs()
        );
    }

    private boolean isAuthorizableRole(String roleName) {
        if (roleName == null) {
            return false;
        }

        return "ADMIN".equalsIgnoreCase(roleName.trim())
                || "OWNER_ADMIN".equalsIgnoreCase(roleName.trim());
    }
}
