package com.financesystem.finance.modules.identity.access.application.usecase;

import com.financesystem.finance.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance.modules.identity.access.application.dto.UpdateTenantRoleRequest;
import com.financesystem.finance.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance.modules.identity.access.domain.exception.InvalidSystemPermissionException;
import com.financesystem.finance.modules.identity.access.domain.exception.TenantRoleAlreadyExistsException;
import com.financesystem.finance.modules.identity.access.domain.exception.TenantRoleNotFoundException;
import com.financesystem.finance.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance.modules.identity.access.domain.repository.SystemPermissionRepository;
import com.financesystem.finance.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance.modules.identity.access.domain.repository.TenantRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class UpdateTenantRoleUseCase {

    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePermissionRepository tenantRolePermissionRepository;
    private final SystemPermissionRepository systemPermissionRepository;
    private final TenantRoleMapper tenantRoleMapper;
    private final AuditTrailService auditTrailService;

    public UpdateTenantRoleUseCase(
            TenantRoleRepository tenantRoleRepository,
            TenantRolePermissionRepository tenantRolePermissionRepository,
            SystemPermissionRepository systemPermissionRepository,
            TenantRoleMapper tenantRoleMapper,
            AuditTrailService auditTrailService
    ) {
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantRolePermissionRepository = tenantRolePermissionRepository;
        this.systemPermissionRepository = systemPermissionRepository;
        this.tenantRoleMapper = tenantRoleMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public TenantRoleResponse execute(UUID id, UpdateTenantRoleRequest request) {
        TenantRole existingRole = tenantRoleRepository.findById(id)
                .orElseThrow(() -> new TenantRoleNotFoundException("Tenant role not found with id: " + id));

        String normalizedName = request.name().trim().toUpperCase();
        List<String> normalizedPermissionCodes = normalizePermissionCodes(request.permissionCodes());

        tenantRoleRepository.findByName(normalizedName)
                .filter(foundRole -> !foundRole.id().equals(id))
                .ifPresent(foundRole -> {
                    throw new TenantRoleAlreadyExistsException(
                            "A tenant role with name '" + normalizedName + "' already exists"
                    );
                });

        validatePermissions(normalizedPermissionCodes);

        TenantRole updatedRole = new TenantRole(
                existingRole.id(),
                normalizedName,
                normalizeNullable(request.description()),
                existingRole.active(),
                existingRole.createdAt()
        );

        TenantRole savedRole = tenantRoleRepository.save(updatedRole);
        tenantRolePermissionRepository.replacePermissions(savedRole.id(), normalizedPermissionCodes);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ROLE_UPDATED,
                "ROLE",
                savedRole.id().toString(),
                Map.of(
                        "name", savedRole.name(),
                        "permissionCodes", normalizedPermissionCodes
                )
        );

        return tenantRoleMapper.toResponse(savedRole, normalizedPermissionCodes);
    }

    private void validatePermissions(List<String> permissionCodes) {
        Set<String> validCodes = systemPermissionRepository.findActiveCodes();

        List<String> invalidCodes = permissionCodes.stream()
                .filter(code -> !validCodes.contains(code))
                .toList();

        if (!invalidCodes.isEmpty()) {
            throw new InvalidSystemPermissionException(
                    "Invalid system permission codes: " + invalidCodes
            );
        }
    }

    private List<String> normalizePermissionCodes(List<String> permissionCodes) {
        if (permissionCodes == null) {
            return List.of();
        }

        return permissionCodes.stream()
                .filter(code -> code != null && !code.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}