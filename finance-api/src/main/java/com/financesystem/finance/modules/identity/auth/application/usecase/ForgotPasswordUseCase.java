package com.financesystem.finance.modules.identity.auth.application.usecase;

import com.financesystem.finance.common.tenancy.context.TenantContext;
import com.financesystem.finance.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance.modules.governance.notifications.application.config.PasswordResetNotificationProperties;
import com.financesystem.finance.modules.governance.notifications.application.usecase.SendPasswordResetNotificationUseCase;
import com.financesystem.finance.modules.governance.notifications.domain.model.PasswordResetNotification;
import com.financesystem.finance.modules.identity.auth.application.dto.ForgotPasswordRequest;
import com.financesystem.finance.modules.identity.auth.domain.model.PasswordResetToken;
import com.financesystem.finance.modules.identity.auth.domain.repository.PasswordResetTokenRepository;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;

@Service
public class ForgotPasswordUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetNotificationProperties properties;
    private final SendPasswordResetNotificationUseCase sendPasswordResetNotificationUseCase;
    private final AuditTrailService auditTrailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public ForgotPasswordUseCase(
            TenantUserRepository tenantUserRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetNotificationProperties properties,
            SendPasswordResetNotificationUseCase sendPasswordResetNotificationUseCase,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.properties = properties;
        this.sendPasswordResetNotificationUseCase = sendPasswordResetNotificationUseCase;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public void execute(ForgotPasswordRequest request) {
        TenantContext tenantContext = TenantContextHolder.getRequired();
        String normalizedEmail = request.email().trim().toLowerCase();

        TenantUser tenantUser = tenantUserRepository.findByEmail(normalizedEmail).orElse(null);

        if (tenantUser == null || !tenantUser.active()) {
            return;
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getExpirationMinutes(), ChronoUnit.MINUTES);

        passwordResetTokenRepository.invalidateAllByEmail(normalizedEmail, now);

        String token = generateSecureToken();

        PasswordResetToken passwordResetToken = new PasswordResetToken(
                null,
                normalizedEmail,
                token,
                expiresAt,
                false,
                null,
                null
        );

        passwordResetTokenRepository.save(passwordResetToken);

        sendPasswordResetNotificationUseCase.execute(
                new PasswordResetNotification(
                        normalizedEmail,
                        tenantContext.tenantSlug(),
                        token,
                        expiresAt
                )
        );

        auditTrailService.recordTenantEvent(
                AuditEventTypes.PASSWORD_RESET_REQUESTED,
                "USER",
                tenantUser.id().toString(),
                Map.of("email", normalizedEmail)
        );
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}