package com.financesystem.finance_api.config;

import com.financesystem.finance_api.multitenant.DataSourceBasedMultiTenantConnectionProviderImpl;
import com.financesystem.finance_api.multitenant.TenantIdentifierResolver;
import com.financesystem.finance_api.multitenant.TenantResolutionFilter;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class MultiTenantConfig {

    @Bean
    public MultiTenantConnectionProvider<String> multiTenantConnectionProvider(DataSource dataSource) {
        return new DataSourceBasedMultiTenantConnectionProviderImpl(dataSource);
    }

    @Bean
    public CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolver() {
        return new TenantIdentifierResolver();
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(MultiTenantConnectionProvider<String> provider,
            CurrentTenantIdentifierResolver<String> resolver) {
        return (Map<String, Object> props) -> {
            props.put(org.hibernate.cfg.AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, provider);
            props.put(org.hibernate.cfg.AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, resolver);
        };
    }

    @Bean
    public FilterRegistrationBean<TenantResolutionFilter> tenantFilterRegistration() {
        FilterRegistrationBean<TenantResolutionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TenantResolutionFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
