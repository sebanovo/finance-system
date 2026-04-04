package com.financesystem.finance.modules.platform.tenants.application.usecase;

import com.financesystem.finance.bootstrap.tenant.TenantBootstrapService;
import com.financesystem.finance.common.tenancy.migration.TenantSchemaMigrationService;
import com.financesystem.finance.common.tenancy.schema.TenantSchemaNamingStrategy;
import com.financesystem.finance.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance.modules.platform.tenants.application.dto.CreateTenantRequest;
import com.financesystem.finance.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance.modules.platform.tenants.application.mapper.PlatformTenantMapper;
import com.financesystem.finance.modules.platform.tenants.domain.exception.PlatformTenantAlreadyExistsException;
import com.financesystem.finance.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance.modules.platform.tenants.domain.model.PlatformTenantStatus;
import com.financesystem.finance.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import com.financesystem.finance.modules.platform.tenants.infrastructure.persistence.PlatformPlanLookupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CreateTenantUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformPlanLookupService platformPlanLookupService;
    private final TenantSchemaNamingStrategy tenantSchemaNamingStrategy;
    private final TenantSchemaMigrationService tenantSchemaMigrationService;
    private final TenantBootstrapService tenantBootstrapService;
    private final PlatformTenantMapper platformTenantMapper;
    private final AuditTrailService auditTrailService;

    public CreateTenantUseCase(
            PlatformTenantRepository platformTenantRepository,
            PlatformPlanLookupService platformPlanLookupService,
            TenantSchemaNamingStrategy tenantSchemaNamingStrategy,
            TenantSchemaMigrationService tenantSchemaMigrationService,
            TenantBootstrapService tenantBootstrapService,
            PlatformTenantMapper platformTenantMapper,
            AuditTrailService auditTrailService
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformPlanLookupService = platformPlanLookupService;
        this.tenantSchemaNamingStrategy = tenantSchemaNamingStrategy;
        this.tenantSchemaMigrationService = tenantSchemaMigrationService;
        this.tenantBootstrapService = tenantBootstrapService;
        this.platformTenantMapper = platformTenantMapper;
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

        var planId = platformPlanLookupService.findPlanIdByCodeOrDefault(request.planCode());

        PlatformTenant tenantToCreate = new PlatformTenant(
                null,
                request.name().trim(),
                normalizedSlug,
                schemaName,
                PlatformTenantStatus.ACTIVE,
                planId,
                true,
                null,
                null
        );

        PlatformTenant createdTenant = platformTenantRepository.save(tenantToCreate);

        tenantSchemaMigrationService.migrateSchema(createdTenant.schemaName());
        tenantBootstrapService.initializeTenantData(createdTenant.schemaName(), createdTenant.name());

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.TENANT_CREATED,
                "TENANT",
                createdTenant.id().toString(),
                Map.of(
                        "name", createdTenant.name(),
                        "slug", createdTenant.slug(),
                        "schemaName", createdTenant.schemaName(),
                        "planId", String.valueOf(createdTenant.planId())
                )
        );

        return platformTenantMapper.toResponse(createdTenant);
    }
}