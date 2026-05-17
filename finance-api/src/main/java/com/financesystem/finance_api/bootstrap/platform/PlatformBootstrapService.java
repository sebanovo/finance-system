package com.financesystem.finance_api.bootstrap.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PlatformBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformBootstrapService.class);
    private static final Pattern PERMISSION_CODE_PATTERN = Pattern.compile("^[a-z][a-z0-9-]*(?:[.][a-z0-9-]+)*$");
    private static final Pattern MODULE_PATTERN = Pattern.compile("^[a-z][a-z0-9-]*$");

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

        seedPermissions(permissions);

        logger.info("Base system permissions seeded successfully.");
    }

    public void seedBaseAccountPermissions() {
        logger.info("Seeding base accounts permissions...");

        List<PermissionSeed> permissions = List.of(
                // OWNER_ADMIN PERMISSIONS
                new PermissionSeed("accounts.create", "accounts", "Create tenant accounts"),
                new PermissionSeed("accounts.list", "accounts", "List tenant accounts"),
                new PermissionSeed("accounts.view", "accounts", "View tenant account details"),
                new PermissionSeed("accounts.balance.read", "accounts", "Read tenant account balances"),
                new PermissionSeed("accounts.update", "accounts", "Update tenant accounts"),
                new PermissionSeed("accounts.approve", "accounts", "Approve tenant accounts"),
                new PermissionSeed("accounts.activate", "accounts", "Activate tenant accounts"),
                new PermissionSeed("accounts.block", "accounts", "Block tenant accounts"),
                new PermissionSeed("accounts.freeze", "accounts", "Freeze tenant accounts"),
                new PermissionSeed("accounts.close", "accounts", "Close tenant accounts"),

                // Client permissions
                new PermissionSeed("accounts.me.create", "accounts", "Create own accounts"),
                new PermissionSeed("accounts.me.list", "accounts", "List own accounts"),
                new PermissionSeed("accounts.me.view", "accounts", "View own account details"),
                new PermissionSeed("accounts.me.balance.read", "accounts", "Read own account balances"),
                new PermissionSeed("accounts.me.update.alias", "accounts", "Update own account aliases")
        );

        seedPermissions(permissions);

        logger.info("Base accounts permissions seeded successfully.");
    }

    public void seedBaseTransactionPermissions() {
        logger.info("Seeding base transactions permissions...");

        List<PermissionSeed> permissions = List.of(
                new PermissionSeed("transactions.read", "transactions", "Read tenant transactions"),
                new PermissionSeed("transactions.detail", "transactions", "Read tenant transaction details"),
                new PermissionSeed("transactions.create.transfer", "transactions", "Create transfer transactions"),
                new PermissionSeed("transactions.create.deposit", "transactions", "Create deposit transactions"),
                new PermissionSeed("transactions.create.withdrawal", "transactions", "Create withdrawal transactions"),
                new PermissionSeed("transactions.create.payment", "transactions", "Create payment transactions"),
                new PermissionSeed("transactions.reverse", "transactions", "Reverse transactions"),
                new PermissionSeed("transactions.refund", "transactions", "Refund transactions"),
                new PermissionSeed("transactions.fee", "transactions", "Create fee transactions"),
                new PermissionSeed("transactions.hold", "transactions", "Hold account funds"),
                new PermissionSeed("transactions.release", "transactions", "Release held funds"),
                new PermissionSeed("transactions.adjust", "transactions", "Adjust transactions"),
                new PermissionSeed("transactions.admin.read", "transactions", "Read admin transaction views"),
                new PermissionSeed("transactions.admin.export", "transactions", "Export transaction data"),
                new PermissionSeed("transactions.qr.create", "transactions", "Create QR transaction intents"),
                new PermissionSeed("transactions.qr.confirm", "transactions", "Confirm QR transaction intents"),
                new PermissionSeed("me.transactions.read", "transactions", "Read own transactions"),
                new PermissionSeed("me.transactions.transfer", "transactions", "Create own transfer transactions"),
                new PermissionSeed("me.transactions.deposit", "transactions", "Create own deposit transactions"),
                new PermissionSeed("me.transactions.withdrawal", "transactions", "Create own withdrawal transactions"),
                new PermissionSeed("me.transactions.payment", "transactions", "Create own payment transactions"),
                new PermissionSeed("me.transactions.hold", "transactions", "Hold own account funds"),
                new PermissionSeed("me.transactions.release", "transactions", "Release own held funds"),
                new PermissionSeed("me.transactions.qr.confirm", "transactions", "Confirm own QR transaction intents")
        );

        seedPermissions(permissions);

        logger.info("Base transactions permissions seeded successfully.");
    }

    public void seedBaseLimitPermissions() {
        logger.info("Seeding base limits permissions...");

        List<PermissionSeed> permissions = List.of(
                new PermissionSeed("limits.read", "limits", "Read limit rules"),
                new PermissionSeed("limits.create", "limits", "Create limit rules"),
                new PermissionSeed("limits.update", "limits", "Update limit rules"),
                new PermissionSeed("limits.delete", "limits", "Delete limit rules"),
                new PermissionSeed("limits.evaluate", "limits", "Evaluate limit rules")
        );

        seedPermissions(permissions);

        logger.info("Base limits permissions seeded successfully.");
    }

    public void seedBaseAccountingPermissions() {
        logger.info("Seeding base accounting permissions...");

        List<PermissionSeed> permissions = List.of(
                new PermissionSeed("accounting.journal.read", "accounting", "Read journal entries"),
                new PermissionSeed("accounting.journal.detail", "accounting", "Read journal entry details"),
                new PermissionSeed("accounting.periods.read", "accounting", "Read accounting periods"),
                new PermissionSeed("accounting.periods.create", "accounting", "Create accounting periods"),
                new PermissionSeed("accounting.periods.close", "accounting", "Close accounting periods")
        );

        seedPermissions(permissions);

        logger.info("Base accounting permissions seeded successfully.");
    }

    public void seedBaseFxPermissions() {
        logger.info("Seeding base fx permissions...");

        List<PermissionSeed> permissions = List.of(
                new PermissionSeed("fx.rates.read", "fx", "Read exchange rates"),
                new PermissionSeed("fx.rates.create", "fx", "Create exchange rates"),
                new PermissionSeed("fx.rates.update", "fx", "Update exchange rates"),
                new PermissionSeed("fx.rates.delete", "fx", "Delete exchange rates"),
                new PermissionSeed("fx.fees.read", "fx", "Read operation fees"),
                new PermissionSeed("fx.fees.create", "fx", "Create operation fees"),
                new PermissionSeed("fx.fees.update", "fx", "Update operation fees"),
                new PermissionSeed("fx.fees.delete", "fx", "Delete operation fees")
        );

        seedPermissions(permissions);

        logger.info("Base fx permissions seeded successfully.");
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

    private void seedPermissions(List<PermissionSeed> permissions) {
        validatePermissionSeeds(permissions);

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
    }

    private void validatePermissionSeeds(List<PermissionSeed> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            throw new IllegalArgumentException("Permission seed list must not be empty");
        }

        Set<String> uniqueCodes = permissions.stream()
                .map(PermissionSeed::code)
                .map(this::normalizeRequiredText)
                .collect(Collectors.toSet());

        if (uniqueCodes.size() != permissions.size()) {
            throw new IllegalArgumentException("Permission seed codes must be unique");
        }

        for (PermissionSeed permission : permissions) {
            String code = normalizeRequiredText(permission.code());
            String module = normalizeRequiredText(permission.module());
            String description = normalizeRequiredText(permission.description());

            if (!PERMISSION_CODE_PATTERN.matcher(code).matches()) {
                throw new IllegalArgumentException("Invalid permission code seed: " + code);
            }

            if (!MODULE_PATTERN.matcher(module).matches()) {
                throw new IllegalArgumentException("Invalid permission module seed: " + module);
            }

            if (description.isBlank()) {
                throw new IllegalArgumentException("Permission seed description must not be blank");
            }
        }
    }

    private String normalizeRequiredText(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Permission seed values must not be blank");
        }

        return value.trim();
    }
}
