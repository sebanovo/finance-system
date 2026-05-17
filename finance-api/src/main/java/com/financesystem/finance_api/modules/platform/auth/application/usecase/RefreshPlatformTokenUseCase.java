package com.financesystem.finance_api.modules.platform.auth.application.usecase;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.jwt.JwtClaimNames;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformAuthTokenResponse;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformRefreshTokenRequest;
import com.financesystem.finance_api.modules.platform.auth.domain.model.PlatformAuthConstants;
import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RefreshPlatformTokenUseCase {

    private final PlatformSuperadminRepository platformSuperadminRepository;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuditTrailService auditTrailService;

    public RefreshPlatformTokenUseCase(
            PlatformSuperadminRepository platformSuperadminRepository,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuditTrailService auditTrailService
    ) {
        this.platformSuperadminRepository = platformSuperadminRepository;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.auditTrailService = auditTrailService;
    }

    public PlatformAuthTokenResponse execute(PlatformRefreshTokenRequest request) {
        Claims claims = jwtTokenService.parseRefreshToken(request.refreshToken());

        String subject = claims.getSubject();
        String tokenTenant = claims.get(JwtClaimNames.TENANT, String.class);

        if (subject == null || subject.isBlank()) {
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        if (!PlatformAuthConstants.PLATFORM_TENANT_SLUG.equalsIgnoreCase(tokenTenant)) {
            throw new AuthenticationFailedException("Refresh token does not belong to platform auth");
        }

        PlatformSuperadmin superadmin = platformSuperadminRepository.findByEmail(subject)
                .orElseThrow(() -> new AuthenticationFailedException("Platform superadmin not found"));

        if (!superadmin.active()) {
            throw new AuthenticationFailedException("Platform superadmin is inactive");
        }

        String accessToken = jwtTokenService.generateAccessToken(
                superadmin.email(),
                PlatformAuthConstants.PLATFORM_TENANT_SLUG,
                PlatformAuthConstants.PLATFORM_ROLES,
                List.of()
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                superadmin.email(),
                PlatformAuthConstants.PLATFORM_TENANT_SLUG
        );

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.TOKEN_REFRESHED,
                "PLATFORM_SUPERADMIN",
                superadmin.id().toString(),
                Map.of("email", superadmin.email())
        );

        return new PlatformAuthTokenResponse(
                "Bearer",
                accessToken,
                refreshToken,
                jwtProperties.getAccessExpirationMs()
        );
    }
}
