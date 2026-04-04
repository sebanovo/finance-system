package com.financesystem.finance.bootstrap.platform;

import com.financesystem.finance.common.tenancy.migration.TenantSchemaMigrationService;
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

    public PlatformBootstrapRunner(
            PlatformBootstrapService platformBootstrapService,
            TenantSchemaMigrationService tenantSchemaMigrationService
    ) {
        this.platformBootstrapService = platformBootstrapService;
        this.tenantSchemaMigrationService = tenantSchemaMigrationService;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Starting platform bootstrap runner...");

        platformBootstrapService.seedBasePlans();
        platformBootstrapService.seedBaseSystemPermissions();
        tenantSchemaMigrationService.migrateRegisteredTenantSchemas();

        logger.info("Platform bootstrap runner completed successfully.");
    }
}