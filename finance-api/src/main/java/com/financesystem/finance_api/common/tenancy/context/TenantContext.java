package com.financesystem.finance_api.common.tenancy.context;

public record TenantContext(
        String tenantSlug,
        String schemaName,
        boolean publicRequest
) {
}