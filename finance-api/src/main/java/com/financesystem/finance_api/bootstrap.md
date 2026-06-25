# Directory Export: /home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/bootstrap

_Generated on 2026-06-25 01:03:47Z_

## Summary

- Source directory: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/bootstrap`
- Output file: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/bootstrap.md`

## Files

### `platform/DevTokenController.java`

```java
package com.financesystem.finance_api.bootstrap.platform;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/dev-token")
@Profile("dev")
@Hidden
public class DevTokenController {

    private final JwtTokenService jwtTokenService;

    public DevTokenController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    public ApiResponse<DevTokenResponse> generateToken(@Valid @RequestBody DevTokenRequest request) {
        List<String> roles = request.roles() == null || request.roles().isEmpty()
                ? List.of("USER")
                : request.roles();

        String accessToken = jwtTokenService.generateAccessToken(
                request.email(),
                request.email(),
                null,
                request.tenantSlug(),
                roles,
                List.of()
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                request.email(),
                request.tenantSlug()
        );

        return ApiResponse.success(
                "Development token generated successfully",
                new DevTokenResponse("Bearer", accessToken, refreshToken)
        );
    }
}

```

### `platform/DevTokenRequest.java`

```java
package com.financesystem.finance_api.bootstrap.platform;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DevTokenRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String tenantSlug,

        List<String> roles
) {
}
```

### `platform/DevTokenResponse.java`

```java
package com.financesystem.finance_api.bootstrap.platform;

public record DevTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken
) {
}
```

### `platform/PlatformBootstrapRunner.java`

```java
package com.financesystem.finance_api.bootstrap.platform;

import com.financesystem.finance_api.common.tenancy.migration.TenantSchemaMigrationService;
import com.financesystem.finance_api.common.tenancy.reporting.ReportingSecurityService;
import com.financesystem.finance_api.bootstrap.sample.SampleDataBootstrapProperties;
import com.financesystem.finance_api.bootstrap.sample.SampleDataBootstrapService;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionLifecycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class PlatformBootstrapRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(PlatformBootstrapRunner.class);

    private final PlatformBootstrapService platformBootstrapService;
    private final TenantSchemaMigrationService tenantSchemaMigrationService;
    private final PlatformSubscriptionLifecycleService platformSubscriptionLifecycleService;
    private final ReportingSecurityService reportingSecurityService;
    private final SampleDataBootstrapProperties sampleDataBootstrapProperties;
    private final SampleDataBootstrapService sampleDataBootstrapService;

    public PlatformBootstrapRunner(
            PlatformBootstrapService platformBootstrapService,
            TenantSchemaMigrationService tenantSchemaMigrationService,
            PlatformSubscriptionLifecycleService platformSubscriptionLifecycleService,
            ReportingSecurityService reportingSecurityService,
            SampleDataBootstrapProperties sampleDataBootstrapProperties,
            SampleDataBootstrapService sampleDataBootstrapService
    ) {
        this.platformBootstrapService = platformBootstrapService;
        this.tenantSchemaMigrationService = tenantSchemaMigrationService;
        this.platformSubscriptionLifecycleService = platformSubscriptionLifecycleService;
        this.reportingSecurityService = reportingSecurityService;
        this.sampleDataBootstrapProperties = sampleDataBootstrapProperties;
        this.sampleDataBootstrapService = sampleDataBootstrapService;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Starting platform bootstrap runner...");

        platformBootstrapService.seedBasePlans();
        platformBootstrapService.seedBaseSystemPermissions();
        platformBootstrapService.seedBaseAccountPermissions();
        platformBootstrapService.seedBaseTransactionPermissions();
        platformBootstrapService.seedBaseLimitPermissions();
        platformBootstrapService.seedBaseAccountingPermissions();
        platformBootstrapService.seedBaseFxPermissions();
        platformBootstrapService.seedBaseBackupPermissions();
        platformBootstrapService.seedBaseServicePermissions();
        platformBootstrapService.seedBaseReportingPermissions();
        platformBootstrapService.seedInitialPlatformSuperadmin();

        if (sampleDataBootstrapProperties.isEnabled()) {
            sampleDataBootstrapService.seedDemoTenants();
        } else {
            logger.info("Optional sample data bootstrap is disabled.");
        }

        tenantSchemaMigrationService.migrateRegisteredTenantSchemas();
        platformBootstrapService.seedBackupPermissionsForRegisteredTenants();
        platformBootstrapService.seedServicePermissionsForRegisteredTenants();
        platformBootstrapService.seedReportingPermissionsForRegisteredTenants();

        // Reporting security backfill: per-view grants on every existing tenant
        // + regenerate cross-tenant views. Runs after tenant migrations so the
        // reporting_* views (tenant V16) already exist.
        reportingSecurityService.backfillRegisteredTenants();

        platformSubscriptionLifecycleService.refreshExpiredSubscriptions();

        logger.info("Platform bootstrap runner completed successfully.");
    }
}

```

### `platform/PlatformBootstrapService.java`

```java
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
                new PlanSeed("DEMO", "Demo", "Demo trial plan", 5, 3, "DEMO", 10),
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
                // IDENTITY / ACCESS PERMISSIONS
                new PermissionSeed("access.permissions.read", "access", "Read system permissions"),
                new PermissionSeed("access.roles.read", "access", "Read tenant roles"),
                new PermissionSeed("access.roles.create", "access", "Create tenant roles"),
                new PermissionSeed("access.roles.detail", "access", "Read tenant role details"),
                new PermissionSeed("access.roles.update", "access", "Update tenant roles"),
                new PermissionSeed("access.roles.activate", "access", "Activate tenant roles"),
                new PermissionSeed("access.roles.deactivate", "access", "Deactivate tenant roles"),
                new PermissionSeed("access.users.roles.read", "access", "Read user role assignments"),
                new PermissionSeed("access.users.roles.assign", "access", "Assign roles to users"),
                new PermissionSeed("users.list", "users", "List tenant users"),
                new PermissionSeed("users.create", "users", "Create tenant users"),
                new PermissionSeed("users.detail", "users", "Read tenant user details"),
                new PermissionSeed("users.update", "users", "Update tenant users"),
                new PermissionSeed("users.activate", "users", "Activate tenant users"),
                new PermissionSeed("users.deactivate", "users", "Deactivate tenant users"),
                // GOVERNANCE PERMISSIONS
                new PermissionSeed("audit.events.read", "audit", "Read tenant audit events"),
                new PermissionSeed("notifications.templates.read", "notifications", "Read notification templates"),
                new PermissionSeed("notifications.templates.detail", "notifications", "Read notification template details"),
                new PermissionSeed("notifications.deliveries.read", "notifications", "Read notification deliveries"),


                // REPORTS PERMISSIONS
                new PermissionSeed("reports.analytic.read", "reports", "Read analytic reports"),
                new PermissionSeed("reports.managerial.read", "reports", "Read managerial reports"),
                new PermissionSeed("reports.analytic.run", "reports", "Run analytic reports"),
                new PermissionSeed("reports.managerial.run", "reports", "Run managerial reports"),
                new PermissionSeed("reports.export", "reports", "Export reports"),
                new PermissionSeed("reports.executions.read", "reports", "Read report executions"),
                new PermissionSeed("reports.executions.rerun", "reports", "Rerun report executions"),


                // TENANT SETTINGS PERMISSIONS
                new PermissionSeed("tenant-settings.read", "tenantsettings", "Read tenant settings"),
                new PermissionSeed("tenant-settings.update", "tenantsettings", "Update tenant settings")
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
                new PermissionSeed("accounts.transactions.read", "accounts", "Read transactions for a tenant account"),
                // CLIENT PERMISSIONS
                new PermissionSeed("me.accounts.create", "accounts", "Create own accounts"),
                new PermissionSeed("me.accounts.list", "accounts", "List own accounts"),
                new PermissionSeed("me.accounts.view", "accounts", "View own account details"),
                new PermissionSeed("me.accounts.balance.read", "accounts", "Read own account balances"),
                new PermissionSeed("me.accounts.update.alias", "accounts", "Update own account aliases"),
                new PermissionSeed("me.accounts.transactions.read", "accounts", "Read own account transactions")
        );

        seedPermissions(permissions);

        logger.info("Base accounts permissions seeded successfully.");
    }

    public void seedBaseTransactionPermissions() {
        logger.info("Seeding base transactions permissions...");

        List<PermissionSeed> permissions = List.of(
                // OWNER_ADMIN PERMISSIONS
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
                // CLIENT PERMISSIONS
                new PermissionSeed("me.transactions.read", "transactions", "Read own transactions"),
                new PermissionSeed("me.transactions.detail", "transactions", "Read own transaction details"),
                new PermissionSeed("me.transactions.transfer", "transactions", "Create own transfer transactions"),
                new PermissionSeed("me.transactions.deposit", "transactions", "Create own deposit transactions"),
                new PermissionSeed("me.transactions.withdrawal", "transactions", "Create own withdrawal transactions"),
                new PermissionSeed("me.transactions.payment", "transactions", "Create own payment transactions"),
                new PermissionSeed("me.transactions.hold", "transactions", "Hold own account funds"),
                new PermissionSeed("me.transactions.release", "transactions", "Release own held funds"),
                new PermissionSeed("me.transactions.qr.create", "transactions", "Create own QR transaction intents"),
                new PermissionSeed("me.transactions.qr.read", "transactions", "Read own QR transaction intents"),
                new PermissionSeed("me.transactions.qr.cancel", "transactions", "Cancel own QR transaction intents"),
                new PermissionSeed("me.transactions.qr.confirm", "transactions", "Confirm own QR transaction intents")
        );

        seedPermissions(permissions);

        logger.info("Base transactions permissions seeded successfully.");
    }

    public void seedBaseLimitPermissions() {
        logger.info("Seeding base limits permissions...");

        List<PermissionSeed> permissions = List.of(
                // OWNER_ADMIN PERMISSIONS
                new PermissionSeed("limits.read", "limits", "Read limit rules"),
                new PermissionSeed("limits.detail", "limits", "Read limit rule details"),
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
                // OWNER_ADMIN PERMISSIONS
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
                // OWNER_ADMIN PERMISSIONS
                new PermissionSeed("fx.rates.read", "fx", "Read exchange rates"),
                new PermissionSeed("fx.rates.detail", "fx", "Read exchange rate details"),
                new PermissionSeed("fx.rates.create", "fx", "Create exchange rates"),
                new PermissionSeed("fx.rates.update", "fx", "Update exchange rates"),
                new PermissionSeed("fx.rates.delete", "fx", "Delete exchange rates"),
                new PermissionSeed("fx.fees.read", "fx", "Read operation fees"),
                new PermissionSeed("fx.fees.detail", "fx", "Read operation fee details"),
                new PermissionSeed("fx.fees.create", "fx", "Create operation fees"),
                new PermissionSeed("fx.fees.update", "fx", "Update operation fees"),
                new PermissionSeed("fx.fees.delete", "fx", "Delete operation fees")
        );

        seedPermissions(permissions);

        logger.info("Base fx permissions seeded successfully.");
    }

    public void seedBaseBackupPermissions() {
        logger.info("Seeding base backup permissions...");

        List<PermissionSeed> permissions = List.of(
                new PermissionSeed("backups.create", "backups", "Create tenant backups"),
                new PermissionSeed("backups.list", "backups", "List tenant backups"),
                new PermissionSeed("backups.detail", "backups", "Read tenant backup details"),
                new PermissionSeed("backups.download", "backups", "Download tenant backups"),
                new PermissionSeed("backups.restore", "backups", "Restore tenant backups")
        );

        seedPermissions(permissions);

        logger.info("Base backup permissions seeded successfully.");
    }

    public void seedBaseServicePermissions() {
        logger.info("Seeding base service permissions...");

        List<PermissionSeed> permissions = List.of(
                new PermissionSeed("me.service-providers.read", "services", "Read own service providers"),
                new PermissionSeed("me.service-enrollments.read", "services", "Read own service enrollments"),
                new PermissionSeed("me.service-enrollments.create", "services", "Create own service enrollments"),
                new PermissionSeed("me.service-enrollments.update", "services", "Update own service enrollments"),
                new PermissionSeed("me.service-enrollments.delete", "services", "Delete own service enrollments"),
                new PermissionSeed("me.service-bills.query", "services", "Query own service bills"),
                new PermissionSeed("me.service-payments.create", "services", "Create own service payments"),
                new PermissionSeed("me.service-payments.read", "services", "Read own service payments"),
                new PermissionSeed("me.service-payments.detail", "services", "Read own service payment details"),
                new PermissionSeed("service-providers.read", "services", "Read service providers"),
                new PermissionSeed("service-bills.query", "services", "Query service bills"),
                new PermissionSeed("service-payments.create", "services", "Create service payments"),
                new PermissionSeed("service-payments.read", "services", "Read service payments"),
                new PermissionSeed("service-payments.detail", "services", "Read service payment details"),
                new PermissionSeed("service-enrollments.read", "services", "Read service enrollments")
        );

        seedPermissions(permissions);

        logger.info("Base service permissions seeded successfully.");
    }

    public void seedBaseReportingPermissions() {
        logger.info("Seeding base reporting permissions...");

        List<PermissionSeed> permissions = List.of(
                new PermissionSeed("reports.tenant.read", "reports", "Read tenant report catalog"),
                new PermissionSeed("reports.tenant.run", "reports", "Run tenant reports"),
                new PermissionSeed("reports.tenant.export", "reports", "Export tenant reports"),
                new PermissionSeed("reports.tenant.ai", "reports", "Run AI tenant reports"),
                new PermissionSeed("reports.tenant.history", "reports", "Read tenant report history"),
                new PermissionSeed("reports.platform.read", "reports", "Read platform report catalog"),
                new PermissionSeed("reports.platform.run", "reports", "Run platform reports"),
                new PermissionSeed("reports.platform.export", "reports", "Export platform reports"),
                new PermissionSeed("reports.platform.ai", "reports", "Run AI platform reports"),
                new PermissionSeed("reports.platform.history", "reports", "Read platform report history")
        );

        seedPermissions(permissions);

        logger.info("Base reporting permissions seeded successfully.");
    }

    public void seedReportingPermissionsForRegisteredTenants() {
        logger.info("Assigning reporting permissions to registered tenant roles...");

        List<TenantSchemaSeed> tenants = jdbcTemplate.query(
                """
                SELECT slug, schema_name
                FROM public.platform_tenants
                WHERE active = true
                  AND schema_name IS NOT NULL
                  AND schema_name <> ''
                ORDER BY slug ASC
                """,
                (rs, rowNum) -> new TenantSchemaSeed(rs.getString("slug"), rs.getString("schema_name"))
        );

        List<String> reportingCodes = List.of(
                "reports.tenant.read",
                "reports.tenant.run",
                "reports.tenant.export",
                "reports.tenant.ai",
                "reports.tenant.history"
        );

        for (TenantSchemaSeed tenant : tenants) {
            validateSchemaName(tenant.schemaName());
            seedTenantRolePermissions(tenant.schemaName(), "OWNER_ADMIN", reportingCodes);
            seedTenantRolePermissions(tenant.schemaName(), "ADMIN", reportingCodes);
        }

        logger.info("Reporting permissions assigned to registered tenants successfully.");
    }

    public void seedServicePermissionsForRegisteredTenants() {
        logger.info("Assigning service permissions to registered tenant roles...");

        List<TenantSchemaSeed> tenants = jdbcTemplate.query(
                """
                SELECT slug, schema_name
                FROM public.platform_tenants
                WHERE active = true
                  AND schema_name IS NOT NULL
                  AND schema_name <> ''
                ORDER BY slug ASC
                """,
                (rs, rowNum) -> new TenantSchemaSeed(rs.getString("slug"), rs.getString("schema_name"))
        );

        List<String> tenantCodes = List.of(
                "service-providers.read",
                "service-bills.query",
                "service-payments.create",
                "service-payments.read",
                "service-payments.detail",
                "service-enrollments.read"
        );

        List<String> clientCodes = List.of(
                "me.service-providers.read",
                "me.service-enrollments.read",
                "me.service-enrollments.create",
                "me.service-enrollments.update",
                "me.service-enrollments.delete",
                "me.service-bills.query",
                "me.service-payments.create",
                "me.service-payments.read",
                "me.service-payments.detail"
        );

        for (TenantSchemaSeed tenant : tenants) {
            validateSchemaName(tenant.schemaName());

            seedTenantRolePermissions(tenant.schemaName(), "OWNER_ADMIN", tenantCodes);
            seedTenantRolePermissions(tenant.schemaName(), "OWNER_ADMIN", clientCodes);
            seedTenantRolePermissions(tenant.schemaName(), "ADMIN", tenantCodes);
            seedTenantRolePermissions(tenant.schemaName(), "ADMIN", clientCodes);
            seedTenantRolePermissions(tenant.schemaName(), "USER", clientCodes);
        }

        logger.info("Service permissions assigned to registered tenants successfully.");
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

    public void seedBackupPermissionsForRegisteredTenants() {
        logger.info("Assigning backup permissions to registered tenant roles...");

        List<TenantSchemaSeed> tenants = jdbcTemplate.query(
                """
            SELECT slug, schema_name
            FROM public.platform_tenants
            WHERE active = true
              AND schema_name IS NOT NULL
              AND schema_name <> ''
            ORDER BY slug ASC
            """,
                (rs, rowNum) -> new TenantSchemaSeed(
                        rs.getString("slug"),
                        rs.getString("schema_name")
                )
        );

        for (TenantSchemaSeed tenant : tenants) {
            validateSchemaName(tenant.schemaName());

            seedTenantRolePermissions(
                    tenant.schemaName(),
                    "OWNER_ADMIN",
                    List.of(
                            "backups.create",
                            "backups.list",
                            "backups.detail",
                            "backups.download",
                            "backups.restore"
                    )
            );

            seedTenantRolePermissions(
                    tenant.schemaName(),
                    "ADMIN",
                    List.of(
                            "backups.create",
                            "backups.list",
                            "backups.detail",
                            "backups.download"
                    )
            );
        }

        logger.info("Backup permissions assigned to registered tenants successfully.");
    }

    private void seedTenantRolePermissions(String schemaName, String roleName, List<String> permissionCodes) {
        for (String permissionCode : permissionCodes) {
            jdbcTemplate.update(
                    """
                INSERT INTO %s.tenant_role_permissions (role_id, permission_code, assigned_at)
                SELECT r.id, ?, NOW()
                FROM %s.tenant_roles r
                JOIN public.system_permissions sp
                    ON sp.code = ? AND sp.active = true
                WHERE r.name = ?
                ON CONFLICT (role_id, permission_code) DO NOTHING
                """.formatted(schemaName, schemaName),
                    permissionCode,
                    permissionCode,
                    roleName
            );
        }
    }

    private void validateSchemaName(String schemaName) {
        if (schemaName == null || schemaName.isBlank()) {
            throw new IllegalArgumentException("Schema name must not be blank");
        }

        if (!schemaName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid schema name: " + schemaName);
        }
    }

    private record TenantSchemaSeed(
            String slug,
            String schemaName
            ) {

    }

}

```

### `platform/PublicTestController.java`

```java
package com.financesystem.finance_api.bootstrap.platform;

import com.financesystem.finance_api.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicTestController {

    @GetMapping("/ping")
    public ApiResponse<Map<String, String>> ping() {
        return ApiResponse.success(
                "Finance API is running correctly",
                Map.of("status", "ok")
        );
    }

    @GetMapping("/security-status")
    public ApiResponse<Map<String, String>> securityStatus() {
        return ApiResponse.success(
                "Security base is active",
                Map.of("jwt", "enabled")
        );
    }
}

```

### `platform/SecureTestController.java`

```java
package com.financesystem.finance_api.bootstrap.platform;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.common.security.principal.AuthenticatedUserPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/secure")
public class SecureTestController {

    private final SecurityContextFacade securityContextFacade;

    public SecureTestController(SecurityContextFacade securityContextFacade) {
        this.securityContextFacade = securityContextFacade;
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me() {
        AuthenticatedUserPrincipal principal = securityContextFacade.getCurrentPrincipal();

        return ApiResponse.success(
                "Authenticated principal resolved successfully",
                Map.of(
                        "subject", principal.subject(),
                        "tenantSlug", principal.tenantSlug(),
                        "roles", principal.roles()
                )
        );
    }
}
```

### `platform/SuperadminBootstrapProperties.java`

```java
package com.financesystem.finance_api.bootstrap.platform;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap.superadmin")
public class SuperadminBootstrapProperties {

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
```

### `platform/TenantMigrationTestController.java`

```java
package com.financesystem.finance_api.bootstrap.platform;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.tenancy.migration.TenantSchemaMigrationService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/dev-tenancy")
@Profile("dev")
@Hidden
public class TenantMigrationTestController {

    private final TenantSchemaMigrationService tenantSchemaMigrationService;

    public TenantMigrationTestController(TenantSchemaMigrationService tenantSchemaMigrationService) {
        this.tenantSchemaMigrationService = tenantSchemaMigrationService;
    }

    @PostMapping("/migrate-schema/{schemaName}")
    public ApiResponse<Map<String, String>> migrateTenantSchema(@PathVariable String schemaName) {
        tenantSchemaMigrationService.migrateSchema(schemaName);

        return ApiResponse.success(
                "Tenant schema migrated successfully",
                Map.of("schemaName", schemaName)
        );
    }
}
```

### `sample/SampleDataBootstrapProperties.java`

```java
package com.financesystem.finance_api.bootstrap.sample;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap.sample-data")
public class SampleDataBootstrapProperties {

    /**
     * Enables optional sample data seeds on top of the always-on bootstrap data.
     */
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

```

### `sample/SampleDataBootstrapService.java`

```java
package com.financesystem.finance_api.bootstrap.sample;

import com.financesystem.finance_api.modules.platform.tenants.application.dto.CreatePlatformTenantRequest;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.usecase.CreatePlatformTenantUseCase;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionProvisioningService;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SampleDataBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataBootstrapService.class);
    private static final String DEMO_PASSWORD = "password";
    private static final String DEMO_IDEMPOTENCY_PREFIX = "sample-bootstrap";
    private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal("150.00");

    private final CreatePlatformTenantUseCase createPlatformTenantUseCase;
    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformSubscriptionProvisioningService platformSubscriptionProvisioningService;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public SampleDataBootstrapService(
            CreatePlatformTenantUseCase createPlatformTenantUseCase,
            PlatformTenantRepository platformTenantRepository,
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformSubscriptionProvisioningService platformSubscriptionProvisioningService,
            @Qualifier("targetDataSource") DataSource targetDataSource,
            PasswordEncoder passwordEncoder
    ) {
        this.createPlatformTenantUseCase = createPlatformTenantUseCase;
        this.platformTenantRepository = platformTenantRepository;
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformSubscriptionProvisioningService = platformSubscriptionProvisioningService;
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
        this.passwordEncoder = passwordEncoder;
    }

    public void seedDemoTenants() {
        logger.info("Seeding optional sample tenants and demo data...");

        for (TenantSeed seed : sampleTenants()) {
            try {
                PlatformTenantResponse tenant = ensureTenant(seed);
                seedTenantBundle(seed, tenant);
            } catch (Exception exception) {
                logger.warn("Sample tenant '{}' could not be fully seeded. Continuing with the next seed.", seed.slug(), exception);
            }
        }

        logger.info("Optional sample tenants seeding completed.");
    }

    private PlatformTenantResponse ensureTenant(TenantSeed seed) {
        return platformTenantRepository.findBySlug(seed.slug())
                .map(tenant -> new PlatformTenantResponse(
                        tenant.id(),
                        tenant.name(),
                        tenant.slug(),
                        tenant.schemaName(),
                        tenant.status().name(),
                        tenant.planId(),
                        tenant.active(),
                        tenant.createdAt(),
                        tenant.updatedAt()
                ))
                .orElseGet(() -> createPlatformTenantUseCase.execute(
                        new CreatePlatformTenantRequest(
                                seed.name(),
                                seed.slug(),
                                seed.planCode(),
                                seed.ownerEmail(),
                                DEMO_PASSWORD,
                                seed.ownerFirstName(),
                                seed.ownerLastName()
                        )
                ));
    }

    private void seedTenantBundle(TenantSeed seed, PlatformTenantResponse tenant) {
        String schemaName = tenant.schemaName();
        logger.info("Seeding demo data for tenant '{}' in schema '{}'.", seed.slug(), schemaName);

        UUID ownerUserId = upsertTenantUser(
                schemaName,
                seed.ownerEmail(),
                seed.ownerFirstName(),
                seed.ownerLastName(),
                "OWNER_ADMIN"
        );

        ensurePlatformSubscription(seed);
        PlatformTenant platformTenant = platformTenantRepository.findBySlug(seed.slug())
                .orElseThrow(() -> new IllegalStateException("Tenant not found for platform audit seeding: " + seed.slug()));
        seedPlatformAuditEvents(platformTenant, seed, ownerUserId);

        ensureAccountSequence(schemaName, "WALLET", "BOB", 1L);
        ensureAccountSequence(schemaName, "SAVINGS", "BOB", 1L);

        String ownerAccountNumber = accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L);
        String savingsAccountNumber = accountNumber(seed.accountPrefix(), "SAVINGS", "BOB", 1L);

        UUID ownerAccountId = upsertTenantAccount(
                schemaName,
                ownerUserId,
                ownerAccountNumber,
                "MAIN_WALLET",
                "WALLET",
                "BOB",
                seed.ownerStartingBalance(),
                true,
                "Billetera principal demo"
        );

        UUID savingsAccountId = upsertTenantAccount(
                schemaName,
                ownerUserId,
                savingsAccountNumber,
                "SAVINGS_ACCOUNT",
                "SAVINGS",
                "BOB",
                seed.savingsStartingBalance(),
                false,
                "Cuenta de ahorro demo"
        );

        UUID ownerTransactionId = seedOwnerFinanceBundle(
                tenant,
                schemaName,
                seed,
                ownerUserId,
                ownerAccountId,
                savingsAccountId
        );

        runOptionalSeedStep("service enrollment", seed.slug(), () -> upsertServiceEnrollment(
                schemaName,
                ownerUserId,
                seed.serviceProviderCode(),
                seed.serviceCustomerCode(),
                seed.serviceCustomerName(),
                seed.serviceAlias()
        ));

        runOptionalSeedStep("tenant users", seed.slug(), () -> seedEnterpriseTenantUsers(
                tenant,
                schemaName,
                seed,
                ownerUserId,
                ownerAccountId
        ));

        logger.info(
                "Demo bundle seeded for tenant '{}': owner '{}'.",
                seed.slug(),
                seed.ownerEmail()
        );
    }

    private void ensurePlatformSubscription(TenantSeed seed) {
        PlatformTenant tenant = platformTenantRepository.findBySlug(seed.slug())
                .orElseThrow(() -> new IllegalStateException("Tenant not found for subscription seeding: " + seed.slug()));

        if (platformSubscriptionRepository.findCurrentByTenantId(tenant.id()).isPresent()) {
            return;
        }

        platformSubscriptionProvisioningService.assignCurrentSubscription(
                tenant.id(),
                seed.planCode(),
                null,
                true
        );
    }

    private void runOptionalSeedStep(String stepName, String tenantSlug, Runnable step) {
        try {
            step.run();
        } catch (Exception exception) {
            logger.warn("Sample tenant '{}' failed during {} seeding. Continuing with the next step.", tenantSlug, stepName, exception);
        }
    }

    private UUID seedOwnerFinanceBundle(
            PlatformTenantResponse tenant,
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            UUID ownerAccountId,
            UUID savingsAccountId
    ) {
        String ownerAccountNumber = accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L);
        String savingsAccountNumber = accountNumber(seed.accountPrefix(), "SAVINGS", "BOB", 1L);

        List<UUID> ownerTransactionIds = seedAlternatingTransferSeriesIds(
                schemaName,
                seed,
                ownerUserId,
                ownerAccountId,
                ownerAccountNumber,
                seed.ownerStartingBalance(),
                savingsAccountId,
                savingsAccountNumber,
                seed.savingsStartingBalance(),
                "owner-transfer",
                10
        );
        UUID ownerTransactionId = ownerTransactionIds.get(ownerTransactionIds.size() - 1);

        upsertAccountingPeriods(schemaName, seed, ownerTransactionId);
        upsertJournalEntry(schemaName, seed, ownerTransactionId, ownerAccountId, savingsAccountId);
        upsertLimitRules(schemaName, seed, ownerUserId);
        upsertFxConfiguration(schemaName, seed);
        upsertOperationFees(schemaName, seed);
        upsertNotifications(schemaName, seed, ownerUserId, ownerTransactionId, "owner");
        upsertTenantAuditEvents(schemaName, seed, ownerUserId, ownerTransactionId);
        seedPublicServicePaymentsForUser(
                schemaName,
                tenant,
                seed,
                ownerUserId,
                ownerAccountId,
                ownerAccountNumber,
                seed.serviceCustomerCode(),
                seed.serviceCustomerName(),
                seed.serviceAlias(),
                ownerTransactionIds,
                "owner"
        );

        return ownerTransactionId;
    }

    private void seedEnterpriseTenantUsers(
            PlatformTenantResponse tenant,
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            UUID ownerAccountId
    ) {
        List<TenantUserSeed> userSeeds = tenantUserSeeds(seed);
        for (int index = 0; index < userSeeds.size(); index++) {
            TenantUserSeed userSeed = userSeeds.get(index);
            int userNumber = index + 1;

            UUID userUserId = upsertTenantUser(
                    schemaName,
                    userSeed.email(),
                    userSeed.firstName(),
                    userSeed.lastName(),
                    "USER"
            );

            long walletSequence = userNumber + 1L;
            long checkingSequence = userNumber;
            ensureAccountSequence(schemaName, "WALLET", "BOB", walletSequence);
            ensureAccountSequence(schemaName, "CHECKING", "BOB", checkingSequence);

            String walletAccountNumber = accountNumber(seed.accountPrefix(), "WALLET", "BOB", walletSequence);
            String checkingAccountNumber = accountNumber(seed.accountPrefix(), "CHECKING", "BOB", checkingSequence);

            UUID walletAccountId = upsertTenantAccount(
                    schemaName,
                    userUserId,
                    walletAccountNumber,
                    "MAIN_WALLET",
                    "WALLET",
                    "BOB",
                    new BigDecimal("1000.00"),
                    true,
                    userSeed.firstName() + " wallet"
            );

            UUID checkingAccountId = upsertTenantAccount(
                    schemaName,
                    userUserId,
                    checkingAccountNumber,
                    "CHECKING_ACCOUNT",
                    "CHECKING",
                    "BOB",
                    new BigDecimal("0.00"),
                    false,
                    userSeed.firstName() + " checking"
            );

            List<UUID> transferTransactionIds = seedAlternatingTransferSeriesIds(
                    schemaName,
                    seed,
                    userUserId,
                    walletAccountId,
                    walletAccountNumber,
                    new BigDecimal("1000.00"),
                    checkingAccountId,
                    checkingAccountNumber,
                    new BigDecimal("0.00"),
                    "user-" + String.format("%02d", userNumber) + "-transfer",
                    10
            );
            UUID lastTransactionId = transferTransactionIds.get(transferTransactionIds.size() - 1);

            upsertNotifications(schemaName, seed, userUserId, lastTransactionId, "user-" + String.format("%02d", userNumber));
            insertTenantAuditEvent(
                    schemaName,
                    seed,
                    userUserId,
                    "TRANSFER_RECEIVED",
                    "TRANSACTIONS",
                    lastTransactionId.toString(),
                    "Usuario demo recibió una serie de transferencias"
            );
            seedPublicServicePaymentsForUser(
                    schemaName,
                    tenant,
                    seed,
                    userUserId,
                    checkingAccountId,
                    checkingAccountNumber,
                    tenantServiceCustomerCode(seed, userNumber),
                    tenantServiceCustomerName(seed, userSeed.firstName(), userNumber),
                    tenantServiceAlias(seed, userSeed.firstName(), userNumber),
                    transferTransactionIds,
                    "user-" + String.format("%02d", userNumber)
            );
        }
    }

    private List<UUID> seedAlternatingTransferSeriesIds(
            String schemaName,
            TenantSeed seed,
            UUID requestedByUserId,
            UUID firstAccountId,
            String firstAccountNumber,
            BigDecimal firstStartingBalance,
            UUID secondAccountId,
            String secondAccountNumber,
            BigDecimal secondStartingBalance,
            String transactionKeyPrefix,
            int transactionCount
    ) {
        BigDecimal firstBalance = firstStartingBalance;
        BigDecimal secondBalance = secondStartingBalance;
        List<UUID> transactionIds = new ArrayList<>();

        for (int index = 1; index <= transactionCount; index++) {
            boolean firstIsSource = index % 2 == 1;

            UUID sourceAccountId = firstIsSource ? firstAccountId : secondAccountId;
            UUID targetAccountId = firstIsSource ? secondAccountId : firstAccountId;
            String sourceAccountNumber = firstIsSource ? firstAccountNumber : secondAccountNumber;
            String targetAccountNumber = firstIsSource ? secondAccountNumber : firstAccountNumber;
            BigDecimal sourceBefore = firstIsSource ? firstBalance : secondBalance;
            BigDecimal targetBefore = firstIsSource ? secondBalance : firstBalance;

            UUID transactionId = upsertTransferTransaction(
                    schemaName,
                    seed,
                    requestedByUserId,
                    sourceAccountId,
                    targetAccountId,
                    transactionKeyPrefix + "-" + index
            );
            transactionIds.add(transactionId);

            BigDecimal sourceAfter = sourceBefore.subtract(TRANSFER_AMOUNT);
            BigDecimal targetAfter = targetBefore.add(TRANSFER_AMOUNT);

            upsertTransactionMovement(
                    schemaName,
                    transactionId,
                    sourceAccountId,
                    "DEBIT",
                    TRANSFER_AMOUNT,
                    sourceBefore,
                    sourceAfter,
                    "Salida por transferencia demo"
            );

            upsertTransactionMovement(
                    schemaName,
                    transactionId,
                    targetAccountId,
                    "CREDIT",
                    TRANSFER_AMOUNT,
                    targetBefore,
                    targetAfter,
                    "Entrada por transferencia demo"
            );

            jdbcTemplate.update(
                    """
                    UPDATE %s.tenant_accounts
                    SET available_balance = CASE
                            WHEN account_number = ? THEN ?
                            WHEN account_number = ? THEN ?
                            ELSE available_balance
                        END,
                        updated_at = NOW()
                    WHERE account_number IN (?, ?)
                    """.formatted(schemaName),
                    sourceAccountNumber,
                    sourceAfter,
                    targetAccountNumber,
                    targetAfter,
                    sourceAccountNumber,
                    targetAccountNumber
            );

            if (firstIsSource) {
                firstBalance = sourceAfter;
                secondBalance = targetAfter;
            } else {
                secondBalance = sourceAfter;
                firstBalance = targetAfter;
            }
        }

        return transactionIds;
    }

    private UUID seedAlternatingTransferSeries(
            String schemaName,
            TenantSeed seed,
            UUID requestedByUserId,
            UUID firstAccountId,
            String firstAccountNumber,
            BigDecimal firstStartingBalance,
            UUID secondAccountId,
            String secondAccountNumber,
            BigDecimal secondStartingBalance,
            String transactionKeyPrefix,
            int transactionCount
    ) {
        List<UUID> transactionIds = seedAlternatingTransferSeriesIds(
                schemaName,
                seed,
                requestedByUserId,
                firstAccountId,
                firstAccountNumber,
                firstStartingBalance,
                secondAccountId,
                secondAccountNumber,
                secondStartingBalance,
                transactionKeyPrefix,
                transactionCount
        );
        return transactionIds.get(transactionIds.size() - 1);
    }

    private List<TenantUserSeed> tenantUserSeeds(TenantSeed seed) {
        return List.of(
                new TenantUserSeed(seed.userEmail(), seed.userFirstName(), seed.userLastName()),
                generatedTenantUserSeed(seed, 2),
                generatedTenantUserSeed(seed, 3),
                generatedTenantUserSeed(seed, 4),
                generatedTenantUserSeed(seed, 5),
                generatedTenantUserSeed(seed, 6),
                generatedTenantUserSeed(seed, 7),
                generatedTenantUserSeed(seed, 8),
                generatedTenantUserSeed(seed, 9)
        );
    }

    private TenantUserSeed generatedTenantUserSeed(TenantSeed seed, int index) {
        String slugBase = seed.slug().replace("-", "");
        return new TenantUserSeed(
                slugBase + String.format("%02d", index) + "@gmail.com",
                "User" + String.format("%02d", index),
                "Demo"
        );
    }

    private void seedPlatformAuditEvents(PlatformTenant tenant, TenantSeed seed, UUID ownerUserId) {
        insertPlatformAuditEvent(
                tenant,
                seed,
                ownerUserId,
                "TENANT_BOOTSTRAPPED",
                "TENANT",
                tenant.id().toString(),
                "Tenant demo created and seeded"
        );
        insertPlatformAuditEvent(
                tenant,
                seed,
                ownerUserId,
                "SUBSCRIPTION_ASSIGNED",
                "SUBSCRIPTION",
                seed.planCode(),
                "Current subscription assigned for demo tenant"
        );
    }

    private void insertPlatformAuditEvent(
            PlatformTenant tenant,
            TenantSeed seed,
            UUID ownerUserId,
            String eventType,
            String resourceType,
            String resourceId,
            String details
    ) {
        UUID eventId = deterministicUuid("public:platform-audit:" + tenant.slug() + ":" + eventType + ":" + resourceId);
        jdbcTemplate.update(
                """
                INSERT INTO public.platform_audit_events (
                    id, actor_subject, actor_id, actor_email, tenant_slug, event_type, resource_type,
                    resource_id, event_details, source, outcome, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'BOOTSTRAP', 'SUCCESS', NOW())
                ON CONFLICT (id) DO UPDATE SET
                    actor_subject = EXCLUDED.actor_subject,
                    actor_id = EXCLUDED.actor_id,
                    actor_email = EXCLUDED.actor_email,
                    tenant_slug = EXCLUDED.tenant_slug,
                    event_type = EXCLUDED.event_type,
                    resource_type = EXCLUDED.resource_type,
                    resource_id = EXCLUDED.resource_id,
                    event_details = EXCLUDED.event_details,
                    source = EXCLUDED.source,
                    outcome = EXCLUDED.outcome
                """,
                eventId,
                ownerUserId.toString(),
                ownerUserId,
                seed.ownerEmail(),
                tenant.slug(),
                eventType,
                resourceType,
                resourceId,
                details
        );
    }

    private record TenantUserSeed(
            String email,
            String firstName,
            String lastName
    ) {
    }

    private UUID upsertTenantUser(
            String schemaName,
            String email,
            String firstName,
            String lastName,
            String roleName
    ) {
        String normalizedEmail = email.trim().toLowerCase();
        String normalizedFirstName = firstName.trim();
        String normalizedLastName = lastName.trim();
        String passwordHash = passwordEncoder.encode(DEMO_PASSWORD);
        UUID userId = deterministicUuid(schemaName + ":user:" + normalizedEmail);

        UUID persistedUserId = jdbcTemplate.queryForObject(
                """
                INSERT INTO %s.tenant_users (
                    id, email, password_hash, first_name, last_name, active, status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, true, 'ACTIVE', NOW(), NOW())
                ON CONFLICT (email) DO UPDATE SET
                    password_hash = EXCLUDED.password_hash,
                    first_name = EXCLUDED.first_name,
                    last_name = EXCLUDED.last_name,
                    active = true,
                    status = 'ACTIVE',
                    updated_at = NOW()
                RETURNING id
                """.formatted(schemaName),
                UUID.class,
                userId,
                normalizedEmail,
                passwordHash,
                normalizedFirstName,
                normalizedLastName
        );

        UUID roleId = fetchRequiredRoleId(schemaName, roleName);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_user_roles (user_id, role_id, assigned_at)
                VALUES (?, ?, NOW())
                ON CONFLICT (user_id, role_id) DO NOTHING
                """.formatted(schemaName),
                persistedUserId,
                roleId
        );

        return persistedUserId;
    }

    private UUID upsertTenantAccount(
            String schemaName,
            UUID userId,
            String accountNumber,
            String accountName,
            String accountType,
            String currency,
            BigDecimal availableBalance,
            boolean primaryAccount,
            String customAlias
    ) {
        UUID accountId = deterministicUuid(schemaName + ":account:" + accountNumber);
        String normalizedAlias = customAlias == null ? null : customAlias.trim();

        return jdbcTemplate.queryForObject(
                """
                INSERT INTO %s.tenant_accounts (
                    id, user_id, account_number, account_name, custom_alias, account_type, currency,
                    available_balance, held_balance, status, active, is_primary, opened_at, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 'ACTIVE', true, ?, NOW(), NOW(), NOW())
                ON CONFLICT (account_number) DO UPDATE SET
                    user_id = EXCLUDED.user_id,
                    account_name = EXCLUDED.account_name,
                    custom_alias = EXCLUDED.custom_alias,
                    account_type = EXCLUDED.account_type,
                    currency = EXCLUDED.currency,
                    available_balance = EXCLUDED.available_balance,
                    held_balance = 0,
                    status = 'ACTIVE',
                    active = true,
                    is_primary = EXCLUDED.is_primary,
                    status_reason = NULL,
                    updated_at = NOW()
                RETURNING id
                """.formatted(schemaName),
                UUID.class,
                accountId,
                userId,
                accountNumber,
                accountName,
                normalizedAlias,
                accountType,
                currency,
                availableBalance,
                primaryAccount
        );
    }

    private UUID upsertTransferTransaction(
            String schemaName,
            TenantSeed seed,
            UUID requestedByUserId,
            UUID sourceAccountId,
            UUID targetAccountId,
            String transactionKeySuffix
    ) {
        String idempotencyKey = DEMO_IDEMPOTENCY_PREFIX + ":" + seed.slug() + ":" + transactionKeySuffix;
        UUID transactionId = deterministicUuid(schemaName + ":transaction:" + idempotencyKey);
        BigDecimal amount = TRANSFER_AMOUNT;
        String externalReference = "SEED-" + seed.slug().toUpperCase() + "-" + transactionKeySuffix.toUpperCase().replaceAll("[^A-Z0-9]+", "-");

        return jdbcTemplate.queryForObject(
                """
                INSERT INTO %s.tenant_transactions (
                    id, type, status, channel, amount, currency, source_account_id, target_account_id,
                    external_reference, idempotency_key, description, failure_reason, metadata,
                    parent_transaction_id, reversed_transaction_id, requested_by_user_id, approved_by_user_id,
                    processed_at, created_at, updated_at
                )
                VALUES (
                    ?, 'TRANSFER', 'COMPLETED', 'MANUAL', ?, 'BOB', ?, ?,
                    ?, ?, ?, NULL, '{}'::jsonb,
                    NULL, NULL, ?, ?, NOW(), NOW(), NOW()
                )
                ON CONFLICT (requested_by_user_id, idempotency_key) DO UPDATE SET
                    type = EXCLUDED.type,
                    status = EXCLUDED.status,
                    channel = EXCLUDED.channel,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    source_account_id = EXCLUDED.source_account_id,
                    target_account_id = EXCLUDED.target_account_id,
                    external_reference = EXCLUDED.external_reference,
                    description = EXCLUDED.description,
                    failure_reason = NULL,
                    metadata = EXCLUDED.metadata,
                    approved_by_user_id = EXCLUDED.approved_by_user_id,
                    processed_at = NOW(),
                    updated_at = NOW()
                RETURNING id
                """.formatted(schemaName),
                UUID.class,
                transactionId,
                amount,
                sourceAccountId,
                targetAccountId,
                externalReference,
                idempotencyKey,
                "Demo transfer between seeded accounts - " + transactionKeySuffix,
                requestedByUserId,
                requestedByUserId
        );
    }

    private void seedRegularUserBundle(
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            UUID ownerAccountId
    ) {
        UUID userUserId = upsertTenantUser(
                schemaName,
                seed.userEmail(),
                seed.userFirstName(),
                seed.userLastName(),
                "USER"
        );

        ensureAccountSequence(schemaName, "CHECKING", "BOB", 1L);

        String userAccountNumber = accountNumber(seed.accountPrefix(), "CHECKING", "BOB", 1L);
        UUID userAccountId = upsertTenantAccount(
                schemaName,
                userUserId,
                userAccountNumber,
                "CHECKING_ACCOUNT",
                "CHECKING",
                "BOB",
                new BigDecimal("0.00"),
                false,
                "Cuenta bancaria usuario demo"
        );

        UUID userTransactionId = upsertTransferTransaction(
                schemaName,
                seed,
                ownerUserId,
                ownerAccountId,
                userAccountId,
                "user-transfer-1"
        );

        BigDecimal ownerBefore = seed.ownerStartingBalance().subtract(TRANSFER_AMOUNT);
        BigDecimal ownerAfter = ownerBefore.subtract(TRANSFER_AMOUNT);
        BigDecimal userBefore = new BigDecimal("0.00");
        BigDecimal userAfter = userBefore.add(TRANSFER_AMOUNT);

        upsertTransactionMovement(
                schemaName,
                userTransactionId,
                ownerAccountId,
                "DEBIT",
                TRANSFER_AMOUNT,
                ownerBefore,
                ownerAfter,
                "Salida por transferencia a usuario demo"
        );

        upsertTransactionMovement(
                schemaName,
                userTransactionId,
                userAccountId,
                "CREDIT",
                TRANSFER_AMOUNT,
                userBefore,
                userAfter,
                "Entrada por transferencia demo"
        );

        jdbcTemplate.update(
                """
                UPDATE %s.tenant_accounts
                SET available_balance = CASE
                        WHEN account_number = ? THEN ?
                        WHEN account_number = ? THEN ?
                        ELSE available_balance
                    END,
                    updated_at = NOW()
                WHERE account_number IN (?, ?)
                """.formatted(schemaName),
                accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L),
                ownerAfter,
                userAccountNumber,
                userAfter,
                accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L),
                userAccountNumber
        );

        upsertNotifications(schemaName, seed, userUserId, userTransactionId, "user");
        insertTenantAuditEvent(
                schemaName,
                seed,
                userUserId,
                "TRANSFER_RECEIVED",
                "TRANSACTIONS",
                userTransactionId.toString(),
                "Usuario demo recibió una transferencia"
        );
    }

    private void upsertTransactionMovements(
            String schemaName,
            TenantSeed seed,
            UUID transactionId,
            UUID sourceAccountId,
            UUID targetAccountId
    ) {
        BigDecimal ownerBefore = seed.ownerStartingBalance();
        BigDecimal ownerAfter = ownerBefore.subtract(TRANSFER_AMOUNT);
        BigDecimal customerBefore = seed.savingsStartingBalance();
        BigDecimal customerAfter = customerBefore.add(TRANSFER_AMOUNT);

        upsertTransactionMovement(
                schemaName,
                transactionId,
                sourceAccountId,
                "DEBIT",
                TRANSFER_AMOUNT,
                ownerBefore,
                ownerAfter,
                "Salida por transferencia demo"
        );

        upsertTransactionMovement(
                schemaName,
                transactionId,
                targetAccountId,
                "CREDIT",
                TRANSFER_AMOUNT,
                customerBefore,
                customerAfter,
                "Entrada por transferencia demo"
        );

        jdbcTemplate.update(
                """
                UPDATE %s.tenant_accounts
                SET available_balance = CASE
                        WHEN account_number = ? THEN ?
                        WHEN account_number = ? THEN ?
                        ELSE available_balance
                    END,
                    updated_at = NOW()
                WHERE account_number IN (?, ?)
                """.formatted(schemaName),
                accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L),
                ownerAfter,
                accountNumber(seed.accountPrefix(), "SAVINGS", "BOB", 1L),
                customerAfter,
                accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L),
                accountNumber(seed.accountPrefix(), "SAVINGS", "BOB", 1L)
        );
    }

    private void upsertTransactionMovement(
            String schemaName,
            UUID transactionId,
            UUID accountId,
            String movementType,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            String description
    ) {
        UUID movementId = deterministicUuid(schemaName + ":movement:" + transactionId + ":" + accountId + ":" + movementType);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_transaction_movements (
                    id, transaction_id, account_id, movement_type, amount, currency,
                    balance_before, balance_after, description, created_at
                )
                VALUES (?, ?, ?, ?, ?, 'BOB', ?, ?, ?, NOW())
                ON CONFLICT (id) DO UPDATE SET
                    transaction_id = EXCLUDED.transaction_id,
                    account_id = EXCLUDED.account_id,
                    movement_type = EXCLUDED.movement_type,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    balance_before = EXCLUDED.balance_before,
                    balance_after = EXCLUDED.balance_after,
                    description = EXCLUDED.description
                """.formatted(schemaName),
                movementId,
                transactionId,
                accountId,
                movementType,
                amount,
                balanceBefore,
                balanceAfter,
                description
        );
    }

    private void upsertAccountingPeriods(String schemaName, TenantSeed seed, UUID sourceTransactionId) {
        String currentPeriodCode = currentPeriodCode(seed);
        String previousPeriodCode = previousPeriodCode(seed);

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_accounting_periods (
                    id, period_code, period_type, status, start_date, end_date, closed_at, description, created_at, updated_at
                )
                VALUES (?, ?, 'MONTHLY', 'OPEN', date_trunc('month', NOW())::date, (date_trunc('month', NOW()) + INTERVAL '1 month - 1 day')::date, NULL, ?, NOW(), NOW())
                ON CONFLICT (period_code) DO UPDATE SET
                    period_type = 'MONTHLY',
                    status = 'OPEN',
                    closed_at = NULL,
                    description = EXCLUDED.description,
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":period:" + currentPeriodCode),
                currentPeriodCode,
                "Periodo mensual abierto demo"
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_accounting_periods (
                    id, period_code, period_type, status, start_date, end_date, closed_at, description, created_at, updated_at
                )
                VALUES (?, ?, 'MONTHLY', 'CLOSED', (date_trunc('month', NOW()) - INTERVAL '1 month')::date, (date_trunc('month', NOW()) - INTERVAL '1 day')::date, NOW(), ?, NOW(), NOW())
                ON CONFLICT (period_code) DO UPDATE SET
                    period_type = 'MONTHLY',
                    status = 'CLOSED',
                    closed_at = NOW(),
                    description = EXCLUDED.description,
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":period:" + previousPeriodCode),
                previousPeriodCode,
                "Periodo mensual cerrado demo"
        );
    }

    private void upsertJournalEntry(String schemaName, TenantSeed seed, UUID sourceTransactionId, UUID debitAccountId, UUID creditAccountId) {
        String entryNumber = seed.slug().toUpperCase() + "-JE-001";
        String periodCode = currentPeriodCode(seed);
        UUID entryId = deterministicUuid(schemaName + ":journal:" + entryNumber);
        UUID periodId = deterministicUuid(schemaName + ":period:" + periodCode);

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_journal_entries (
                    id, entry_number, source_transaction_id, period_id, entry_type, status,
                    description, reference, total_debits, total_credits, posted_at, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, 'TRANSFER', 'POSTED', ?, ?, ?, ?, NOW(), NOW(), NOW())
                ON CONFLICT (entry_number) DO UPDATE SET
                    source_transaction_id = EXCLUDED.source_transaction_id,
                    period_id = EXCLUDED.period_id,
                    entry_type = EXCLUDED.entry_type,
                    status = EXCLUDED.status,
                    description = EXCLUDED.description,
                    reference = EXCLUDED.reference,
                    total_debits = EXCLUDED.total_debits,
                    total_credits = EXCLUDED.total_credits,
                    posted_at = NOW(),
                    updated_at = NOW()
                """.formatted(schemaName),
                entryId,
                entryNumber,
                sourceTransactionId,
                periodId,
                "Asiento contable demo",
                "SEED-" + seed.slug().toUpperCase() + "-001",
                TRANSFER_AMOUNT,
                TRANSFER_AMOUNT
        );

        upsertJournalLine(schemaName, entryId, 1, "MAIN_WALLET", "Billetera principal", "DEBIT", TRANSFER_AMOUNT, "BOB", "Débito por transferencia demo");
        upsertJournalLine(schemaName, entryId, 2, "SAVINGS_ACCOUNT", "Cuenta de ahorro", "CREDIT", TRANSFER_AMOUNT, "BOB", "Crédito por transferencia demo");
    }

    private void upsertJournalLine(
            String schemaName,
            UUID journalEntryId,
            int lineNo,
            String accountCode,
            String accountName,
            String lineType,
            BigDecimal amount,
            String currency,
            String description
    ) {
        UUID lineId = deterministicUuid(schemaName + ":journal-line:" + journalEntryId + ":" + lineNo);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_journal_lines (
                    id, journal_entry_id, line_no, account_code, account_name, line_type,
                    amount, currency, description, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
                ON CONFLICT (journal_entry_id, line_no) DO UPDATE SET
                    account_code = EXCLUDED.account_code,
                    account_name = EXCLUDED.account_name,
                    line_type = EXCLUDED.line_type,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    description = EXCLUDED.description
                """.formatted(schemaName),
                lineId,
                journalEntryId,
                lineNo,
                accountCode,
                accountName,
                lineType,
                amount,
                currency,
                description
        );
    }

    private void upsertLimitRules(String schemaName, TenantSeed seed, UUID ownerUserId) {
        insertLimitRule(schemaName, seed, ownerUserId, "DAILY_TRANSFER_AMOUNT", "Límite diario de transferencias", "DAILY_AMOUNT", "TENANT", "DAILY", "TRANSFER", null, "BOB", new BigDecimal("2500.00"), null, true);
        insertLimitRule(schemaName, seed, ownerUserId, "MONTHLY_WITHDRAWAL_COUNT", "Límite mensual de retiros", "MONTHLY_COUNT", "TENANT", "MONTHLY", "WITHDRAWAL", null, null, null, 20L, true);
    }

    private void upsertFxConfiguration(String schemaName, TenantSeed seed) {
        upsertFxRate(schemaName, seed, "BOB", "USD", new BigDecimal("0.14500000"), "BOB a USD");
        upsertFxRate(schemaName, seed, "USD", "BOB", new BigDecimal("6.89000000"), "USD a BOB");
        upsertFxRate(schemaName, seed, "BOB", "EUR", new BigDecimal("0.13200000"), "BOB a EUR");
        upsertFxRate(schemaName, seed, "EUR", "BOB", new BigDecimal("7.42000000"), "EUR a BOB");
    }

    private void upsertFxRate(String schemaName, TenantSeed seed, String sourceCurrency, String targetCurrency, BigDecimal rate, String description) {
        UUID rateId = deterministicUuid(schemaName + ":fx-rate:" + sourceCurrency + ":" + targetCurrency);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_fx_exchange_rates (
                    id, source_currency, target_currency, rate, active, description, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, true, ?, NOW(), NOW())
                ON CONFLICT (source_currency, target_currency) DO UPDATE SET
                    rate = EXCLUDED.rate,
                    active = true,
                    description = EXCLUDED.description,
                    updated_at = NOW()
                """.formatted(schemaName),
                rateId,
                sourceCurrency,
                targetCurrency,
                rate,
                description + " - " + seed.slug()
        );
    }

    private void upsertOperationFees(String schemaName, TenantSeed seed) {
        upsertOperationFee(schemaName, seed, "TRANSFER", "FIXED", new BigDecimal("1.50"), "SEPARATE", "Tarifa por transferencia");
        upsertOperationFee(schemaName, seed, "WITHDRAWAL", "PERCENTAGE", new BigDecimal("0.50"), "SEPARATE", "Tarifa por retiro");
        upsertOperationFee(schemaName, seed, "PAYMENT", "FIXED", new BigDecimal("0.80"), "INCLUDED", "Tarifa por pago");
        upsertOperationFee(schemaName, seed, "SERVICE_PAYMENT", "FIXED", new BigDecimal("0.75"), "SEPARATE", "Tarifa por pago de servicios");
    }

    private void upsertOperationFee(
            String schemaName,
            TenantSeed seed,
            String operationCode,
            String feeType,
            BigDecimal feeValue,
            String calculationMode,
            String description
    ) {
        UUID feeId = deterministicUuid(schemaName + ":operation-fee:" + operationCode);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_operation_fees (
                    id, operation_code, fee_type, fee_value, calculation_mode, active, description, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, true, ?, NOW(), NOW())
                ON CONFLICT (operation_code) DO UPDATE SET
                    fee_type = EXCLUDED.fee_type,
                    fee_value = EXCLUDED.fee_value,
                    calculation_mode = EXCLUDED.calculation_mode,
                    active = true,
                    description = EXCLUDED.description,
                    updated_at = NOW()
                """.formatted(schemaName),
                feeId,
                operationCode,
                feeType,
                feeValue,
                calculationMode,
                description + " - " + seed.slug()
        );
    }

    private void insertLimitRule(
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            String suffix,
            String name,
            String limitType,
            String scopeType,
            String period,
            String transactionType,
            String accountType,
            String currency,
            BigDecimal maxAmount,
            Long maxCount,
            boolean review
    ) {
        String code = seed.slug().toUpperCase() + "-" + suffix;
        UUID ruleId = deterministicUuid(schemaName + ":limit:" + code);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_limit_rules (
                    id, code, name, description, limit_type, scope_type, period, transaction_type, account_type,
                    currency, min_amount, max_amount, max_count, active, require_review_exceed,
                    created_by_user_id, updated_by_user_id, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?, true, ?, ?, ?, NOW(), NOW())
                ON CONFLICT (code) DO UPDATE SET
                    name = EXCLUDED.name,
                    description = EXCLUDED.description,
                    limit_type = EXCLUDED.limit_type,
                    scope_type = EXCLUDED.scope_type,
                    period = EXCLUDED.period,
                    transaction_type = EXCLUDED.transaction_type,
                    account_type = EXCLUDED.account_type,
                    currency = EXCLUDED.currency,
                    min_amount = EXCLUDED.min_amount,
                    max_amount = EXCLUDED.max_amount,
                    max_count = EXCLUDED.max_count,
                    active = true,
                    require_review_exceed = EXCLUDED.require_review_exceed,
                    updated_by_user_id = EXCLUDED.updated_by_user_id,
                    updated_at = NOW()
                """.formatted(schemaName),
                ruleId,
                code,
                name,
                "Regla demo " + name,
                limitType,
                scopeType,
                period,
                transactionType,
                accountType,
                currency,
                maxAmount,
                maxCount,
                review,
                ownerUserId,
                ownerUserId
        );
    }

    private void upsertNotifications(String schemaName, TenantSeed seed, UUID userId, UUID transactionId, String notificationKeySuffix) {
        UUID unreadId = deterministicUuid(schemaName + ":notification:" + notificationKeySuffix + ":unread");
        UUID readId = deterministicUuid(schemaName + ":notification:" + notificationKeySuffix + ":read");
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_notifications (
                    id, user_id, type, category, priority, title, body, data, image_url, action_url,
                    read_at, opened_at, archived_at, expires_at, created_at, updated_at
                )
                VALUES (?, ?, 'TRANSFER_SENT', 'TRANSACTIONS', 'HIGH', ?, ?, '{}'::jsonb, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW())
                ON CONFLICT (id) DO UPDATE SET
                    title = EXCLUDED.title,
                    body = EXCLUDED.body,
                    updated_at = NOW()
                """.formatted(schemaName),
                unreadId,
                userId,
                "Transacción demo completada",
                "Se generó la transacción demo " + transactionId
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_notifications (
                    id, user_id, type, category, priority, title, body, data, image_url, action_url,
                    read_at, opened_at, archived_at, expires_at, created_at, updated_at
                )
                VALUES (?, ?, 'SYSTEM_NOTICE', 'ACCOUNTS', 'NORMAL', ?, ?, '{}'::jsonb, NULL, NULL, NOW(), NOW(), NULL, NULL, NOW(), NOW())
                ON CONFLICT (id) DO UPDATE SET
                    title = EXCLUDED.title,
                    body = EXCLUDED.body,
                    read_at = NOW(),
                    opened_at = NOW(),
                    updated_at = NOW()
                """.formatted(schemaName),
                readId,
                userId,
                "Resumen de cuentas demo",
                "Tus cuentas demo ya están listas"
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_notification_preferences (
                    id, user_id, category, push_enabled, in_app_enabled, email_enabled, sms_enabled, created_at, updated_at
                )
                VALUES (?, ?, 'TRANSACTIONS', true, true, false, false, NOW(), NOW())
                ON CONFLICT (user_id, category) DO UPDATE SET
                    push_enabled = true,
                    in_app_enabled = true,
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":notification-pref:" + notificationKeySuffix + ":transactions"),
                userId
        );
    }

    private void upsertTenantAuditEvents(String schemaName, TenantSeed seed, UUID ownerUserId, UUID transactionId) {
        insertTenantAuditEvent(schemaName, seed, ownerUserId, "LOGIN_SUCCESS", "AUTH", ownerUserId.toString(), "Owner login demo");
        insertTenantAuditEvent(schemaName, seed, ownerUserId, "TRANSACTION_CREATED", "TRANSACTIONS", transactionId.toString(), "Demo transfer created");
    }

    private void insertTenantAuditEvent(
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            String eventType,
            String resourceType,
            String resourceId,
            String details
    ) {
        UUID eventId = deterministicUuid(schemaName + ":audit:" + eventType + ":" + resourceId);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_audit_events (
                    id, actor_subject, actor_id, actor_email, tenant_slug, event_type, resource_type,
                    resource_id, event_details, source, outcome, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'BOOTSTRAP', 'SUCCESS', NOW())
                ON CONFLICT (id) DO UPDATE SET
                    actor_subject = EXCLUDED.actor_subject,
                    actor_id = EXCLUDED.actor_id,
                    actor_email = EXCLUDED.actor_email,
                    tenant_slug = EXCLUDED.tenant_slug,
                    event_type = EXCLUDED.event_type,
                    resource_type = EXCLUDED.resource_type,
                    resource_id = EXCLUDED.resource_id,
                    event_details = EXCLUDED.event_details,
                    source = EXCLUDED.source,
                    outcome = EXCLUDED.outcome
                """.formatted(schemaName),
                eventId,
                ownerUserId.toString(),
                ownerUserId,
                seed.ownerEmail(),
                seed.slug(),
                eventType,
                resourceType,
                resourceId,
                details
        );
    }

    private String currentPeriodCode(TenantSeed seed) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return seed.slug().toUpperCase() + "-CUR-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());
    }

    private String previousPeriodCode(TenantSeed seed) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).minusMonths(1);
        return seed.slug().toUpperCase() + "-PREV-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());
    }

    private void upsertServiceEnrollment(
            String schemaName,
            UUID userId,
            String providerCode,
            String serviceCustomerCode,
            String serviceCustomerName,
            String alias
    ) {
        UUID providerId = jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM public.service_providers
                WHERE code = ?
                LIMIT 1
                """,
                UUID.class,
                providerCode
        );

        if (providerId == null) {
            logger.warn("Service provider '{}' not found. Skipping service enrollment seed.", providerCode);
            return;
        }

        String providerName = jdbcTemplate.queryForObject(
                """
                SELECT name
                FROM public.service_providers
                WHERE id = ?
                """,
                String.class,
                providerId
        );

        String providerCategory = jdbcTemplate.queryForObject(
                """
                SELECT category
                FROM public.service_providers
                WHERE id = ?
                """,
                String.class,
                providerId
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_service_enrollments (
                    id, user_id, provider_id, provider_code, provider_name, provider_category,
                    service_customer_code, service_customer_name, alias, status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE', NOW(), NOW())
                ON CONFLICT (user_id, provider_id, service_customer_code) DO UPDATE SET
                    provider_code = EXCLUDED.provider_code,
                    provider_name = EXCLUDED.provider_name,
                    provider_category = EXCLUDED.provider_category,
                    service_customer_name = EXCLUDED.service_customer_name,
                    alias = EXCLUDED.alias,
                    status = 'ACTIVE',
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":service-enrollment:" + userId + ":" + providerCode + ":" + serviceCustomerCode),
                userId,
                providerId,
                providerCode,
                providerName,
                providerCategory,
                serviceCustomerCode,
                serviceCustomerName,
                alias
        );
    }

    private void seedPublicServicePaymentsForUser(
            String schemaName,
            PlatformTenantResponse tenant,
            TenantSeed seed,
            UUID userId,
            UUID accountId,
            String accountNumber,
            String serviceCustomerCode,
            String serviceCustomerName,
            String serviceAlias,
            List<UUID> transactionIds,
            String keyPrefix
    ) {
        UUID providerId = jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM public.service_providers
                WHERE code = ?
                LIMIT 1
                """,
                UUID.class,
                seed.serviceProviderCode()
        );

        if (providerId == null) {
            logger.warn("Service provider '{}' not found. Skipping service bill seed.", seed.serviceProviderCode());
            return;
        }

        upsertServiceEnrollment(
                schemaName,
                userId,
                seed.serviceProviderCode(),
                serviceCustomerCode,
                serviceCustomerName,
                serviceAlias
        );

        UUID serviceCustomerId = upsertPublicServiceCustomer(providerId, serviceCustomerCode, serviceCustomerName);

        if ("owner".equals(keyPrefix)) {
            upsertPublicServiceBill(
                    providerId,
                    serviceCustomerId,
                    serviceCustomerCode,
                    serviceCustomerName,
                    billingPeriod(OffsetDateTime.now(ZoneOffset.UTC)),
                    servicePendingAmount(seed),
                    "PENDING",
                    LocalDate.now(ZoneOffset.UTC).plusDays(10),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        for (int index = 0; index < transactionIds.size(); index++) {
            int paymentIndex = index + 1;
            UUID transactionId = transactionIds.get(index);
            String billingPeriod = billingPeriod(OffsetDateTime.now(ZoneOffset.UTC).minusMonths(paymentIndex));
            BigDecimal paidAmount = servicePaidAmount(seed, paymentIndex, serviceCustomerCode);
            LocalDate dueDate = LocalDate.now(ZoneOffset.UTC).minusDays(5L + paymentIndex);
            String paymentKeySuffix = keyPrefix + "-" + String.format("%02d", paymentIndex);

            UUID paidBillId = upsertPublicServiceBill(
                    providerId,
                    serviceCustomerId,
                    serviceCustomerCode,
                    serviceCustomerName,
                    billingPeriod,
                    paidAmount,
                    "PAID",
                    dueDate,
                    tenant.id(),
                    tenant.slug(),
                    userId,
                    accountId,
                    accountNumber,
                    transactionId
            );

            upsertPublicServicePayment(
                    paidBillId,
                    providerId,
                    tenant.id(),
                    tenant.slug(),
                    userId,
                    accountId,
                    accountNumber,
                    transactionId,
                    paidAmount,
                    "BOB",
                    seed.slug(),
                    paymentKeySuffix
            );
        }
    }

    private UUID upsertPublicServiceCustomer(UUID providerId, String serviceCustomerCode, String customerName) {
        UUID customerId = deterministicUuid("public:service-customer:" + providerId + ":" + serviceCustomerCode);
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO public.service_customers (
                    id, provider_id, service_customer_code, customer_name, status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, 'ACTIVE', NOW(), NOW())
                ON CONFLICT (provider_id, service_customer_code) DO UPDATE SET
                    customer_name = EXCLUDED.customer_name,
                    status = 'ACTIVE',
                    updated_at = NOW()
                RETURNING id
                """,
                UUID.class,
                customerId,
                providerId,
                serviceCustomerCode,
                customerName
        );
    }

    private UUID upsertPublicServiceBill(
            UUID providerId,
            UUID serviceCustomerId,
            String serviceCustomerCode,
            String customerName,
            String billingPeriod,
            BigDecimal amount,
            String status,
            LocalDate dueDate,
            UUID paidByTenantId,
            String paidByTenantSlug,
            UUID paidByUserId,
            UUID paidByAccountId,
            String paidByAccountNumber,
            UUID paidTransactionId
    ) {
        UUID billId = deterministicUuid("public:service-bill:" + providerId + ":" + serviceCustomerCode + ":" + billingPeriod);
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO public.service_bills (
                    id, provider_id, service_customer_id, service_customer_code, customer_name, billing_period,
                    amount, currency, due_date, status, paid_by_tenant_id, paid_by_tenant_slug, paid_by_user_id,
                    paid_by_account_id, paid_by_account_number, paid_transaction_id, paid_at, created_by_superadmin_id,
                    created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, 'BOB', ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NOW(), NOW())
                ON CONFLICT (provider_id, service_customer_code, billing_period) DO UPDATE SET
                    service_customer_id = EXCLUDED.service_customer_id,
                    customer_name = EXCLUDED.customer_name,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    due_date = EXCLUDED.due_date,
                    status = EXCLUDED.status,
                    paid_by_tenant_id = EXCLUDED.paid_by_tenant_id,
                    paid_by_tenant_slug = EXCLUDED.paid_by_tenant_slug,
                    paid_by_user_id = EXCLUDED.paid_by_user_id,
                    paid_by_account_id = EXCLUDED.paid_by_account_id,
                    paid_by_account_number = EXCLUDED.paid_by_account_number,
                    paid_transaction_id = EXCLUDED.paid_transaction_id,
                    paid_at = EXCLUDED.paid_at,
                    updated_at = NOW()
                RETURNING id
                """,
                UUID.class,
                billId,
                providerId,
                serviceCustomerId,
                serviceCustomerCode,
                customerName,
                billingPeriod,
                amount,
                dueDate,
                status,
                paidByTenantId,
                paidByTenantSlug,
                paidByUserId,
                paidByAccountId,
                paidByAccountNumber,
                paidTransactionId,
                status.equalsIgnoreCase("PAID") ? OffsetDateTime.now(ZoneOffset.UTC) : null
        );
    }

    private void upsertPublicServicePayment(
            UUID billId,
            UUID providerId,
            UUID paidByTenantId,
            String paidByTenantSlug,
            UUID paidByUserId,
            UUID paidByAccountId,
            String paidByAccountNumber,
            UUID paidTransactionId,
            BigDecimal amount,
            String currency,
            String seedSlug,
            String paymentKeySuffix
    ) {
        UUID paymentId = deterministicUuid("public:service-payment:" + billId);
        String idempotencyKey = DEMO_IDEMPOTENCY_PREFIX + ":" + seedSlug + ":service-payment:" + paymentKeySuffix;
        String receiptNumber = "SP-SEED-" + seedSlug.toUpperCase() + "-" + paymentKeySuffix.toUpperCase().replaceAll("[^A-Z0-9]+", "-");

        jdbcTemplate.update(
                """
                INSERT INTO public.service_bill_payments (
                    id, bill_id, provider_id, paid_by_tenant_id, paid_by_tenant_slug, paid_by_user_id,
                    paid_by_account_id, paid_by_account_number, paid_transaction_id, amount, currency,
                    receipt_number, idempotency_key, status, paid_at, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PAID', NOW(), NOW())
                ON CONFLICT (bill_id) DO UPDATE SET
                    provider_id = EXCLUDED.provider_id,
                    paid_by_tenant_id = EXCLUDED.paid_by_tenant_id,
                    paid_by_tenant_slug = EXCLUDED.paid_by_tenant_slug,
                    paid_by_user_id = EXCLUDED.paid_by_user_id,
                    paid_by_account_id = EXCLUDED.paid_by_account_id,
                    paid_by_account_number = EXCLUDED.paid_by_account_number,
                    paid_transaction_id = EXCLUDED.paid_transaction_id,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    receipt_number = EXCLUDED.receipt_number,
                    idempotency_key = EXCLUDED.idempotency_key,
                    status = 'PAID',
                    paid_at = NOW()
                """,
                paymentId,
                billId,
                providerId,
                paidByTenantId,
                paidByTenantSlug,
                paidByUserId,
                paidByAccountId,
                paidByAccountNumber,
                paidTransactionId,
                amount,
                currency,
                receiptNumber,
                idempotencyKey
        );
    }

    private BigDecimal servicePendingAmount(TenantSeed seed) {
        int variant = Math.abs(seed.slug().hashCode() % 7);
        return BigDecimal.valueOf(39L + variant).setScale(2);
    }

    private BigDecimal servicePaidAmount(TenantSeed seed, int paymentIndex, String serviceCustomerCode) {
        int variant = Math.abs((seed.slug() + ":" + serviceCustomerCode + ":" + paymentIndex).hashCode() % 9);
        return BigDecimal.valueOf(24L + paymentIndex + variant).setScale(2);
    }

    private String tenantServiceCustomerCode(TenantSeed seed, int userNumber) {
        return seed.serviceCustomerCode() + "-U" + String.format("%02d", userNumber);
    }

    private String tenantServiceCustomerName(TenantSeed seed, String firstName, int userNumber) {
        return seed.serviceCustomerName() + " - " + firstName + " " + String.format("%02d", userNumber);
    }

    private String tenantServiceAlias(TenantSeed seed, String firstName, int userNumber) {
        return seed.serviceAlias() + " - " + firstName + " " + String.format("%02d", userNumber);
    }

    private String billingPeriod(OffsetDateTime dateTime) {
        return dateTime.getYear() + "-" + String.format("%02d", dateTime.getMonthValue());
    }

    private void ensureAccountSequence(String schemaName, String accountType, String currency, long currentValue) {
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_account_sequences (
                    id, account_type, currency, current_value, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, NOW(), NOW())
                ON CONFLICT (account_type, currency) DO UPDATE SET
                    current_value = GREATEST(tenant_account_sequences.current_value, EXCLUDED.current_value),
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":sequence:" + accountType + ":" + currency),
                accountType,
                currency,
                currentValue
        );
    }

    private UUID fetchRequiredRoleId(String schemaName, String roleName) {
        UUID roleId = jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM %s.tenant_roles
                WHERE name = ?
                LIMIT 1
                """.formatted(schemaName),
                UUID.class,
                roleName
        );

        if (roleId == null) {
            throw new IllegalStateException("Required role '" + roleName + "' was not found in schema '" + schemaName + "'");
        }

        return roleId;
    }

    private String accountNumber(String tenantPrefix, String accountType, String currency, long sequence) {
        return "%s-%s-%s-%06d".formatted(
                tenantPrefix,
                accountShortCode(accountType),
                currency,
                sequence
        );
    }

    private String accountShortCode(String accountType) {
        return switch (accountType) {
            case "WALLET" -> "WAL";
            case "SAVINGS" -> "SAV";
            case "CHECKING" -> "CHK";
            case "CREDIT_CARD" -> "CCD";
            case "PREPAID_CARD" -> "PPD";
            case "LOAN" -> "LOA";
            default -> "ACC";
        };
    }

    private UUID deterministicUuid(String seedValue) {
        return UUID.nameUUIDFromBytes(seedValue.getBytes(StandardCharsets.UTF_8));
    }

    private List<TenantSeed> sampleTenants() {
        return List.of(
                new TenantSeed("Acme Finance", "acme-finance", "ENTERPRISE", "ariana@gmail.com", "Ariana", "Lopez", "bruno@gmail.com", "Bruno", "Diaz", "ACME", "ELECTRICITY_CRE", "12345678", "Casa Central", "Luz del hogar", new BigDecimal("1200.00"), new BigDecimal("420.00")),
                new TenantSeed("Nova Wallet", "nova-wallet", "ENTERPRISE", "carla@gmail.com", "Carla", "Perez", "elena@gmail.com", "Elena", "Gutierrez", "NOVA", "INTERNET_ENTEL", "NOVA-778899", "Internet Hogar", "Internet principal", new BigDecimal("980.00"), new BigDecimal("305.00")),
                new TenantSeed("Pampa Pay", "pampa-pay", "ENTERPRISE", "lucia@gmail.com", "Lucia", "Mendoza", "felipe@gmail.com", "Felipe", "Rojas", "PAMPA", "WATER_SAGUAPAC", "PAMPA-1001", "Casa Centro", "Agua familiar", new BigDecimal("1500.00"), new BigDecimal("260.00")),
                new TenantSeed("Tumi Bank", "tumi-bank", "ENTERPRISE", "jose@gmail.com", "Jose", "Vargas", "gabriela@gmail.com", "Gabriela", "Sosa", "TUMI", "TV_TIGO", "TUMI-2222", "Hogar Norte", "TV cable", new BigDecimal("2100.00"), new BigDecimal("510.00")),
                new TenantSeed("Luna Cash", "luna-cash", "ENTERPRISE", "maria@gmail.com", "Maria", "Fernandez", "hector@gmail.com", "Hector", "Lima", "LUNA", "ELECTRICITY_CRE", "LUNA-7788", "Residencial Sur", "Energia luz", new BigDecimal("800.00"), new BigDecimal("190.00")),
                new TenantSeed("Andes Wallet", "andes-wallet", "ENTERPRISE", "pedro@gmail.com", "Pedro", "Quispe", "irene@gmail.com", "Irene", "Valdez", "ANDES", "INTERNET_ENTEL", "ANDES-9900", "Oficina Central", "Internet oficina", new BigDecimal("1750.00"), new BigDecimal("380.00")),
                new TenantSeed("Suma Finance", "suma-finance", "ENTERPRISE", "sofia@gmail.com", "Sofia", "Rojas", "javier@gmail.com", "Javier", "Mora", "SUMA", "WATER_SAGUAPAC", "SUMA-4455", "Barrio Este", "Servicio agua", new BigDecimal("1325.00"), new BigDecimal("275.00")),
                new TenantSeed("Kawi Pay", "kawi-pay", "ENTERPRISE", "diego@gmail.com", "Diego", "Salazar", "karen@gmail.com", "Karen", "Nunez", "KAWI", "TV_TIGO", "KAWI-3003", "Ciudad Jardín", "Tv familiar", new BigDecimal("990.00"), new BigDecimal("215.00")),
                new TenantSeed("Runa Cash", "runa-cash", "ENTERPRISE", "ana@gmail.com", "Ana", "Torrez", "luis@gmail.com", "Luis", "Paredes", "RUNA", "ELECTRICITY_CRE", "RUNA-5566", "Centro", "Luz casa", new BigDecimal("2450.00"), new BigDecimal("610.00")),
                new TenantSeed("Pacha Wallet", "pacha-wallet", "ENTERPRISE", "camila@gmail.com", "Camila", "Flores", "marta@gmail.com", "Marta", "Campos", "PACHA", "INTERNET_ENTEL", "PACHA-9090", "Zona Norte", "Internet hogar", new BigDecimal("1110.00"), new BigDecimal("330.00"))
        );
    }

    private record TenantSeed(
            String name,
            String slug,
            String planCode,
            String ownerEmail,
            String ownerFirstName,
            String ownerLastName,
            String userEmail,
            String userFirstName,
            String userLastName,
            String accountPrefix,
            String serviceProviderCode,
            String serviceCustomerCode,
            String serviceCustomerName,
            String serviceAlias,
            BigDecimal ownerStartingBalance,
            BigDecimal savingsStartingBalance
    ) {
    }
}

```

### `tenant/TenantBootstrapService.java`

```java
package com.financesystem.finance_api.bootstrap.tenant;

import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class TenantBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(TenantBootstrapService.class);
    private static final List<String> DEFAULT_OWNER_ADMIN_PERMISSION_CODES = List.of(
            "me.accounts.create",
            "me.accounts.list",
            "me.accounts.view",
            "me.accounts.balance.read",
            "me.accounts.update.alias",
            "me.accounts.transactions.read",
            "me.transactions.read",
            "me.transactions.detail",
            "me.transactions.transfer",
            "me.transactions.deposit",
            "me.transactions.withdrawal",
            "me.transactions.payment",
            "me.transactions.hold",
            "me.transactions.release",
            "me.transactions.qr.confirm",

            "accounts.create",
            "accounts.list",
            "accounts.view",
            "accounts.balance.read",
            "accounts.update",
            "accounts.approve",
            "accounts.activate",
            "accounts.block",
            "accounts.freeze",
            "accounts.close",
            "accounts.transactions.read",

            "access.permissions.read",
            "access.roles.read",
            "access.roles.create",
            "access.roles.detail",
            "access.roles.update",
            "access.roles.activate",
            "access.roles.deactivate",
            "access.users.roles.read",
            "access.users.roles.assign",

            "users.list",
            "users.create",
            "users.detail",
            "users.update",
            "users.activate",
            "users.deactivate",

            "transactions.read",
            "transactions.detail",
            "transactions.create.transfer",
            "transactions.create.deposit",
            "transactions.create.withdrawal",
            "transactions.create.payment",
            "transactions.reverse",
            "transactions.refund",
            "transactions.fee",
            "transactions.hold",
            "transactions.release",
            "transactions.adjust",
            "transactions.admin.read",
            "transactions.admin.export",
            "transactions.qr.create",
            "transactions.qr.confirm",

            "limits.read",
            "limits.detail",
            "limits.create",
            "limits.update",
            "limits.delete",
            "limits.evaluate",

            "accounting.journal.read",
            "accounting.journal.detail",
            "accounting.periods.read",
            "accounting.periods.create",
            "accounting.periods.close",

            "audit.events.read",
            "notifications.templates.read",
            "notifications.templates.detail",
            "notifications.deliveries.read",

            "reports.analytic.read",
            "reports.managerial.read",
            "reports.analytic.run",
            "reports.managerial.run",
            "reports.export",
            "reports.executions.read",
            "reports.executions.rerun",

            "reports.tenant.read",
            "reports.tenant.run",
            "reports.tenant.export",
            "reports.tenant.ai",
            "reports.tenant.history",

            "fx.rates.read",
            "fx.rates.detail",
            "fx.rates.create",
            "fx.rates.update",
            "fx.rates.delete",
            "fx.fees.read",
            "fx.fees.detail",
            "fx.fees.create",
            "fx.fees.update",
            "fx.fees.delete",

            "backups.create",
            "backups.list",
            "backups.detail",
            "backups.download",
            "backups.restore",

            "service-providers.read",
            "service-bills.query",
            "service-payments.create",
            "service-payments.read",
            "service-payments.detail",
            "service-enrollments.read"
    );

    private static final List<String> DEFAULT_ADMIN_PERMISSION_CODES = List.of(
            "me.accounts.create",
            "me.accounts.list",
            "me.accounts.view",
            "me.accounts.balance.read",
            "me.accounts.update.alias",
            "me.accounts.transactions.read",
            "me.transactions.read",
            "me.transactions.detail",
            "me.transactions.transfer",
            "me.transactions.deposit",
            "me.transactions.withdrawal",
            "me.transactions.payment",
            "me.transactions.hold",
            "me.transactions.release",
            "me.transactions.qr.confirm",

            "accounts.list",
            "accounts.view",
            "accounts.balance.read",
            "accounts.approve",
            "accounts.activate",
            "accounts.block",
            "accounts.freeze",
            "accounts.close",
            "accounts.transactions.read",

            "users.list",
            "users.create",
            "users.detail",
            "users.update",
            "users.activate",
            "users.deactivate",

            "transactions.read",
            "transactions.detail",
            "transactions.create.transfer",
            "transactions.create.deposit",
            "transactions.create.withdrawal",
            "transactions.create.payment",
            "transactions.reverse",
            "transactions.refund",
            "transactions.fee",
            "transactions.hold",
            "transactions.release",
            "transactions.adjust",
            "transactions.admin.read",
            "transactions.admin.export",
            "transactions.qr.create",
            "transactions.qr.confirm",

            "limits.read",
            "limits.detail",
            "limits.evaluate",

            "accounting.journal.read",
            "accounting.journal.detail",
            "accounting.periods.read",

            "audit.events.read",
            "notifications.templates.read",
            "notifications.templates.detail",
            "notifications.deliveries.read",

            "reports.analytic.read",
            "reports.managerial.read",
            "reports.analytic.run",
            "reports.managerial.run",
            "reports.export",
            "reports.executions.read",
            "reports.executions.rerun",

            "reports.tenant.read",
            "reports.tenant.run",
            "reports.tenant.export",
            "reports.tenant.ai",
            "reports.tenant.history",

            "fx.rates.read",
            "fx.rates.detail",
            "fx.fees.read",
            "fx.fees.detail",

            "backups.create",
            "backups.list",
            "backups.detail",
            "backups.download",
            "backups.restore",

            "service-providers.read",
            "service-bills.query",
            "service-payments.create",
            "service-payments.read",
            "service-payments.detail",
            "service-enrollments.read"
    );
    private static final List<String> DEFAULT_CLIENT_PERMISSION_CODES = List.of(
            "me.accounts.create",
            "me.accounts.list",
            "me.accounts.view",
            "me.accounts.balance.read",
            "me.accounts.update.alias",
            "me.accounts.transactions.read",
            "me.transactions.read",
            "me.transactions.detail",
            "me.transactions.transfer",
            "me.transactions.deposit",
            "me.transactions.withdrawal",
            "me.transactions.payment",
            "me.transactions.hold",
            "me.transactions.release",
            "me.transactions.qr.create",
            "me.transactions.qr.read",
            "me.transactions.qr.cancel",
            "me.transactions.qr.confirm",

            "me.service-providers.read",
            "me.service-enrollments.read",
            "me.service-enrollments.create",
            "me.service-enrollments.update",
            "me.service-enrollments.delete",
            "me.service-bills.query",
            "me.service-payments.create",
            "me.service-payments.read",
            "me.service-payments.detail"
    );
    private static final List<FxRateSeed> DEFAULT_FX_RATES = List.of(
            new FxRateSeed("BOB", "BOB", new java.math.BigDecimal("1.00000000"), "Base currency to itself"),
            new FxRateSeed("USD", "USD", new java.math.BigDecimal("1.00000000"), "Base currency to itself"),
            new FxRateSeed("EUR", "EUR", new java.math.BigDecimal("1.00000000"), "Base currency to itself"),
            new FxRateSeed("USDT", "USDT", new java.math.BigDecimal("1.00000000"), "Base currency to itself"),
            new FxRateSeed("BOB", "USD", new java.math.BigDecimal("0.14500000"), "Default BOB to USD rate"),
            new FxRateSeed("USD", "BOB", new java.math.BigDecimal("6.90000000"), "Default USD to BOB rate"),
            new FxRateSeed("BOB", "EUR", new java.math.BigDecimal("0.13300000"), "Default BOB to EUR rate"),
            new FxRateSeed("EUR", "BOB", new java.math.BigDecimal("7.51879699"), "Default EUR to BOB rate"),
            new FxRateSeed("BOB", "USDT", new java.math.BigDecimal("0.14500000"), "Default BOB to USDT rate"),
            new FxRateSeed("USDT", "BOB", new java.math.BigDecimal("6.90000000"), "Default USDT to BOB rate"),
            new FxRateSeed("USD", "EUR", new java.math.BigDecimal("0.92000000"), "Default USD to EUR rate"),
            new FxRateSeed("EUR", "USD", new java.math.BigDecimal("1.08695652"), "Default EUR to USD rate"),
            new FxRateSeed("USD", "USDT", new java.math.BigDecimal("1.00000000"), "Default USD to USDT rate"),
            new FxRateSeed("USDT", "USD", new java.math.BigDecimal("1.00000000"), "Default USDT to USD rate"),
            new FxRateSeed("EUR", "USDT", new java.math.BigDecimal("1.08695652"), "Default EUR to USDT rate"),
            new FxRateSeed("USDT", "EUR", new java.math.BigDecimal("0.92000000"), "Default USDT to EUR rate")
    );
    private static final List<OperationFeeSeed> DEFAULT_OPERATION_FEES = List.of(
            new OperationFeeSeed(FxOperationCode.TRANSFER, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default transfer fee"),
            new OperationFeeSeed(FxOperationCode.CONVERSION, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default conversion fee"),
            new OperationFeeSeed(FxOperationCode.DEPOSIT, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default deposit fee"),
            new OperationFeeSeed(FxOperationCode.WITHDRAWAL, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default withdrawal fee"),
            new OperationFeeSeed(FxOperationCode.PAYMENT, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default payment fee")
    );

    private final JdbcTemplate jdbcTemplate;

    public TenantBootstrapService(@Qualifier("targetDataSource") DataSource targetDataSource) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
    }

    public void initializeTenantData(String schemaName, String tenantName) {
        validateSchemaName(schemaName);

        logger.info("Initializing tenant bootstrap data for schema '{}'.", schemaName);

        seedDefaultRoles(schemaName);
        seedDefaultRolePermissions(schemaName, "OWNER_ADMIN", DEFAULT_OWNER_ADMIN_PERMISSION_CODES);
        seedDefaultRolePermissions(schemaName, "ADMIN", DEFAULT_ADMIN_PERMISSION_CODES);
        seedDefaultRolePermissions(schemaName, "USER", DEFAULT_CLIENT_PERMISSION_CODES);
        seedDefaultSettings(schemaName, tenantName);
        seedDefaultFxConfiguration(schemaName);

        logger.info("Tenant bootstrap data initialized successfully for schema '{}'.", schemaName);
    }

    private void seedDefaultRoles(String schemaName) {
        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('OWNER_ADMIN', 'Tenant owner administrator role', true, NOW())
                ON CONFLICT (name) DO UPDATE SET
                    active = true,
                    description = EXCLUDED.description
                """.formatted(schemaName));

        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('ADMIN', 'Default tenant administrator role', true, NOW())
                ON CONFLICT (name) DO UPDATE SET
                    active = true,
                    description = EXCLUDED.description
                """.formatted(schemaName));

        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('USER', 'Default tenant user role', true, NOW())
                ON CONFLICT (name) DO UPDATE SET
                    active = true,
                    description = EXCLUDED.description
                """.formatted(schemaName));
    }

    private void seedDefaultRolePermissions(String schemaName, String roleName, List<String> permissionCodes) {
        for (String permissionCode : permissionCodes) {
            Integer permissionCount = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*)
                    FROM public.system_permissions
                    WHERE code = ? AND active = true
                    """,
                    Integer.class,
                    permissionCode
            );

            if (permissionCount == null || permissionCount == 0) {
                throw new IllegalStateException(
                        "Default permission '" + permissionCode + "' is not available in public.system_permissions"
                );
            }

            jdbcTemplate.update(
                    """
                    INSERT INTO %s.tenant_role_permissions (role_id, permission_code, assigned_at)
                    SELECT r.id, ?, NOW()
                    FROM %s.tenant_roles r
                    JOIN public.system_permissions sp
                        ON sp.code = ? AND sp.active = true
                    WHERE r.name = ?
                    ON CONFLICT (role_id, permission_code) DO NOTHING
                    """.formatted(schemaName, schemaName),
                    permissionCode,
                    permissionCode,
                    roleName
            );
        }
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

    private void seedDefaultFxConfiguration(String schemaName) {
        validateFxSeeds();
        assertFxTablesExist(schemaName);

        for (FxRateSeed rate : DEFAULT_FX_RATES) {
            jdbcTemplate.update(
                    """
                    INSERT INTO %s.tenant_fx_exchange_rates (
                        source_currency, target_currency, rate, active, description, created_at, updated_at
                    )
                    VALUES (?, ?, ?, true, ?, NOW(), NOW())
                    ON CONFLICT ON CONSTRAINT uq_tenant_fx_exchange_rates_pair DO UPDATE SET
                        rate = EXCLUDED.rate,
                        active = true,
                        description = EXCLUDED.description,
                        updated_at = NOW()
                    """.formatted(schemaName),
                    rate.sourceCurrency(),
                    rate.targetCurrency(),
                    rate.rate(),
                    rate.description()
            );
        }

        for (OperationFeeSeed fee : DEFAULT_OPERATION_FEES) {
            jdbcTemplate.update(
                    """
                    INSERT INTO %s.tenant_operation_fees (
                        operation_code, fee_type, fee_value, calculation_mode, active, description, created_at, updated_at
                    )
                    VALUES (?, ?, ?, ?, true, ?, NOW(), NOW())
                    ON CONFLICT ON CONSTRAINT uq_tenant_operation_fees_operation_code DO UPDATE SET
                        fee_type = EXCLUDED.fee_type,
                        fee_value = EXCLUDED.fee_value,
                        calculation_mode = EXCLUDED.calculation_mode,
                        active = true,
                        description = EXCLUDED.description,
                        updated_at = NOW()
                    """.formatted(schemaName),
                    fee.operationCode().name(),
                    fee.feeType(),
                    fee.feeValue(),
                    fee.calculationMode(),
                    fee.description()
            );
        }
    }

    private void assertFxTablesExist(String schemaName) {
        assertTableExists(schemaName, "tenant_fx_exchange_rates");
        assertTableExists(schemaName, "tenant_operation_fees");
    }

    private void assertTableExists(String schemaName, String tableName) {
        Integer tableCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = ? AND table_name = ?
                """,
                Integer.class,
                schemaName,
                tableName
        );

        if (tableCount == null || tableCount == 0) {
            throw new IllegalStateException(
                    "Required tenant table '" + schemaName + "." + tableName + "' does not exist. " +
                            "Tenant schema migration may not have completed successfully."
            );
        }
    }

    private void validateSchemaName(String schemaName) {
        if (schemaName == null || schemaName.isBlank()) {
            throw new IllegalArgumentException("Schema name must not be blank");
        }

        if (!schemaName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Schema name contains invalid characters: " + schemaName);
        }
    }

    private void validateFxSeeds() {
        Set<String> ratePairs = new HashSet<>();
        for (FxRateSeed rate : DEFAULT_FX_RATES) {
            validateCurrencyCode(rate.sourceCurrency(), "source currency");
            validateCurrencyCode(rate.targetCurrency(), "target currency");

            if (rate.sourceCurrency().equals(rate.targetCurrency()) &&
                    rate.rate().compareTo(new java.math.BigDecimal("1.00000000")) != 0) {
                throw new IllegalStateException("Self exchange rates must be 1.00000000");
            }

            String pairKey = rate.sourceCurrency() + "->" + rate.targetCurrency();
            if (!ratePairs.add(pairKey)) {
                throw new IllegalStateException("Duplicate FX rate seed for pair " + pairKey);
            }
        }

        Set<String> feeCodes = new HashSet<>();
        for (OperationFeeSeed fee : DEFAULT_OPERATION_FEES) {
            if (fee.operationCode() == null) {
                throw new IllegalStateException("FX fee operation code must not be null");
            }

            if (!feeCodes.add(fee.operationCode().name())) {
                throw new IllegalStateException("Duplicate FX fee seed for operation " + fee.operationCode());
            }
        }
    }

    private void validateCurrencyCode(String currency, String fieldName) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalStateException("FX seed " + fieldName + " must not be blank");
        }

        try {
            com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode.valueOf(currency);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Unsupported FX seed currency: " + currency);
        }
    }

    private record FxRateSeed(
            String sourceCurrency,
            String targetCurrency,
            java.math.BigDecimal rate,
            String description
    ) {
    }

    private record OperationFeeSeed(
            FxOperationCode operationCode,
            String feeType,
            java.math.BigDecimal feeValue,
            String calculationMode,
            String description
    ) {
    }

}

```

### `tenant/TenantContextTestController.java`

```java
package com.financesystem.finance_api.bootstrap.tenant;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/tenant")
public class TenantContextTestController {

    @GetMapping("/context")
    public ApiResponse<Map<String, Object>> currentTenantContext() {
        TenantContext tenantContext = TenantContextHolder.getRequired();

        return ApiResponse.success(
                "Tenant context resolved successfully",
                Map.of(
                        "tenantSlug", tenantContext.tenantSlug(),
                        "schemaName", tenantContext.schemaName(),
                        "publicRequest", tenantContext.publicRequest()
                )
        );
    }
}
```

