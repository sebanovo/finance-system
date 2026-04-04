package com.financesystem.finance.common.tenancy.resolver;

import com.financesystem.finance.common.tenancy.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;

public interface TenantResolver {

    TenantContext resolve(HttpServletRequest request);
}