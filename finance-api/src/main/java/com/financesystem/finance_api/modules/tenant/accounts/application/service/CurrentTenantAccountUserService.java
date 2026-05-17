package com.financesystem.finance_api.modules.tenant.accounts.application.service;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentTenantAccountUserService {

    private final SecurityContextFacade securityContextFacade;
    private final TenantUserRepository tenantUserRepository;

    public CurrentTenantAccountUserService(
            SecurityContextFacade securityContextFacade,
            TenantUserRepository tenantUserRepository
    ) {
        this.securityContextFacade = securityContextFacade;
        this.tenantUserRepository = tenantUserRepository;
    }

    public UUID getCurrentUserId() {
        String subject = securityContextFacade.getCurrentSubject();

        if (subject == null || subject.isBlank()) {
            throw new TenantUserNotFoundException("Authenticated tenant user not found");
        }

        TenantUser tenantUser = tenantUserRepository.findById(parseSubjectAsUserId(subject))
                .orElseThrow(() -> new TenantUserNotFoundException(
                        "Tenant user not found with subject: " + subject
                ));

        return tenantUser.id();
    }

    private UUID parseSubjectAsUserId(String subject) {
        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new TenantUserNotFoundException("Authenticated tenant user not found");
        }
    }
}
