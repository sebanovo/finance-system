package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance_api.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance_api.modules.identity.access.domain.exception.TenantRoleNotFoundException;
import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class DeactivateTenantRoleUseCase {

    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePermissionRepository tenantRolePermissionRepository;
    private final TenantRoleMapper tenantRoleMapper;
    private final AuditTrailService auditTrailService;

    public DeactivateTenantRoleUseCase(
            TenantRoleRepository tenantRoleRepository,
            TenantRolePermissionRepository tenantRolePermissionRepository,
            TenantRoleMapper tenantRoleMapper,
            AuditTrailService auditTrailService
    ) {
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantRolePermissionRepository = tenantRolePermissionRepository;
        this.tenantRoleMapper = tenantRoleMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public TenantRoleResponse execute(UUID id) {
        TenantRole existingRole = tenantRoleRepository.findById(id)
                .orElseThrow(() -> new TenantRoleNotFoundException("Tenant role not found with id: " + id));

        TenantRole updatedRole = new TenantRole(
                existingRole.id(),
                existingRole.name(),
                existingRole.description(),
                false,
                existingRole.createdAt()
        );

        TenantRole savedRole = tenantRoleRepository.save(updatedRole);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ROLE_DEACTIVATED,
                "ROLE",
                savedRole.id().toString(),
                Map.of("name", savedRole.name())
        );

        return tenantRoleMapper.toResponse(
                savedRole,
                tenantRolePermissionRepository.findPermissionCodesByRoleId(savedRole.id()).stream().toList()
        );
    }
}