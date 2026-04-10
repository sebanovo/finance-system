package com.financesystem.finance_api.common.security.context;

import com.financesystem.finance_api.common.security.principal.AuthenticatedUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextFacade {

    public AuthenticatedUserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUserPrincipal authenticatedUserPrincipal) {
            return authenticatedUserPrincipal;
        }

        return null;
    }

    public String getCurrentSubject() {
        AuthenticatedUserPrincipal principal = getCurrentPrincipal();
        return principal != null ? principal.subject() : null;
    }

    public String getCurrentTenantSlug() {
        AuthenticatedUserPrincipal principal = getCurrentPrincipal();
        return principal != null ? principal.tenantSlug() : null;
    }
}