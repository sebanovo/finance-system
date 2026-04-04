package com.financesystem.finance.bootstrap.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class TenantBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(TenantBootstrapService.class);

    private final JdbcTemplate jdbcTemplate;

    public TenantBootstrapService(@Qualifier("targetDataSource") DataSource targetDataSource) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
    }

    public void initializeTenantData(String schemaName, String tenantName) {
        validateSchemaName(schemaName);

        logger.info("Initializing tenant bootstrap data for schema '{}'.", schemaName);

        seedDefaultRoles(schemaName);
        seedDefaultSettings(schemaName, tenantName);

        logger.info("Tenant bootstrap data initialized successfully for schema '{}'.", schemaName);
    }

    private void seedDefaultRoles(String schemaName) {
        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('ADMIN', 'Default tenant administrator role', true, NOW())
                ON CONFLICT (name) DO NOTHING
                """.formatted(schemaName));

        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('USER', 'Default tenant user role', true, NOW())
                ON CONFLICT (name) DO NOTHING
                """.formatted(schemaName));
    }

    private void seedDefaultSettings(String schemaName, String tenantName) {
        jdbcTemplate.update("""
                INSERT INTO %s.tenant_settings (setting_key, setting_value, created_at, updated_at)
                VALUES ('company.name', ?, NOW(), NOW())
                ON CONFLICT (setting_key) DO UPDATE SET
                    setting_value = EXCLUDED.setting_value,
                    updated_at = NOW()
                """.formatted(schemaName), tenantName);

        jdbcTemplate.update("""
                INSERT INTO %s.tenant_settings (setting_key, setting_value, created_at, updated_at)
                VALUES ('company.timezone', 'America/La_Paz', NOW(), NOW())
                ON CONFLICT (setting_key) DO NOTHING
                """.formatted(schemaName));

        jdbcTemplate.update("""
                INSERT INTO %s.tenant_settings (setting_key, setting_value, created_at, updated_at)
                VALUES ('company.currency', 'BOB', NOW(), NOW())
                ON CONFLICT (setting_key) DO NOTHING
                """.formatted(schemaName));
    }

    private void validateSchemaName(String schemaName) {
        if (schemaName == null || schemaName.isBlank()) {
            throw new IllegalArgumentException("Schema name must not be blank");
        }

        if (!schemaName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Schema name contains invalid characters: " + schemaName);
        }
    }
}