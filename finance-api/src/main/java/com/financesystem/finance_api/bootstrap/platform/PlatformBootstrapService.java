package com.financesystem.finance_api.bootstrap.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class PlatformBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformBootstrapService.class);

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final SuperadminBootstrapProperties superadminBootstrapProperties;

    public PlatformBootstrapService(
            @Qualifier("targetDataSource") DataSource targetDataSource,
            PasswordEncoder passwordEncoder,
            SuperadminBootstrapProperties superadminBootstrapProperties
    ) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
        this.passwordEncoder = passwordEncoder;
        this.superadminBootstrapProperties = superadminBootstrapProperties;
    }

    public void seedBasePlans() {
        logger.info("Seeding base platform plans...");

        List<PlanSeed> plans = List.of(
                new PlanSeed("DEMO", "Demo", "Demo trial plan", 2, 2, "DEMO", 10),
                new PlanSeed("BASIC", "Basic", "Basic subscription plan", 10, 5, "PAID", null),
                new PlanSeed("PRO", "Professional", "Professional subscription plan", 25, 10, "PAID", null),
                new PlanSeed("ENTERPRISE", "Enterprise", "Enterprise subscription plan", 9999, 999, "PAID", null)
        );

        for (PlanSeed plan : plans) {
            jdbcTemplate.update(
                    """
                    INSERT INTO public.platform_plans (
                        code, name, description, max_users, max_roles, plan_type, trial_days, active, created_at, updated_at
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, true, NOW(), NOW())
                    ON CONFLICT (code) DO UPDATE SET
                        name = EXCLUDED.name,
                        description = EXCLUDED.description,
                        max_users = EXCLUDED.max_users,
                        max_roles = EXCLUDED.max_roles,
                        plan_type = EXCLUDED.plan_type,
                        trial_days = EXCLUDED.trial_days,
                        active = true,
                        updated_at = NOW()
                    """,
                    plan.code(),
                    plan.name(),
                    plan.description(),
                    plan.maxUsers(),
                    plan.maxRoles(),
                    plan.planType(),
                    plan.trialDays()
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

    public void seedInitialPlatformSuperadmin() {
        logger.info("Seeding initial platform superadmin...");

        String email = superadminBootstrapProperties.getEmail().trim().toLowerCase();
        String passwordHash = passwordEncoder.encode(superadminBootstrapProperties.getPassword());

        jdbcTemplate.update(
                """
                INSERT INTO public.platform_superadmins (
                    email, password_hash, first_name, last_name, active, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, true, NOW(), NOW())
                ON CONFLICT (email) DO UPDATE SET
                    password_hash = EXCLUDED.password_hash,
                    first_name = EXCLUDED.first_name,
                    last_name = EXCLUDED.last_name,
                    active = true,
                    updated_at = NOW()
                """,
                email,
                passwordHash,
                superadminBootstrapProperties.getFirstName(),
                superadminBootstrapProperties.getLastName()
        );

        logger.info("Initial platform superadmin seeded successfully for email '{}'.", email);
    }

    private record PlanSeed(
            String code,
            String name,
            String description,
            int maxUsers,
            int maxRoles,
            String planType,
            Integer trialDays
    ) {
    }

    private record PermissionSeed(
            String code,
            String module,
            String description
    ) {
    }
}
