package com.financesystem.finance_api.common.security.principal;

import java.util.List;

public record AuthenticatedUserPrincipal(
        String subject,
        String tenantSlug,
        List<String> roles,
        List<String> permissions
) {
}
