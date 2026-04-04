package com.financesystem.finance.modules.identity.access.application.usecase;

import com.financesystem.finance.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance.modules.identity.access.domain.exception.TenantRoleNotFoundException;
import com.financesystem.finance.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance.modules.identity.access.domain.repository.TenantRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActivateTenantRoleUseCase {

    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePermissionRepository tenantRolePermissionRepository;
    private final TenantRoleMapper tenantRoleMapper;

    public ActivateTenantRoleUseCase(
            TenantRoleRepository tenantRoleRepository,
            TenantRolePermissionRepository tenantRolePermissionRepository,
            TenantRoleMapper tenantRoleMapper
    ) {
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantRolePermissionRepository = tenantRolePermissionRepository;
        this.tenantRoleMapper = tenantRoleMapper;
    }

    @Transactional
    public TenantRoleResponse execute(UUID id) {
        TenantRole existingRole = tenantRoleRepository.findById(id)
                .orElseThrow(() -> new TenantRoleNotFoundException("Tenant role not found with id: " + id));

        TenantRole updatedRole = new TenantRole(
                existingRole.id(),
                existingRole.name(),
                existingRole.description(),
                true,
                existingRole.createdAt()
        );

        TenantRole savedRole = tenantRoleRepository.save(updatedRole);

        return tenantRoleMapper.toResponse(
                savedRole,
                tenantRolePermissionRepository.findPermissionCodesByRoleId(savedRole.id()).stream().toList()
        );
    }
}