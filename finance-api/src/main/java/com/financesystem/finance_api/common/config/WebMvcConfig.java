package com.financesystem.finance_api.common.config;

import com.financesystem.finance_api.common.tenancy.interceptor.TenantContextInterceptor;
import com.financesystem.finance_api.common.tenancy.maintenance.TenantMaintenanceInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TenantContextInterceptor tenantContextInterceptor;
    private final TenantMaintenanceInterceptor tenantMaintenanceInterceptor;

    public WebMvcConfig(
            TenantContextInterceptor tenantContextInterceptor,
            TenantMaintenanceInterceptor tenantMaintenanceInterceptor
    ) {
        this.tenantContextInterceptor = tenantContextInterceptor;
        this.tenantMaintenanceInterceptor = tenantMaintenanceInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantContextInterceptor)
                .addPathPatterns("/**")
                .order(0);

        registry.addInterceptor(tenantMaintenanceInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/platform/**",
                        "/api/public/**",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/actuator/**"
                )
                .order(1);
    }
}
