package com.financesystem.finance_api.common.tenancy.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * Keeps the reporting security layer in sync as tenants are provisioned,
 * backfilled or restored.
 *
 * <p>Two responsibilities:
 * <ul>
 *   <li>Grant the read-only role {@code finance_tenant_readonly} SELECT on a
 *       tenant's {@code reporting_*} views (USAGE on the schema, SELECT on the
 *       views only — never on raw tables).</li>
 *   <li>Re-run {@code reporting.regenerate_views()} so the cross-tenant
 *       platform views (read by {@code finance_platform_readonly}) reflect the
 *       current set of tenants.</li>
 * </ul>
 *
 * <p>All statements run on the privileged {@code targetDataSource} (the app
 * user owns the schemas and can GRANT); never on a read-only datasource.
 */
@Service
public class ReportingSecurityService {

    private static final Logger logger = LoggerFactory.getLogger(ReportingSecurityService.class);

    private static final String TENANT_READONLY_ROLE = "finance_tenant_readonly";

    private final JdbcTemplate jdbcTemplate;

    public ReportingSecurityService(@Qualifier("targetDataSource") DataSource targetDataSource) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
    }

    /**
     * Apply per-view grants for one tenant schema and refresh the platform views.
     * Safe to call repeatedly (grants are idempotent).
     */
    public void applyTenantSecurity(String schemaName) {
        applyTenantGrants(schemaName);
        regeneratePlatformViews();
    }

    /**
     * Grant USAGE on the schema and SELECT on each existing {@code reporting_*}
     * view to the tenant read-only role. Only views that actually exist are
     * granted, so a tenant with an incomplete migration does not abort the run.
     */
    public void applyTenantGrants(String schemaName) {
        String schema = requireSafeSchema(schemaName);

        List<String> views = jdbcTemplate.queryForList(
                """
                SELECT table_name
                FROM information_schema.views
                WHERE table_schema = ?
                  AND table_name LIKE 'reporting\\_%'
                """,
                String.class,
                schema
        );

        if (views.isEmpty()) {
            logger.warn("No reporting_* views found in schema '{}'; skipping grants.", schema);
            return;
        }

        jdbcTemplate.execute("GRANT USAGE ON SCHEMA \"" + schema + "\" TO " + TENANT_READONLY_ROLE);
        for (String view : views) {
            // view names come from information_schema and match reporting_[a-z_]+; quoted for safety.
            jdbcTemplate.execute(
                    "GRANT SELECT ON \"" + schema + "\".\"" + view + "\" TO " + TENANT_READONLY_ROLE);
        }
        logger.info("Applied reporting read-only grants on schema '{}' ({} views).", schema, views.size());
    }

    /** Rebuild the cross-tenant {@code reporting.*} views (UNION ALL + tenant_slug). */
    public void regeneratePlatformViews() {
        jdbcTemplate.execute("SELECT reporting.regenerate_views()");
        logger.info("Regenerated cross-tenant reporting views.");
    }

    /**
     * Re-apply grants for every active tenant and regenerate the platform views.
     * Used at startup (after tenant migrations) and after a full-database restore.
     */
    public void backfillRegisteredTenants() {
        List<String> schemas = jdbcTemplate.queryForList(
                "SELECT schema_name FROM public.platform_tenants WHERE active = true ORDER BY created_at ASC",
                String.class
        );

        for (String schema : schemas) {
            try {
                applyTenantGrants(schema);
            } catch (Exception e) {
                logger.warn("Failed to apply reporting grants for schema '{}': {}", schema, e.getMessage());
            }
        }

        regeneratePlatformViews();
        logger.info("Reporting security backfill completed for {} tenant schema(s).", schemas.size());
    }

    private String requireSafeSchema(String schemaName) {
        if (schemaName == null || !schemaName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid schema name for reporting grants: " + schemaName);
        }
        return schemaName;
    }
}
