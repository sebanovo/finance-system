package com.financesystem.finance_api.bootstrap.platform;

import com.financesystem.finance_api.common.tenancy.migration.TenantSchemaMigrationService;
import com.financesystem.finance_api.common.tenancy.reporting.ReportingSecurityService;
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

    public PlatformBootstrapRunner(
            PlatformBootstrapService platformBootstrapService,
            TenantSchemaMigrationService tenantSchemaMigrationService,
            PlatformSubscriptionLifecycleService platformSubscriptionLifecycleService,
            ReportingSecurityService reportingSecurityService
    ) {
        this.platformBootstrapService = platformBootstrapService;
        this.tenantSchemaMigrationService = tenantSchemaMigrationService;
        this.platformSubscriptionLifecycleService = platformSubscriptionLifecycleService;
        this.reportingSecurityService = reportingSecurityService;
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
