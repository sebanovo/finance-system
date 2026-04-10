package com.financesystem.finance_api.modules.platform.tenants.application.usecase;

import com.financesystem.finance_api.bootstrap.tenant.TenantBootstrapService;
import com.financesystem.finance_api.common.tenancy.migration.TenantSchemaMigrationService;
import com.financesystem.finance_api.common.tenancy.schema.TenantSchemaNamingStrategy;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionProvisioningService;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.CreateTenantRequest;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.mapper.PlatformTenantMapper;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantAlreadyExistsException;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenantStatus;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTenantUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final TenantSchemaNamingStrategy tenantSchemaNamingStrategy;
    private final TenantSchemaMigrationService tenantSchemaMigrationService;
    private final TenantBootstrapService tenantBootstrapService;
    private final PlatformTenantMapper platformTenantMapper;
    private final PlatformSubscriptionProvisioningService platformSubscriptionProvisioningService;

    public CreateTenantUseCase(
            PlatformTenantRepository platformTenantRepository,
            TenantSchemaNamingStrategy tenantSchemaNamingStrategy,
            TenantSchemaMigrationService tenantSchemaMigrationService,
            TenantBootstrapService tenantBootstrapService,
            PlatformTenantMapper platformTenantMapper,
            PlatformSubscriptionProvisioningService platformSubscriptionProvisioningService
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.tenantSchemaNamingStrategy = tenantSchemaNamingStrategy;
        this.tenantSchemaMigrationService = tenantSchemaMigrationService;
        this.tenantBootstrapService = tenantBootstrapService;
        this.platformTenantMapper = platformTenantMapper;
        this.platformSubscriptionProvisioningService = platformSubscriptionProvisioningService;
    }

    @Transactional
    public PlatformTenantResponse execute(CreateTenantRequest request) {
        String normalizedSlug = tenantSchemaNamingStrategy.normalizeSlug(request.slug());
        String schemaName = tenantSchemaNamingStrategy.toSchemaName(normalizedSlug);

        if (platformTenantRepository.existsBySlug(normalizedSlug)) {
            throw new PlatformTenantAlreadyExistsException("A tenant with slug '" + normalizedSlug + "' already exists");
        }

        if (platformTenantRepository.existsBySchemaName(schemaName)) {
            throw new PlatformTenantAlreadyExistsException("A tenant with schema '" + schemaName + "' already exists");
        }

        PlatformTenant tenantToCreate = new PlatformTenant(
                null,
                request.name().trim(),
                normalizedSlug,
                schemaName,
                PlatformTenantStatus.ACTIVE,
                null,
                true,
                null,
                null
        );

        PlatformTenant createdTenant = platformTenantRepository.save(tenantToCreate);

        tenantSchemaMigrationService.migrateSchema(createdTenant.schemaName());
        tenantBootstrapService.initializeTenantData(createdTenant.schemaName(), createdTenant.name());

        platformSubscriptionProvisioningService.assignCurrentSubscription(
                createdTenant.id(),
                request.planCode(),
                null,
                true
        );

        PlatformTenant updatedTenant = platformTenantRepository.findById(createdTenant.id()).orElseThrow();
        return platformTenantMapper.toResponse(updatedTenant);
    }
}
