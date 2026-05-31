package com.financesystem.finance_api.common.tenancy.resolver;

import com.financesystem.finance_api.common.tenancy.TenancyProperties;
import com.financesystem.finance_api.common.tenancy.exception.TenantResolutionException;
import com.financesystem.finance_api.common.tenancy.schema.TenantSchemaNamingStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HeaderTenantResolverTest {

    @Test
    void shouldResolveRegisteredTenant() {
        TenancyProperties properties = new TenancyProperties();
        properties.setHeaderName("X-Tenant-Slug");
        properties.setTenantSchemaPrefix("tenant_");

        TenantSchemaNamingStrategy namingStrategy = new TenantSchemaNamingStrategy(properties);
        HeaderTenantResolver resolver = new HeaderTenantResolver(properties, namingStrategy);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Tenant-Slug")).thenReturn("FinanCruz");

        var context = resolver.resolve(request);

        assertEquals("financruz", context.tenantSlug());
        assertEquals("tenant_financruz", context.schemaName());
    }

    @Test
    void shouldRejectUnknownTenantSlugBeforeTenantQueries() {
        TenancyProperties properties = new TenancyProperties();
        properties.setHeaderName("X-Tenant-Slug");
        properties.setTenantSchemaPrefix("tenant_");

        TenantSchemaNamingStrategy namingStrategy = new TenantSchemaNamingStrategy(properties);
        HeaderTenantResolver resolver = new HeaderTenantResolver(properties, namingStrategy);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Tenant-Slug")).thenReturn("FinanCru"); // missing a letter

        var context = resolver.resolve(request);

        assertEquals("financru", context.tenantSlug());
        assertEquals("tenant_financru", context.schemaName());
    }
}
