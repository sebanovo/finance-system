package com.financesystem.finance.modules.identity.auth.application.usecase;

import com.financesystem.finance.common.exception.BusinessException;
import com.financesystem.finance.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance.modules.identity.auth.application.dto.ResetPasswordRequest;
import com.financesystem.finance.modules.identity.auth.domain.exception.InvalidPasswordResetTokenException;
import com.financesystem.finance.modules.identity.auth.domain.model.PasswordResetToken;
import com.financesystem.finance.modules.identity.auth.domain.repository.PasswordResetTokenRepository;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class ResetPasswordUseCase {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final TenantUserRepository tenantUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditTrailService auditTrailService;

    public ResetPasswordUseCase(
            PasswordResetTokenRepository passwordResetTokenRepository,
            TenantUserRepository tenantUserRepository,
            PasswordEncoder passwordEncoder,
            AuditTrailService auditTrailService
    ) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.tenantUserRepository = tenantUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public void execute(ResetPasswordRequest request) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.token().trim())
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Password reset token is invalid"));

        if (passwordResetToken.used()) {
            throw new InvalidPasswordResetTokenException("Password reset token has already been used");
        }

        if (passwordResetToken.expiresAt().isBefore(Instant.now())) {
            throw new InvalidPasswordResetTokenException("Password reset token has expired");
        }

        TenantUser tenantUser = tenantUserRepository.findByEmail(passwordResetToken.email())
                .orElseThrow(() -> new BusinessException("User associated with password reset token was not found"));

        if (!tenantUser.active()) {
            throw new BusinessException("User is inactive");
        }

        TenantUser updatedUser = new TenantUser(
                tenantUser.id(),
                tenantUser.email(),
                passwordEncoder.encode(request.newPassword()),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status(),
                tenantUser.createdAt(),
                tenantUser.updatedAt()
        );

        tenantUserRepository.save(updatedUser);

        Instant now = Instant.now();
        passwordResetTokenRepository.markUsed(passwordResetToken.token(), now);
        passwordResetTokenRepository.invalidateAllByEmail(passwordResetToken.email(), now);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.PASSWORD_RESET_COMPLETED,
                "USER",
                tenantUser.id().toString(),
                Map.of("email", tenantUser.email())
        );
    }
}