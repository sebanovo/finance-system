package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.jwt.JwtClaimNames;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.auth.application.dto.AuthTokenResponse;
import com.financesystem.finance_api.modules.identity.auth.application.dto.RefreshTokenRequest;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RefreshTenantTokenUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuditTrailService auditTrailService;

    public RefreshTenantTokenUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.auditTrailService = auditTrailService;
    }

    public AuthTokenResponse execute(RefreshTokenRequest request) {
        TenantContext tenantContext = TenantContextHolder.getRequired();

        Claims claims = jwtTokenService.parseRefreshToken(request.refreshToken());

        String subject = claims.getSubject();
        String tokenTenant = claims.get(JwtClaimNames.TENANT, String.class);

        if (subject == null || subject.isBlank()) {
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        if (tokenTenant == null || tokenTenant.isBlank()) {
            throw new AuthenticationFailedException("Invalid refresh token tenant");
        }

        if (!tokenTenant.equalsIgnoreCase(tenantContext.tenantSlug())) {
            throw new AuthenticationFailedException("Refresh token tenant does not match current tenant");
        }

        UUID userId = parseSubjectAsUserId(subject);

        TenantUser tenantUser = tenantUserRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationFailedException("User not found for refresh token"));

        if (!tenantUser.active()) {
            throw new AuthenticationFailedException("User is inactive");
        }

        List<String> roles = tenantUserRoleRepository.findRoleNamesByUserId(tenantUser.id()).stream()
                .filter(this::isAuthorizableRole)
                .toList();
        List<String> permissions = tenantUserRoleRepository.findPermissionCodesByUserId(tenantUser.id());

        String newAccessToken = jwtTokenService.generateAccessToken(
                tenantUser.id().toString(),
                tenantContext.tenantSlug(),
                roles,
                permissions
        );

        String newRefreshToken = jwtTokenService.generateRefreshToken(
                tenantUser.id().toString(),
                tenantContext.tenantSlug()
        );

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TOKEN_REFRESHED,
                "USER",
                tenantUser.id().toString(),
                Map.of("email", tenantUser.email())
        );

        return new AuthTokenResponse(
                "Bearer",
                newAccessToken,
                newRefreshToken,
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

    private UUID parseSubjectAsUserId(String subject) {
        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new AuthenticationFailedException("Invalid refresh token subject");
        }
    }
}
