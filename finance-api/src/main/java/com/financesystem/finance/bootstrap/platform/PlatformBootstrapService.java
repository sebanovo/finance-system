package com.financesystem.finance.bootstrap.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class PlatformBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformBootstrapService.class);

    private final JdbcTemplate jdbcTemplate;

    public PlatformBootstrapService(@Qualifier("targetDataSource") DataSource targetDataSource) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
    }

    public void seedBasePlans() {
        logger.info("Seeding base platform plans...");

        List<PlanSeed> plans = List.of(
                new PlanSeed("BASIC", "Basic", "Basic subscription plan", 10, 5),
                new PlanSeed("PRO", "Professional", "Professional subscription plan", 25, 10),
                new PlanSeed("ENTERPRISE", "Enterprise", "Enterprise subscription plan", 9999, 999)
        );

        for (PlanSeed plan : plans) {
            jdbcTemplate.update(
                    """
                    INSERT INTO public.platform_plans (
                        code, name, description, max_users, max_roles, active, created_at, updated_at
                    )
                    VALUES (?, ?, ?, ?, ?, true, NOW(), NOW())
                    ON CONFLICT (code) DO UPDATE SET
                        name = EXCLUDED.name,
                        description = EXCLUDED.description,
                        max_users = EXCLUDED.max_users,
                        max_roles = EXCLUDED.max_roles,
                        active = true,
                        updated_at = NOW()
                    """,
                    plan.code(),
                    plan.name(),
                    plan.description(),
                    plan.maxUsers(),
                    plan.maxRoles()
            );
        }

        logger.info("Base platform plans seeded successfully.");
    }

    public void seedBaseSystemPermissions() {
        logger.info("Seeding base system permissions...");

        List<PermissionSeed> permissions = List.of(
                new PermissionSeed("users.read", "users", "Read tenant users"),
                new PermissionSeed("users.create", "users", "Create tenant users"),
                new PermissionSeed("users.update", "users", "Update tenant users"),
                new PermissionSeed("users.activate", "users", "Activate tenant users"),
                new PermissionSeed("users.deactivate", "users", "Deactivate tenant users"),

                new PermissionSeed("roles.read", "access", "Read tenant roles"),
                new PermissionSeed("roles.create", "access", "Create tenant roles"),
                new PermissionSeed("roles.update", "access", "Update tenant roles"),
                new PermissionSeed("roles.assign", "access", "Assign roles and permissions"),

                new PermissionSeed("tenant-settings.read", "tenantsettings", "Read tenant settings"),
                new PermissionSeed("tenant-settings.update", "tenantsettings", "Update tenant settings"),

                new PermissionSeed("audit.read", "audit", "Read tenant audit events")
        );

        for (PermissionSeed permission : permissions) {
            jdbcTemplate.update(
                    """
                    INSERT INTO public.system_permissions (
                        code, module, description, active, created_at
                    )
                    VALUES (?, ?, ?, true, NOW())
                    ON CONFLICT (code) DO UPDATE SET
                        module = EXCLUDED.module,
                        description = EXCLUDED.description,
                        active = true
                    """,
                    permission.code(),
                    permission.module(),
                    permission.description()
            );
        }

        logger.info("Base system permissions seeded successfully.");
    }

    private record PlanSeed(
            String code,
            String name,
            String description,
            int maxUsers,
            int maxRoles
    ) {
    }

    private record PermissionSeed(
            String code,
            String module,
            String description
    ) {
    }
}