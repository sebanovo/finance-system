package com.financesystem.finance.modules.identity.auth.application.usecase;

import com.financesystem.finance.common.security.context.SecurityContextFacade;
import com.financesystem.finance.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance.modules.identity.auth.application.dto.AuthenticatedTenantUserResponse;
import com.financesystem.finance.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetCurrentAuthenticatedTenantUserUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final TenantUserRepository tenantUserRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;

    public GetCurrentAuthenticatedTenantUserUseCase(
            SecurityContextFacade securityContextFacade,
            TenantUserRepository tenantUserRepository,
            TenantUserRoleRepository tenantUserRoleRepository
    ) {
        this.securityContextFacade = securityContextFacade;
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
    }

    public AuthenticatedTenantUserResponse execute() {
        String currentSubject = securityContextFacade.getCurrentSubject();
        String currentTenantSlug = securityContextFacade.getCurrentTenantSlug();

        if (currentSubject == null || currentSubject.isBlank()) {
            throw new AuthenticationFailedException("Authenticated subject is not available");
        }

        TenantUser tenantUser = tenantUserRepository.findByEmail(currentSubject)
                .orElseThrow(() -> new AuthenticationFailedException("Authenticated user not found"));

        List<String> roles = tenantUserRoleRepository.findRoleNamesByUserId(tenantUser.id());

        return new AuthenticatedTenantUserResponse(
                tenantUser.id(),
                tenantUser.email(),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status().name(),
                currentTenantSlug,
                roles
        );
    }
}