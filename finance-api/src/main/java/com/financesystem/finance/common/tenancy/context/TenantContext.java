package com.financesystem.finance.common.tenancy.context;

public record TenantContext(
        String tenantSlug,
        String schemaName,
        boolean publicRequest
) {
}