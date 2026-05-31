package com.financesystem.finance_api.modules.platform.auth.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformChangePasswordRequest;
import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ChangePlatformPasswordUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditTrailService auditTrailService;

    public ChangePlatformPasswordUseCase(
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository,
            PasswordEncoder passwordEncoder,
            AuditTrailService auditTrailService
    ) {
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public void execute(PlatformChangePasswordRequest request) {
        String currentSubject = securityContextFacade.getCurrentSubject();

        PlatformSuperadmin superadmin = platformSuperadminRepository.findByEmail(currentSubject)
                .orElseThrow(() -> new BusinessException("Authenticated platform superadmin was not found"));

        if (!passwordEncoder.matches(request.currentPassword(), superadmin.passwordHash())) {
            throw new BusinessException("Current password is incorrect");
        }

        if (request.currentPassword().equals(request.newPassword())) {
            throw new BusinessException("New password must be different from current password");
        }

        PlatformSuperadmin updated = new PlatformSuperadmin(
                superadmin.id(),
                superadmin.email(),
                passwordEncoder.encode(request.newPassword()),
                superadmin.firstName(),
                superadmin.lastName(),
                superadmin.active(),
                superadmin.createdAt(),
                superadmin.updatedAt()
        );

        platformSuperadminRepository.save(updated);

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.PASSWORD_CHANGED,
                "PLATFORM_SUPERADMIN",
                superadmin.id().toString(),
                PlatformAuditPayloads.details(
                        "email", superadmin.email(),
                        "changedBy", currentSubject
                )
        );
    }
}
