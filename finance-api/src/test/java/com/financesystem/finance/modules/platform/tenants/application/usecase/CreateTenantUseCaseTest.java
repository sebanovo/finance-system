package com.financesystem.finance.modules.platform.tenants.application.usecase;

import com.financesystem.finance.bootstrap.tenant.TenantBootstrapService;
import com.financesystem.finance.common.tenancy.migration.TenantSchemaMigrationService;
import com.financesystem.finance.common.tenancy.schema.TenantSchemaNamingStrategy;
import com.financesystem.finance.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance.modules.platform.tenants.application.dto.CreateTenantRequest;
import com.financesystem.finance.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance.modules.platform.tenants.application.mapper.PlatformTenantMapper;
import com.financesystem.finance.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance.modules.platform.tenants.domain.model.PlatformTenantStatus;
import com.financesystem.finance.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import com.financesystem.finance.modules.platform.tenants.infrastructure.persistence.PlatformPlanLookupService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CreateTenantUseCaseTest {

    @Test
    void shouldCreateTenantAndRunBootstrapSuccessfully() {
        PlatformTenantRepository tenantRepository = mock(PlatformTenantRepository.class);
        PlatformPlanLookupService planLookupService = mock(PlatformPlanLookupService.class);
        TenantSchemaNamingStrategy namingStrategy = mock(TenantSchemaNamingStrategy.class);
        TenantSchemaMigrationService migrationService = mock(TenantSchemaMigrationService.class);
        TenantBootstrapService bootstrapService = mock(TenantBootstrapService.class);
        PlatformTenantMapper mapper = mock(PlatformTenantMapper.class);
        AuditTrailService auditTrailService = mock(AuditTrailService.class);

        CreateTenantUseCase useCase = new CreateTenantUseCase(
                tenantRepository,
                planLookupService,
                namingStrategy,
                migrationService,
                bootstrapService,
                mapper,
                auditTrailService
        );

        CreateTenantRequest request = new CreateTenantRequest("FinanCruz Ltda", "financruz", "BASIC");

        UUID planId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        PlatformTenant createdTenant = new PlatformTenant(
                tenantId,
                "FinanCruz Ltda",
                "financruz",
                "tenant_financruz",
                PlatformTenantStatus.ACTIVE,
                planId,
                true,
                Instant.now(),
                Instant.now()
        );

        PlatformTenantResponse expectedResponse = new PlatformTenantResponse(
                tenantId,
                "FinanCruz Ltda",
                "financruz",
                "tenant_financruz",
                "ACTIVE",
                planId,
                true,
                createdTenant.createdAt(),
                createdTenant.updatedAt()
        );

        when(namingStrategy.normalizeSlug("financruz")).thenReturn("financruz");
        when(namingStrategy.toSchemaName("financruz")).thenReturn("tenant_financruz");
        when(tenantRepository.existsBySlug("financruz")).thenReturn(false);
        when(tenantRepository.existsBySchemaName("tenant_financruz")).thenReturn(false);
        when(planLookupService.findPlanIdByCodeOrDefault("BASIC")).thenReturn(planId);
        when(tenantRepository.save(any())).thenReturn(createdTenant);
        when(mapper.toResponse(createdTenant)).thenReturn(expectedResponse);

        PlatformTenantResponse actualResponse = useCase.execute(request);

        assertEquals(expectedResponse, actualResponse);
        verify(migrationService).migrateSchema("tenant_financruz");
        verify(bootstrapService).initializeTenantData("tenant_financruz", "FinanCruz Ltda");
        verify(auditTrailService).recordPlatformEvent(anyString(), eq("TENANT"), eq(tenantId.toString()), any());
    }
}