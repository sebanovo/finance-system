package com.financesystem.finance.modules.platform.tenants.application.usecase;

import com.financesystem.finance.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance.modules.platform.tenants.application.mapper.PlatformTenantMapper;
import com.financesystem.finance.modules.platform.tenants.domain.exception.PlatformTenantNotFoundException;
import com.financesystem.finance.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance.modules.platform.tenants.domain.model.PlatformTenantStatus;
import com.financesystem.finance.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeactivateTenantUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformTenantMapper platformTenantMapper;

    public DeactivateTenantUseCase(
            PlatformTenantRepository platformTenantRepository,
            PlatformTenantMapper platformTenantMapper
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformTenantMapper = platformTenantMapper;
    }

    @Transactional
    public PlatformTenantResponse execute(UUID id) {
        PlatformTenant tenant = platformTenantRepository.findById(id)
                .orElseThrow(() -> new PlatformTenantNotFoundException("Tenant not found with id: " + id));

        PlatformTenant updated = new PlatformTenant(
                tenant.id(),
                tenant.name(),
                tenant.slug(),
                tenant.schemaName(),
                PlatformTenantStatus.INACTIVE,
                tenant.planId(),
                false,
                tenant.createdAt(),
                tenant.updatedAt()
        );

        PlatformTenant saved = platformTenantRepository.save(updated);
        return platformTenantMapper.toResponse(saved);
    }
}