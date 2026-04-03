package com.financesystem.finance_api.multitenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantResolutionFilter extends OncePerRequestFilter {
    public static final String TENANT_HEADER = "X-Tenant-ID";

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tenant = resolveTenantFromRequest(request);

        try {
            TenantContext.setTenant(tenant);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String resolveTenantFromRequest(HttpServletRequest request) {
        // Primero intentar obtener del request attribute set by JwtAuthenticationFilter
        String tenantFromJwt = (String) request.getAttribute("tenantIdFromJwt");
        if (tenantFromJwt != null && !tenantFromJwt.isBlank()) {
            return tenantFromJwt;
        }

        // Segundo intentar obtener del header X-Tenant-ID
        String tenant = request.getHeader(TENANT_HEADER);
        if (tenant != null && !tenant.isBlank()) {
            return tenant.trim();
        }

        // // Tercero intentar obtener del subdominio
        // String host = request.getHeader("Host");
        // if (host != null && host.contains(".")) {
        // String subdomain = host.split("\\.")[0];
        // if (!subdomain.isBlank() && !subdomain.equalsIgnoreCase("localhost")) {
        // return subdomain;
        // }
        // }

        // // Si no se encuentra, retornar null para usar default después
        return null;
    }
}
