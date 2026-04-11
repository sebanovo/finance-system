package com.financesystem.finance_api.modules.platform.onboarding.application.service;

import com.financesystem.finance_api.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.UUID;

@Service
public class TenantOwnerAdminProvisioningService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public TenantOwnerAdminProvisioningService(
            @Qualifier("targetDataSource") DataSource targetDataSource,
            PasswordEncoder passwordEncoder
    ) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
        this.passwordEncoder = passwordEncoder;
    }

    public UUID provisionOwnerAdmin(
            String schemaName,
            String email,
            String rawPassword,
            String firstName,
            String lastName
    ) {
        validateSchemaName(schemaName);

        String normalizedEmail = email.trim().toLowerCase();
        String normalizedFirstName = firstName.trim();
        String normalizedLastName = lastName.trim();

        Integer existingUsers = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM %s.tenant_users
                WHERE email = ?
                """.formatted(schemaName),
                Integer.class,
                normalizedEmail
        );

        if (existingUsers != null && existingUsers > 0) {
            throw new BusinessException("A tenant user with email '" + normalizedEmail + "' already exists");
        }

        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('OWNER_ADMIN', 'Tenant owner administrator role', true, NOW())
                ON CONFLICT (name) DO UPDATE SET
                    active = true,
                    description = EXCLUDED.description
                """.formatted(schemaName));

        UUID userId = UUID.randomUUID();
        String passwordHash = passwordEncoder.encode(rawPassword);

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_users (
                    id, email, password_hash, first_name, last_name, active, status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, true, 'ACTIVE', NOW(), NOW())
                """.formatted(schemaName),
                userId,
                normalizedEmail,
                passwordHash,
                normalizedFirstName,
                normalizedLastName
        );

        UUID ownerAdminRoleId = jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM %s.tenant_roles
                WHERE name = 'OWNER_ADMIN'
                LIMIT 1
                """.formatted(schemaName),
                (rs, rowNum) -> rs.getObject("id", UUID.class)
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_user_roles (user_id, role_id, assigned_at)
                VALUES (?, ?, NOW())
                ON CONFLICT (user_id, role_id) DO NOTHING
                """.formatted(schemaName),
                userId,
                ownerAdminRoleId
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_settings (setting_key, setting_value, created_at, updated_at)
                VALUES ('company.contact_email', ?, NOW(), NOW())
                ON CONFLICT (setting_key) DO UPDATE SET
                    setting_value = EXCLUDED.setting_value,
                    updated_at = NOW()
                """.formatted(schemaName),
                normalizedEmail
        );

        return userId;
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
