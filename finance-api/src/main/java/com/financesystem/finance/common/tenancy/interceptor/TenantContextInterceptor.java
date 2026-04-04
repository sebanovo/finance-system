package com.financesystem.finance.common.tenancy.interceptor;

import com.financesystem.finance.common.tenancy.TenancyProperties;
import com.financesystem.finance.common.tenancy.context.TenantContext;
import com.financesystem.finance.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance.common.tenancy.resolver.TenantResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantContextInterceptor implements HandlerInterceptor {

    private final TenancyProperties tenancyProperties;
    private final TenantResolver tenantResolver;

    public TenantContextInterceptor(
            TenancyProperties tenancyProperties,
            TenantResolver tenantResolver
    ) {
        this.tenancyProperties = tenancyProperties;
        this.tenantResolver = tenantResolver;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        String requestPath = request.getRequestURI();

        if (tenancyProperties.usesPublicSchema(requestPath)) {
            TenantContextHolder.set(new TenantContext(
                    null,
                    tenancyProperties.getPublicSchema(),
                    true
            ));
            return true;
        }

        TenantContext resolvedTenant = tenantResolver.resolve(request);
        TenantContextHolder.set(resolvedTenant);
        return true;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex
    ) {
        TenantContextHolder.clear();
    }
}