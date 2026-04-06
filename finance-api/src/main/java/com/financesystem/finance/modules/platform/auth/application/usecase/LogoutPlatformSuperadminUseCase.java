package com.financesystem.finance.modules.platform.auth.application.usecase;

import com.financesystem.finance.common.security.context.SecurityContextFacade;
import com.financesystem.finance.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance.modules.governance.audit.domain.model.AuditEventTypes;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LogoutPlatformSuperadminUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final AuditTrailService auditTrailService;

    public LogoutPlatformSuperadminUseCase(
            SecurityContextFacade securityContextFacade,
            AuditTrailService auditTrailService
    ) {
        this.securityContextFacade = securityContextFacade;
        this.auditTrailService = auditTrailService;
    }

    public void execute() {
        String currentSubject = securityContextFacade.getCurrentSubject();

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.LOGOUT,
                "PLATFORM_SUPERADMIN",
                currentSubject,
                Map.of("subject", currentSubject)
        );
    }
}
