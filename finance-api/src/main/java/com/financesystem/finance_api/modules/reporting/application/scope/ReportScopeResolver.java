package com.financesystem.finance_api.modules.reporting.application.scope;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.common.security.principal.AuthenticatedUserPrincipal;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.common.tenancy.schema.TenantSchemaNamingStrategy;
import com.financesystem.finance_api.modules.reporting.application.config.ReportingProperties;
import com.financesystem.finance_api.modules.reporting.domain.ReportActorScope;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Resolves the {@link ReportActor} for tenant vs platform report requests. */
@Component
public class ReportScopeResolver {

    private static final String PLATFORM_SLUG = "platform";

    private final SecurityContextFacade securityContextFacade;
    private final TenantSchemaNamingStrategy namingStrategy;
    private final ReportingProperties properties;

    public ReportScopeResolver(SecurityContextFacade securityContextFacade,
                               TenantSchemaNamingStrategy namingStrategy,
                               ReportingProperties properties) {
        this.securityContextFacade = securityContextFacade;
        this.namingStrategy = namingStrategy;
        this.properties = properties;
    }

    public ReportActor resolveTenant() {
        AuthenticatedUserPrincipal principal = requirePrincipal();
        String slug = principal.tenantSlug();
        if (slug == null || PLATFORM_SLUG.equalsIgnoreCase(slug)) {
            throw new IllegalStateException("Tenant scope requiere un usuario de tenant.");
        }
        String schema = resolveTenantSchema(slug);
        return new ReportActor(
                parseUuid(principal.subject()),
                principal.email(),
                principal.displayName(),
                ReportScope.TENANT,
                ReportActorScope.TENANT,
                slug,
                schema
        );
    }

    public ReportActor resolvePlatform() {
        AuthenticatedUserPrincipal principal = requirePrincipal();
        return new ReportActor(
                parseUuid(principal.subject()),
                principal.email(),
                principal.displayName(),
                ReportScope.GLOBAL,
                ReportActorScope.PLATFORM,
                null,
                properties.getSchema()
        );
    }

    private String resolveTenantSchema(String slug) {
        TenantContext context = TenantContextHolder.get();
        if (context != null && context.schemaName() != null && !context.schemaName().isBlank()) {
            return context.schemaName();
        }
        return namingStrategy.toSchemaName(slug);
    }

    private AuthenticatedUserPrincipal requirePrincipal() {
        AuthenticatedUserPrincipal principal = securityContextFacade.getCurrentPrincipal();
        if (principal == null) {
            throw new IllegalStateException("No hay un usuario autenticado.");
        }
        return principal;
    }

    private UUID parseUuid(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
