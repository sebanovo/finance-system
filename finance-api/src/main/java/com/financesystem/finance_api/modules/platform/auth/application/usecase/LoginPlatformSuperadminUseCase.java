package com.financesystem.finance_api.modules.platform.auth.application.usecase;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformAuthTokenResponse;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformLoginRequest;
import com.financesystem.finance_api.modules.platform.auth.domain.model.PlatformAuthConstants;
import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginPlatformSuperadminUseCase {

    private final PlatformSuperadminRepository platformSuperadminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuditTrailService auditTrailService;

    public LoginPlatformSuperadminUseCase(
            PlatformSuperadminRepository platformSuperadminRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuditTrailService auditTrailService
    ) {
        this.platformSuperadminRepository = platformSuperadminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.auditTrailService = auditTrailService;
    }

    public PlatformAuthTokenResponse execute(PlatformLoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        PlatformSuperadmin superadmin = platformSuperadminRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        if (!superadmin.active()) {
            throw new AuthenticationFailedException("Platform superadmin is inactive");
        }

        if (!passwordEncoder.matches(request.password(), superadmin.passwordHash())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        String accessToken = jwtTokenService.generateAccessToken(
                superadmin.email(),
                PlatformAuthConstants.PLATFORM_TENANT_SLUG,
                PlatformAuthConstants.PLATFORM_ROLES
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                superadmin.email(),
                PlatformAuthConstants.PLATFORM_TENANT_SLUG
        );

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.LOGIN,
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
