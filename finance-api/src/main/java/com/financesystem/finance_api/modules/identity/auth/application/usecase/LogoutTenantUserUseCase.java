package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LogoutTenantUserUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final AuditTrailService auditTrailService;

    public LogoutTenantUserUseCase(
            SecurityContextFacade securityContextFacade,
            AuditTrailService auditTrailService
    ) {
        this.securityContextFacade = securityContextFacade;
        this.auditTrailService = auditTrailService;
    }

    public void execute() {
        String currentSubject = securityContextFacade.getCurrentSubject();

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LOGOUT,
                "USER",
                currentSubject,
                Map.of("subject", currentSubject)
        );
    }
}