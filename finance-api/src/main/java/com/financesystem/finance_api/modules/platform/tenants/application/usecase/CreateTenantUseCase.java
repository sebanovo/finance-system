package com.financesystem.finance_api.modules.platform.tenants.application.usecase;

import com.financesystem.finance_api.bootstrap.tenant.TenantBootstrapService;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.common.tenancy.migration.TenantSchemaMigrationService;
import com.financesystem.finance_api.common.tenancy.schema.TenantSchemaNamingStrategy;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
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
    private final AuditTrailService auditTrailService;

    public CreateTenantUseCase(
            PlatformTenantRepository platformTenantRepository,
            TenantSchemaNamingStrategy tenantSchemaNamingStrategy,
            TenantSchemaMigrationService tenantSchemaMigrationService,
            TenantBootstrapService tenantBootstrapService,
            PlatformTenantMapper platformTenantMapper,
            PlatformSubscriptionProvisioningService platformSubscriptionProvisioningService,
            AuditTrailService auditTrailService
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.tenantSchemaNamingStrategy = tenantSchemaNamingStrategy;
        this.tenantSchemaMigrationService = tenantSchemaMigrationService;
        this.tenantBootstrapService = tenantBootstrapService;
        this.platformTenantMapper = platformTenantMapper;
        this.platformSubscriptionProvisioningService = platformSubscriptionProvisioningService;
        this.auditTrailService = auditTrailService;
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

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.TENANT_CREATED,
                "TENANT",
                updatedTenant.id().toString(),
                PlatformAuditPayloads.details(
                        "name", updatedTenant.name(),
                        "slug", updatedTenant.slug(),
                        "schemaName", updatedTenant.schemaName(),
                        "planCode", request.planCode()
                ),
                null,
                PlatformAuditPayloads.tenantState(updatedTenant)
        );

        return platformTenantMapper.toResponse(updatedTenant);
    }
}
