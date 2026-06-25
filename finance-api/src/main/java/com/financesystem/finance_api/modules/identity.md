# Directory Export: /home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/modules/identity

_Generated on 2026-06-25 01:01:29Z_

## Summary

- Source directory: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/modules/identity`
- Output file: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/modules/identity.md`

## Files

### `access/application/dto/AssignUserRolesRequest.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.dto;

import java.util.List;
import java.util.UUID;

public record AssignUserRolesRequest(
        List<UUID> roleIds
) {
}
```

### `access/application/dto/CreateTenantRoleRequest.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateTenantRoleRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String description,

        List<String> permissionCodes
) {
}
```

### `access/application/dto/SystemPermissionResponse.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.dto;

public record SystemPermissionResponse(
        String code,
        String module,
        String description
) {
}
```

### `access/application/dto/TenantRoleResponse.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TenantRoleResponse(
        UUID id,
        String name,
        String description,
        boolean active,
        Instant createdAt,
        List<String> permissionCodes
) {
}
```

### `access/application/dto/UpdateTenantRoleRequest.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateTenantRoleRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String description,

        List<String> permissionCodes
) {
}
```

### `access/application/dto/UserRolesResponse.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.dto;

import java.util.List;
import java.util.UUID;

public record UserRolesResponse(
        UUID userId,
        List<TenantRoleResponse> roles
) {
}
```

### `access/application/mapper/SystemPermissionMapper.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.mapper;

import com.financesystem.finance_api.modules.identity.access.application.dto.SystemPermissionResponse;
import com.financesystem.finance_api.modules.identity.access.domain.model.SystemPermission;
import org.springframework.stereotype.Component;

@Component
public class SystemPermissionMapper {

    public SystemPermissionResponse toResponse(SystemPermission systemPermission) {
        return new SystemPermissionResponse(
                systemPermission.code(),
                systemPermission.module(),
                systemPermission.description()
        );
    }
}
```

### `access/application/mapper/TenantRoleMapper.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.mapper;

import com.financesystem.finance_api.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantRoleMapper {

    public TenantRoleResponse toResponse(TenantRole tenantRole, List<String> permissionCodes) {
        return new TenantRoleResponse(
                tenantRole.id(),
                tenantRole.name(),
                tenantRole.description(),
                tenantRole.active(),
                tenantRole.createdAt(),
                permissionCodes
        );
    }
}
```

### `access/application/usecase/ActivateTenantRoleUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance_api.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance_api.modules.identity.access.domain.exception.TenantRoleNotFoundException;
import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRoleSystemNames;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.TenantPlanEnforcementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class ActivateTenantRoleUseCase {

    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePermissionRepository tenantRolePermissionRepository;
    private final TenantRoleMapper tenantRoleMapper;
    private final AuditTrailService auditTrailService;
    private final TenantPlanEnforcementService tenantPlanEnforcementService;

    public ActivateTenantRoleUseCase(
            TenantRoleRepository tenantRoleRepository,
            TenantRolePermissionRepository tenantRolePermissionRepository,
            TenantRoleMapper tenantRoleMapper,
            AuditTrailService auditTrailService,
            TenantPlanEnforcementService tenantPlanEnforcementService
    ) {
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantRolePermissionRepository = tenantRolePermissionRepository;
        this.tenantRoleMapper = tenantRoleMapper;
        this.auditTrailService = auditTrailService;
        this.tenantPlanEnforcementService = tenantPlanEnforcementService;
    }

    @Transactional
    public TenantRoleResponse execute(UUID id) {
        TenantRole existingRole = tenantRoleRepository.findById(id)
                .orElseThrow(() -> new TenantRoleNotFoundException("Tenant role not found with id: " + id));

        if (!existingRole.active() && !TenantRoleSystemNames.isSystemRole(existingRole.name())) {
            tenantPlanEnforcementService.assertCanActivateRole(
                    tenantRoleRepository.countActiveCustomRoles()
            );
        }

        java.util.List<String> existingPermissionCodes = tenantRolePermissionRepository
                .findPermissionCodesByRoleId(existingRole.id())
                .stream()
                .toList();

        TenantRole updatedRole = new TenantRole(
                existingRole.id(),
                existingRole.name(),
                existingRole.description(),
                true,
                existingRole.createdAt()
        );

        TenantRole savedRole = tenantRoleRepository.save(updatedRole);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ROLE_ACTIVATED,
                "ROLE",
                savedRole.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "ACTIVATE_ROLE",
                        "name", savedRole.name(),
                        "active", true
                ),
                IdentityAuditPayloads.roleState(
                        existingRole.name(),
                        existingRole.description(),
                        existingRole.active(),
                        existingPermissionCodes.size()
                ),
                IdentityAuditPayloads.roleState(
                        savedRole.name(),
                        savedRole.description(),
                        savedRole.active(),
                        existingPermissionCodes.size()
                )
        );

        return tenantRoleMapper.toResponse(
                savedRole,
                tenantRolePermissionRepository.findPermissionCodesByRoleId(savedRole.id()).stream().toList()
        );
    }
}

```

### `access/application/usecase/AssignUserRolesUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.access.application.dto.AssignUserRolesRequest;
import com.financesystem.finance_api.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance_api.modules.identity.access.application.dto.UserRolesResponse;
import com.financesystem.finance_api.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance_api.modules.identity.access.domain.exception.TenantRoleNotFoundException;
import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AssignUserRolesUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePermissionRepository tenantRolePermissionRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final TenantRoleMapper tenantRoleMapper;
    private final AuditTrailService auditTrailService;

    public AssignUserRolesUseCase(
            TenantUserRepository tenantUserRepository,
            TenantRoleRepository tenantRoleRepository,
            TenantRolePermissionRepository tenantRolePermissionRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            TenantRoleMapper tenantRoleMapper,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantRolePermissionRepository = tenantRolePermissionRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.tenantRoleMapper = tenantRoleMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public UserRolesResponse execute(UUID userId, AssignUserRolesRequest request) {
        tenantUserRepository.findById(userId)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + userId));

        List<UUID> roleIds = request.roleIds() == null ? List.of() : request.roleIds();

        List<TenantRole> roles = tenantRoleRepository.findAllByIds(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new TenantRoleNotFoundException("One or more tenant roles do not exist");
        }

        List<String> previousRoleNames = tenantUserRoleRepository.findRoleNamesByUserId(userId);
        tenantUserRoleRepository.replaceUserRoles(userId, roleIds);

        List<TenantRoleResponse> roleResponses = roles.stream()
                .map(role -> tenantRoleMapper.toResponse(
                        role,
                        tenantRolePermissionRepository.findPermissionCodesByRoleId(role.id()).stream().toList()
                ))
                .toList();

        auditTrailService.recordTenantEvent(
                AuditEventTypes.USER_ROLES_ASSIGNED,
                "USER",
                userId.toString(),
                IdentityAuditPayloads.of(
                        "operation", "ASSIGN_USER_ROLES",
                        "roleIds", roleIds,
                        "roleCount", roleIds.size()
                ),
                IdentityAuditPayloads.of(
                        "roleNames", previousRoleNames,
                        "roleCount", previousRoleNames.size()
                ),
                IdentityAuditPayloads.of(
                        "roleNames", roleResponses.stream().map(TenantRoleResponse::name).toList(),
                        "roleCount", roleResponses.size()
                )
        );

        return new UserRolesResponse(userId, roleResponses);
    }
}

```

### `access/application/usecase/CreateTenantRoleUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.access.application.dto.CreateTenantRoleRequest;
import com.financesystem.finance_api.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance_api.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance_api.modules.identity.access.domain.exception.InvalidSystemPermissionException;
import com.financesystem.finance_api.modules.identity.access.domain.exception.TenantRoleAlreadyExistsException;
import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance_api.modules.identity.access.domain.repository.SystemPermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.TenantPlanEnforcementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CreateTenantRoleUseCase {

    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePermissionRepository tenantRolePermissionRepository;
    private final SystemPermissionRepository systemPermissionRepository;
    private final TenantRoleMapper tenantRoleMapper;
    private final AuditTrailService auditTrailService;
    private final TenantPlanEnforcementService tenantPlanEnforcementService;

    public CreateTenantRoleUseCase(
            TenantRoleRepository tenantRoleRepository,
            TenantRolePermissionRepository tenantRolePermissionRepository,
            SystemPermissionRepository systemPermissionRepository,
            TenantRoleMapper tenantRoleMapper,
            AuditTrailService auditTrailService,
            TenantPlanEnforcementService tenantPlanEnforcementService
    ) {
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantRolePermissionRepository = tenantRolePermissionRepository;
        this.systemPermissionRepository = systemPermissionRepository;
        this.tenantRoleMapper = tenantRoleMapper;
        this.auditTrailService = auditTrailService;
        this.tenantPlanEnforcementService = tenantPlanEnforcementService;
    }

    @Transactional
    public TenantRoleResponse execute(CreateTenantRoleRequest request) {
        String normalizedName = request.name().trim().toUpperCase();
        List<String> normalizedPermissionCodes = normalizePermissionCodes(request.permissionCodes());

        if (tenantRoleRepository.existsByName(normalizedName)) {
            throw new TenantRoleAlreadyExistsException(
                    "A tenant role with name '" + normalizedName + "' already exists"
            );
        }

        validatePermissions(normalizedPermissionCodes);

        tenantPlanEnforcementService.assertCanCreateRole(
                tenantRoleRepository.countActiveCustomRoles()
        );

        TenantRole roleToCreate = new TenantRole(
                null,
                normalizedName,
                normalizeNullable(request.description()),
                true,
                null
        );

        TenantRole createdRole = tenantRoleRepository.save(roleToCreate);
        tenantRolePermissionRepository.replacePermissions(createdRole.id(), normalizedPermissionCodes);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ROLE_CREATED,
                "ROLE",
                createdRole.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "CREATE_ROLE",
                        "name", createdRole.name(),
                        "description", createdRole.description(),
                        "permissionCount", normalizedPermissionCodes.size()
                ),
                null,
                IdentityAuditPayloads.roleState(
                        createdRole.name(),
                        createdRole.description(),
                        createdRole.active(),
                        normalizedPermissionCodes.size()
                )
        );

        return tenantRoleMapper.toResponse(createdRole, normalizedPermissionCodes);
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

```

### `access/application/usecase/DeactivateTenantRoleUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
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

        java.util.List<String> existingPermissionCodes = tenantRolePermissionRepository
                .findPermissionCodesByRoleId(existingRole.id())
                .stream()
                .toList();

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
                IdentityAuditPayloads.of(
                        "operation", "DEACTIVATE_ROLE",
                        "name", savedRole.name(),
                        "active", false
                ),
                IdentityAuditPayloads.roleState(
                        existingRole.name(),
                        existingRole.description(),
                        existingRole.active(),
                        existingPermissionCodes.size()
                ),
                IdentityAuditPayloads.roleState(
                        savedRole.name(),
                        savedRole.description(),
                        savedRole.active(),
                        existingPermissionCodes.size()
                )
        );

        return tenantRoleMapper.toResponse(
                savedRole,
                tenantRolePermissionRepository.findPermissionCodesByRoleId(savedRole.id()).stream().toList()
        );
    }
}

```

### `access/application/usecase/GetTenantRoleByIdUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance_api.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance_api.modules.identity.access.domain.exception.TenantRoleNotFoundException;
import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetTenantRoleByIdUseCase {

    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePermissionRepository tenantRolePermissionRepository;
    private final TenantRoleMapper tenantRoleMapper;

    public GetTenantRoleByIdUseCase(
            TenantRoleRepository tenantRoleRepository,
            TenantRolePermissionRepository tenantRolePermissionRepository,
            TenantRoleMapper tenantRoleMapper
    ) {
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantRolePermissionRepository = tenantRolePermissionRepository;
        this.tenantRoleMapper = tenantRoleMapper;
    }

    public TenantRoleResponse execute(UUID id) {
        TenantRole role = tenantRoleRepository.findById(id)
                .orElseThrow(() -> new TenantRoleNotFoundException("Tenant role not found with id: " + id));

        return tenantRoleMapper.toResponse(
                role,
                tenantRolePermissionRepository.findPermissionCodesByRoleId(role.id()).stream().toList()
        );
    }
}
```

### `access/application/usecase/GetUserRolesUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance_api.modules.identity.access.application.dto.UserRolesResponse;
import com.financesystem.finance_api.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetUserRolesUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePermissionRepository tenantRolePermissionRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final TenantRoleMapper tenantRoleMapper;

    public GetUserRolesUseCase(
            TenantUserRepository tenantUserRepository,
            TenantRoleRepository tenantRoleRepository,
            TenantRolePermissionRepository tenantRolePermissionRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            TenantRoleMapper tenantRoleMapper
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantRolePermissionRepository = tenantRolePermissionRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.tenantRoleMapper = tenantRoleMapper;
    }

    public UserRolesResponse execute(UUID userId) {
        tenantUserRepository.findById(userId)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + userId));

        List<UUID> roleIds = tenantUserRoleRepository.findRoleIdsByUserId(userId);

        List<TenantRoleResponse> roles = tenantRoleRepository.findAllByIds(roleIds)
                .stream()
                .map(role -> tenantRoleMapper.toResponse(
                        role,
                        tenantRolePermissionRepository.findPermissionCodesByRoleId(role.id()).stream().toList()
                ))
                .toList();

        return new UserRolesResponse(userId, roles);
    }
}
```

### `access/application/usecase/ListSystemPermissionsUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.identity.access.application.dto.SystemPermissionResponse;
import com.financesystem.finance_api.modules.identity.access.application.mapper.SystemPermissionMapper;
import com.financesystem.finance_api.modules.identity.access.domain.repository.SystemPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListSystemPermissionsUseCase {

    private final SystemPermissionRepository systemPermissionRepository;
    private final SystemPermissionMapper systemPermissionMapper;

    public ListSystemPermissionsUseCase(
            SystemPermissionRepository systemPermissionRepository,
            SystemPermissionMapper systemPermissionMapper
    ) {
        this.systemPermissionRepository = systemPermissionRepository;
        this.systemPermissionMapper = systemPermissionMapper;
    }

    public List<SystemPermissionResponse> execute() {
        return systemPermissionRepository.findAllActive()
                .stream()
                .map(systemPermissionMapper::toResponse)
                .toList();
    }
}
```

### `access/application/usecase/ListTenantRolesUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance_api.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTenantRolesUseCase {

    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePermissionRepository tenantRolePermissionRepository;
    private final TenantRoleMapper tenantRoleMapper;

    public ListTenantRolesUseCase(
            TenantRoleRepository tenantRoleRepository,
            TenantRolePermissionRepository tenantRolePermissionRepository,
            TenantRoleMapper tenantRoleMapper
    ) {
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantRolePermissionRepository = tenantRolePermissionRepository;
        this.tenantRoleMapper = tenantRoleMapper;
    }

    public List<TenantRoleResponse> execute() {
        return tenantRoleRepository.findAll()
                .stream()
                .map(role -> tenantRoleMapper.toResponse(
                        role,
                        tenantRolePermissionRepository.findPermissionCodesByRoleId(role.id())
                                .stream()
                                .toList()
                ))
                .toList();
    }
}
```

### `access/application/usecase/UpdateTenantRoleUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.access.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance_api.modules.identity.access.application.dto.UpdateTenantRoleRequest;
import com.financesystem.finance_api.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance_api.modules.identity.access.domain.exception.InvalidSystemPermissionException;
import com.financesystem.finance_api.modules.identity.access.domain.exception.TenantRoleAlreadyExistsException;
import com.financesystem.finance_api.modules.identity.access.domain.exception.TenantRoleNotFoundException;
import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance_api.modules.identity.access.domain.repository.SystemPermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
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

        List<String> existingPermissionCodes = tenantRolePermissionRepository
                .findPermissionCodesByRoleId(existingRole.id())
                .stream()
                .toList();

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
                IdentityAuditPayloads.of(
                        "operation", "UPDATE_ROLE",
                        "previousName", existingRole.name(),
                        "newName", savedRole.name(),
                        "description", savedRole.description(),
                        "previousPermissionCount", existingPermissionCodes.size(),
                        "newPermissionCount", normalizedPermissionCodes.size()
                ),
                IdentityAuditPayloads.roleState(
                        existingRole.name(),
                        existingRole.description(),
                        existingRole.active(),
                        existingPermissionCodes.size()
                ),
                IdentityAuditPayloads.roleState(
                        savedRole.name(),
                        savedRole.description(),
                        savedRole.active(),
                        normalizedPermissionCodes.size()
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

```

### `access/domain/exception/InvalidSystemPermissionException.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class InvalidSystemPermissionException extends BusinessException {

    public InvalidSystemPermissionException(String message) {
        super(message);
    }
}
```

### `access/domain/exception/TenantRoleAlreadyExistsException.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class TenantRoleAlreadyExistsException extends BusinessException {

    public TenantRoleAlreadyExistsException(String message) {
        super(message);
    }
}
```

### `access/domain/exception/TenantRoleNotFoundException.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class TenantRoleNotFoundException extends ResourceNotFoundException {

    public TenantRoleNotFoundException(String message) {
        super(message);
    }
}
```

### `access/domain/model/SystemPermission.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SystemPermission(
        UUID id,
        String code,
        String module,
        String description,
        boolean active,
        Instant createdAt
) {
}
```

### `access/domain/model/TenantRole.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TenantRole(
        UUID id,
        String name,
        String description,
        boolean active,
        Instant createdAt
) {
}
```

### `access/domain/model/TenantRoleSystemNames.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.model;

import java.util.List;

public final class TenantRoleSystemNames {

    public static final List<String> DEFAULT_SYSTEM_ROLES = List.of(
            "OWNER_ADMIN",
            "ADMIN",
            "USER"
    );

    private TenantRoleSystemNames() {
    }

    public static boolean isSystemRole(String roleName) {
        if (roleName == null) {
            return false;
        }

        return DEFAULT_SYSTEM_ROLES.contains(roleName.trim().toUpperCase());
    }
}

```

### `access/domain/repository/SystemPermissionRepository.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.repository;

import com.financesystem.finance_api.modules.identity.access.domain.model.SystemPermission;

import java.util.List;
import java.util.Set;

public interface SystemPermissionRepository {

    List<SystemPermission> findAllActive();

    Set<String> findActiveCodes();
}
```

### `access/domain/repository/TenantRolePermissionRepository.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TenantRolePermissionRepository {

    Set<String> findPermissionCodesByRoleId(UUID roleId);

    void replacePermissions(UUID roleId, List<String> permissionCodes);
}
```

### `access/domain/repository/TenantRoleRepository.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.repository;

import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRoleRepository {

    TenantRole save(TenantRole tenantRole);

    Optional<TenantRole> findById(UUID id);

    Optional<TenantRole> findByName(String name);

    List<TenantRole> findAll();

    List<TenantRole> findAllByIds(List<UUID> ids);

    boolean existsByName(String name);

    long countActiveCustomRoles();
}

```

### `access/domain/repository/TenantUserRoleRepository.java`

```java
package com.financesystem.finance_api.modules.identity.access.domain.repository;

import java.util.List;
import java.util.UUID;

public interface TenantUserRoleRepository {

    List<UUID> findRoleIdsByUserId(UUID userId);

    List<String> findRoleNamesByUserId(UUID userId);

    List<String> findPermissionCodesByUserId(UUID userId);

    void replaceUserRoles(UUID userId, List<UUID> roleIds);
}

```

### `access/infrastructure/api/AccessController.java`

```java
package com.financesystem.finance_api.modules.identity.access.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.modules.identity.access.application.dto.*;
import com.financesystem.finance_api.modules.identity.access.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/access")
@SecurityRequirement(name = "bearerAuth")
public class AccessController {

    private final ListSystemPermissionsUseCase listSystemPermissionsUseCase;
    private final CreateTenantRoleUseCase createTenantRoleUseCase;
    private final ListTenantRolesUseCase listTenantRolesUseCase;
    private final GetTenantRoleByIdUseCase getTenantRoleByIdUseCase;
    private final UpdateTenantRoleUseCase updateTenantRoleUseCase;
    private final ActivateTenantRoleUseCase activateTenantRoleUseCase;
    private final DeactivateTenantRoleUseCase deactivateTenantRoleUseCase;
    private final GetUserRolesUseCase getUserRolesUseCase;
    private final AssignUserRolesUseCase assignUserRolesUseCase;

    public AccessController(
            ListSystemPermissionsUseCase listSystemPermissionsUseCase,
            CreateTenantRoleUseCase createTenantRoleUseCase,
            ListTenantRolesUseCase listTenantRolesUseCase,
            GetTenantRoleByIdUseCase getTenantRoleByIdUseCase,
            UpdateTenantRoleUseCase updateTenantRoleUseCase,
            ActivateTenantRoleUseCase activateTenantRoleUseCase,
            DeactivateTenantRoleUseCase deactivateTenantRoleUseCase,
            GetUserRolesUseCase getUserRolesUseCase,
            AssignUserRolesUseCase assignUserRolesUseCase
    ) {
        this.listSystemPermissionsUseCase = listSystemPermissionsUseCase;
        this.createTenantRoleUseCase = createTenantRoleUseCase;
        this.listTenantRolesUseCase = listTenantRolesUseCase;
        this.getTenantRoleByIdUseCase = getTenantRoleByIdUseCase;
        this.updateTenantRoleUseCase = updateTenantRoleUseCase;
        this.activateTenantRoleUseCase = activateTenantRoleUseCase;
        this.deactivateTenantRoleUseCase = deactivateTenantRoleUseCase;
        this.getUserRolesUseCase = getUserRolesUseCase;
        this.assignUserRolesUseCase = assignUserRolesUseCase;
    }

    // @GetMapping("/permissions")
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('access.permissions.read')")
    public ApiResponse<Page<SystemPermissionResponse>> listPermissions(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(
                "System permissions retrieved successfully",
                PaginationSupport.page(listSystemPermissionsUseCase.execute(), pageable)
        );
    }

    // @GetMapping("/roles")
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('access.roles.read')")
    public ApiResponse<Page<TenantRoleResponse>> listRoles(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(
                "Tenant roles retrieved successfully",
                PaginationSupport.page(listTenantRolesUseCase.execute(), pageable)
        );
    }

    // @PostMapping("/roles")
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('access.roles.create')")
    public ApiResponse<TenantRoleResponse> createRole(@Valid @RequestBody CreateTenantRoleRequest request) {
        return ApiResponse.success(
                "Tenant role created successfully",
                createTenantRoleUseCase.execute(request)
        );
    }

    // @GetMapping("/roles/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('access.roles.detail')")
    public ApiResponse<TenantRoleResponse> getRoleById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant role retrieved successfully",
                getTenantRoleByIdUseCase.execute(id)
        );
    }

    // @PutMapping("/roles/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('access.roles.update')")
    public ApiResponse<TenantRoleResponse> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantRoleRequest request
    ) {
        return ApiResponse.success(
                "Tenant role updated successfully",
                updateTenantRoleUseCase.execute(id, request)
        );
    }

    // @PatchMapping("/roles/{id}/activate")
    // @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/roles/{id}/activate")
    @PreAuthorize("hasAuthority('access.roles.activate')")
    public ApiResponse<TenantRoleResponse> activateRole(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant role activated successfully",
                activateTenantRoleUseCase.execute(id)
        );
    }

    // @PatchMapping("/roles/{id}/deactivate")
    // @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/roles/{id}/deactivate")
    @PreAuthorize("hasAuthority('access.roles.deactivate')")
    public ApiResponse<TenantRoleResponse> deactivateRole(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant role deactivated successfully",
                deactivateTenantRoleUseCase.execute(id)
        );
    }

    // @GetMapping("/users/{userId}/roles")
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('access.users.roles.read')")
    public ApiResponse<UserRolesResponse> getUserRoles(@PathVariable UUID userId) {
        return ApiResponse.success(
                "User roles retrieved successfully",
                getUserRolesUseCase.execute(userId)
        );
    }

    // @PutMapping("/users/{userId}/roles")
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('access.users.roles.assign')")
    public ApiResponse<UserRolesResponse> assignUserRoles(
            @PathVariable UUID userId,
            @RequestBody AssignUserRolesRequest request
    ) {
        return ApiResponse.success(
                "User roles assigned successfully",
                assignUserRolesUseCase.execute(userId, request)
        );
    }
}

```

### `access/infrastructure/persistence/SystemPermissionEntity.java`

```java
package com.financesystem.finance_api.modules.identity.access.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "system_permissions")
public class SystemPermissionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 50)
    private String module;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

### `access/infrastructure/persistence/SystemPermissionJpaRepository.java`

```java
package com.financesystem.finance_api.modules.identity.access.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SystemPermissionJpaRepository extends JpaRepository<SystemPermissionEntity, UUID> {

    List<SystemPermissionEntity> findByActiveTrueOrderByModuleAscCodeAsc();
}
```

### `access/infrastructure/persistence/SystemPermissionRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.identity.access.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.access.domain.model.SystemPermission;
import com.financesystem.finance_api.modules.identity.access.domain.repository.SystemPermissionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class SystemPermissionRepositoryAdapter implements SystemPermissionRepository {

    private final SystemPermissionJpaRepository jpaRepository;

    public SystemPermissionRepositoryAdapter(SystemPermissionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<SystemPermission> findAllActive() {
        return jpaRepository.findByActiveTrueOrderByModuleAscCodeAsc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Set<String> findActiveCodes() {
        return jpaRepository.findByActiveTrueOrderByModuleAscCodeAsc()
                .stream()
                .map(SystemPermissionEntity::getCode)
                .collect(Collectors.toSet());
    }

    private SystemPermission toDomain(SystemPermissionEntity entity) {
        return new SystemPermission(
                entity.getId(),
                entity.getCode(),
                entity.getModule(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}
```

### `access/infrastructure/persistence/TenantRoleEntity.java`

```java
package com.financesystem.finance_api.modules.identity.access.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_roles")
public class TenantRoleEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

### `access/infrastructure/persistence/TenantRoleJpaRepository.java`

```java
package com.financesystem.finance_api.modules.identity.access.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TenantRoleJpaRepository extends JpaRepository<TenantRoleEntity, UUID> {

    Optional<TenantRoleEntity> findByName(String name);

    boolean existsByName(String name);

    long countByActiveTrueAndNameNotIn(Collection<String> names);
}

```

### `access/infrastructure/persistence/TenantRolePermissionRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.identity.access.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class TenantRolePermissionRepositoryAdapter implements TenantRolePermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    public TenantRolePermissionRepositoryAdapter(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Set<String> findPermissionCodesByRoleId(UUID roleId) {
        List<String> codes = jdbcTemplate.queryForList(
                """
                SELECT permission_code
                FROM tenant_role_permissions
                WHERE role_id = ?
                ORDER BY permission_code ASC
                """,
                String.class,
                roleId
        );

        return new LinkedHashSet<>(codes);
    }

    @Override
    public void replacePermissions(UUID roleId, List<String> permissionCodes) {
        jdbcTemplate.update("DELETE FROM tenant_role_permissions WHERE role_id = ?", roleId);

        for (String permissionCode : permissionCodes) {
            jdbcTemplate.update(
                    """
                    INSERT INTO tenant_role_permissions (role_id, permission_code, assigned_at)
                    VALUES (?, ?, NOW())
                    """,
                    roleId,
                    permissionCode
            );
        }
    }
}
```

### `access/infrastructure/persistence/TenantRoleRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.identity.access.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance_api.modules.identity.access.domain.model.TenantRoleSystemNames;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TenantRoleRepositoryAdapter implements TenantRoleRepository {

    private final TenantRoleJpaRepository jpaRepository;

    public TenantRoleRepositoryAdapter(TenantRoleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TenantRole save(TenantRole tenantRole) {
        TenantRoleEntity entity = toEntity(tenantRole);
        TenantRoleEntity saved = jpaRepository.saveAndFlush(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TenantRole> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TenantRole> findByName(String name) {
        return jpaRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public List<TenantRole> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<TenantRole> findAllByIds(List<UUID> ids) {
        return jpaRepository.findAllById(ids)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public long countActiveCustomRoles() {
        return jpaRepository.countByActiveTrueAndNameNotIn(TenantRoleSystemNames.DEFAULT_SYSTEM_ROLES);
    }

    private TenantRoleEntity toEntity(TenantRole tenantRole) {
        TenantRoleEntity entity = new TenantRoleEntity();
        entity.setId(tenantRole.id());
        entity.setName(tenantRole.name());
        entity.setDescription(tenantRole.description());
        entity.setActive(tenantRole.active());
        return entity;
    }

    private TenantRole toDomain(TenantRoleEntity entity) {
        return new TenantRole(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}

```

### `access/infrastructure/persistence/TenantUserRoleRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.identity.access.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Repository
public class TenantUserRoleRepositoryAdapter implements TenantUserRoleRepository {

    private final JdbcTemplate jdbcTemplate;

    public TenantUserRoleRepositoryAdapter(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<UUID> findRoleIdsByUserId(UUID userId) {
        return jdbcTemplate.query(
                """
                SELECT role_id
                FROM tenant_user_roles
                WHERE user_id = ?
                ORDER BY assigned_at ASC
                """,
                (rs, rowNum) -> rs.getObject("role_id", UUID.class),
                userId
        );
    }

    @Override
    public List<String> findRoleNamesByUserId(UUID userId) {
        return jdbcTemplate.query(
                """
                SELECT r.name
                FROM tenant_user_roles ur
                JOIN tenant_roles r ON r.id = ur.role_id
                WHERE ur.user_id = ?
                  AND r.active = true
                ORDER BY r.name ASC
                """,
                (rs, rowNum) -> rs.getString("name"),
                userId
        );
    }

    @Override
    public List<String> findPermissionCodesByUserId(UUID userId) {
        return jdbcTemplate.query(
                """
                SELECT DISTINCT rp.permission_code
                FROM tenant_user_roles ur
                JOIN tenant_roles r ON r.id = ur.role_id
                JOIN tenant_role_permissions rp ON rp.role_id = r.id
                JOIN public.system_permissions sp ON sp.code = rp.permission_code
                WHERE ur.user_id = ?
                  AND r.active = true
                  AND sp.active = true
                  AND rp.permission_code IS NOT NULL
                  AND rp.permission_code <> ''
                ORDER BY rp.permission_code ASC
                """,
                (rs, rowNum) -> rs.getString("permission_code"),
                userId
        );
    }

    @Override
    public void replaceUserRoles(UUID userId, List<UUID> roleIds) {
        jdbcTemplate.update("DELETE FROM tenant_user_roles WHERE user_id = ?", userId);

        for (UUID roleId : roleIds) {
            jdbcTemplate.update(
                    """
                    INSERT INTO tenant_user_roles (user_id, role_id, assigned_at)
                    VALUES (?, ?, NOW())
                    """,
                    userId,
                    roleId
            );
        }
    }
}

```

### `audit/IdentityAuditPayloads.java`

```java
package com.financesystem.finance_api.modules.identity.audit;

import java.util.LinkedHashMap;
import java.util.Map;

public final class IdentityAuditPayloads {

    private IdentityAuditPayloads() {
    }

    public static Map<String, Object> of(Object... keyValues) {
        if (keyValues == null || keyValues.length == 0) {
            return Map.of();
        }

        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value array must contain an even number of elements");
        }

        Map<String, Object> values = new LinkedHashMap<>();
        for (int index = 0; index < keyValues.length; index += 2) {
            Object key = keyValues[index];
            Object value = keyValues[index + 1];

            if (!(key instanceof String keyName) || keyName.isBlank()) {
                continue;
            }

            if (value != null) {
                values.put(keyName, value);
            }
        }

        return values;
    }

    public static Map<String, Object> userState(
            String email,
            String firstName,
            String lastName,
            boolean active,
            String status
    ) {
        return of(
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "active", active,
                "status", status
        );
    }

    public static Map<String, Object> roleState(
            String name,
            String description,
            boolean active,
            int permissionCount
    ) {
        return of(
                "name", name,
                "description", description,
                "active", active,
                "permissionCount", permissionCount
        );
    }
}

```

### `auth/application/config/FaceRecognitionProperties.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "app.auth.face-recognition")
public class FaceRecognitionProperties {

    private boolean enabled = true;
    private String apiKey = "";
    private String apiSecret = "";
    private String baseUrl = "https://api-us.faceplusplus.com/facepp/v3";
    private String fallbackBaseUrl = "";
    private double confidenceThreshold = 80.0;
    private int timeoutMs = 15000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getFallbackBaseUrl() {
        return fallbackBaseUrl;
    }

    public void setFallbackBaseUrl(String fallbackBaseUrl) {
        this.fallbackBaseUrl = fallbackBaseUrl;
    }

    public double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public void setConfidenceThreshold(double confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public boolean isConfigured() {
        return enabled
                && StringUtils.hasText(apiKey)
                && StringUtils.hasText(apiSecret);
    }
}

```

### `auth/application/config/ProfilePhotoStorageProperties.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "app.storage.profile-photos")
public class ProfilePhotoStorageProperties {

    private String rootDir = "uploads/profile-photos";

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public boolean isConfigured() {
        return StringUtils.hasText(rootDir);
    }
}

```

### `auth/application/dto/AuthenticatedTenantUserResponse.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;

import java.util.List;
import java.util.UUID;

public record AuthenticatedTenantUserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean active,
        String status,
        String tenantSlug,
        List<String> roles
) {
}
```

### `auth/application/dto/AuthTokenResponse.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;

public record AuthTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long accessExpiresInMs
) {
}
```

### `auth/application/dto/ChangePasswordRequest.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 100)
        String newPassword
) {
}
```

### `auth/application/dto/CurrentTenantProfilePhotoResponse.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;

public record CurrentTenantProfilePhotoResponse(
        byte[] bytes,
        String contentType,
        String filename
) {
}

```

### `auth/application/dto/CurrentTenantProfileResponse.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;

import java.time.Instant;
import java.util.UUID;

public record CurrentTenantProfileResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean active,
        String status,
        String tenantSlug,
        boolean faceLoginEnabled,
        boolean profilePhotoAvailable,
        String profilePhotoUrl,
        String profilePhotoContentType,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `auth/application/dto/ForgotPasswordRequest.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
        @NotBlank
        @Email
        @Size(max = 150)
        String email
) {
}
```

### `auth/application/dto/LoginRequest.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password
) {
}
```

### `auth/application/dto/RefreshTokenRequest.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank
        String refreshToken
) {
}
```

### `auth/application/dto/ResetPasswordRequest.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank
        String token,

        @NotBlank
        @Size(min = 8, max = 100)
        String newPassword
) {
}
```

### `auth/application/dto/UpdateCurrentTenantProfileRequest.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.dto;


import jakarta.validation.constraints.Size;

public record UpdateCurrentTenantProfileRequest(
        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName
) {
}

```

### `auth/application/exception/FaceRecognitionUnavailableException.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.exception;

public class FaceRecognitionUnavailableException extends RuntimeException {

    public FaceRecognitionUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

### `auth/application/port/FaceRecognitionPort.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.port;

public interface FaceRecognitionPort {

    DetectedFace detectFace(byte[] imageBytes, String filename, String contentType);

    FaceComparisonResult compareFaceTokens(String faceToken1, String faceToken2);

    boolean isConfigured();

    record DetectedFace(String faceToken, String faceId) {
    }

    record FaceComparisonResult(double confidence) {
    }
}

```

### `auth/application/port/ProfilePhotoStoragePort.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.port;

import java.util.UUID;

public interface ProfilePhotoStoragePort {

    StoredProfilePhoto store(String tenantSlug, UUID userId, byte[] bytes, String contentType);

    PhotoFile read(String tenantSlug, UUID userId, String contentType);

    PhotoFile readPublic(String tenantSlug, UUID userId);

    void delete(String tenantSlug, UUID userId);

    record StoredProfilePhoto(
            String url
    ) {
    }

    record PhotoFile(
            byte[] bytes,
            String contentType,
            String filename
    ) {
    }
}

```

### `auth/application/usecase/ChangePasswordUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.governance.notifications.application.dto.NotificationPublishRequest;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationCategory;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPriority;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationType;
import com.financesystem.finance_api.modules.governance.notifications.domain.port.NotificationPublisherPort;
import com.financesystem.finance_api.modules.identity.auth.application.dto.ChangePasswordRequest;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

@Service
public class ChangePasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordUseCase.class);

    private final SecurityContextFacade securityContextFacade;
    private final TenantUserRepository tenantUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditTrailService auditTrailService;
    private final NotificationPublisherPort notificationPublisherPort;
    private final ObjectMapper objectMapper;

    public ChangePasswordUseCase(
            SecurityContextFacade securityContextFacade,
            TenantUserRepository tenantUserRepository,
            PasswordEncoder passwordEncoder,
            AuditTrailService auditTrailService,
            NotificationPublisherPort notificationPublisherPort,
            ObjectMapper objectMapper
    ) {
        this.securityContextFacade = securityContextFacade;
        this.tenantUserRepository = tenantUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditTrailService = auditTrailService;
        this.notificationPublisherPort = notificationPublisherPort;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void execute(ChangePasswordRequest request) {
        String currentSubject = securityContextFacade.getCurrentSubject();

        TenantUser tenantUser = tenantUserRepository.findById(parseSubjectAsUserId(currentSubject))
                .orElseThrow(() -> new BusinessException("Authenticated user was not found"));

        if (!passwordEncoder.matches(request.currentPassword(), tenantUser.passwordHash())) {
            throw new BusinessException("Current password is incorrect");
        }

        if (request.currentPassword().equals(request.newPassword())) {
            throw new BusinessException("New password must be different from current password");
        }

        TenantUser updatedUser = new TenantUser(
                tenantUser.id(),
                tenantUser.email(),
                passwordEncoder.encode(request.newPassword()),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status(),
                tenantUser.createdAt(),
                tenantUser.updatedAt()
        );

        tenantUserRepository.save(updatedUser);

        Instant changedAt = Instant.now();
        ObjectNode data = objectMapper.createObjectNode()
                .put("email", tenantUser.email())
                .put("status", "CHANGED")
                .put("changedAt", changedAt.toString());

        publishNotificationSafely(new NotificationPublishRequest(
                tenantUser.id(),
                NotificationType.PASSWORD_CHANGED,
                NotificationCategory.SECURITY,
                NotificationPriority.HIGH,
                "Password changed",
                "Your password was changed successfully.",
                data,
                null,
                "/security/password",
                Instant.now().plusSeconds(3600)
        ));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.PASSWORD_CHANGED,
                "USER",
                tenantUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "CHANGE_PASSWORD",
                        "email", tenantUser.email(),
                        "tenantSlug", securityContextFacade.getCurrentTenantSlug(),
                        "subject", currentSubject
                ),
                IdentityAuditPayloads.of(
                        "passwordState", "CURRENT"
                ),
                IdentityAuditPayloads.of(
                        "passwordState", "UPDATED",
                        "changedAt", changedAt.toString()
                )
        );
    }

    private UUID parseSubjectAsUserId(String subject) {
        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Authenticated subject is not a valid user id");
        }
    }

    private void publishNotificationSafely(NotificationPublishRequest request) {
        try {
            notificationPublisherPort.publish(request);
        } catch (Exception exception) {
            log.warn("Unable to publish password change notification: {}", exception.getMessage());
        }
    }
}

```

### `auth/application/usecase/ForgotPasswordUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.governance.notifications.application.dto.NotificationPublishRequest;
import com.financesystem.finance_api.modules.governance.notifications.application.config.PasswordResetNotificationProperties;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationCategory;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPriority;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationType;
import com.financesystem.finance_api.modules.governance.notifications.domain.port.NotificationPublisherPort;
import com.financesystem.finance_api.modules.governance.notifications.application.usecase.SendPasswordResetNotificationUseCase;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.PasswordResetNotification;
import com.financesystem.finance_api.modules.identity.auth.application.dto.ForgotPasswordRequest;
import com.financesystem.finance_api.modules.identity.auth.domain.model.PasswordResetToken;
import com.financesystem.finance_api.modules.identity.auth.domain.repository.PasswordResetTokenRepository;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class ForgotPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordUseCase.class);

    private final TenantUserRepository tenantUserRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetNotificationProperties properties;
    private final SendPasswordResetNotificationUseCase sendPasswordResetNotificationUseCase;
    private final AuditTrailService auditTrailService;
    private final NotificationPublisherPort notificationPublisherPort;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public ForgotPasswordUseCase(
            TenantUserRepository tenantUserRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetNotificationProperties properties,
            SendPasswordResetNotificationUseCase sendPasswordResetNotificationUseCase,
            AuditTrailService auditTrailService,
            NotificationPublisherPort notificationPublisherPort,
            ObjectMapper objectMapper
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.properties = properties;
        this.sendPasswordResetNotificationUseCase = sendPasswordResetNotificationUseCase;
        this.auditTrailService = auditTrailService;
        this.notificationPublisherPort = notificationPublisherPort;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void execute(ForgotPasswordRequest request) {
        TenantContext tenantContext = TenantContextHolder.getRequired();
        String normalizedEmail = request.email().trim().toLowerCase();

        TenantUser tenantUser = tenantUserRepository.findByEmail(normalizedEmail).orElse(null);

        if (tenantUser == null || !tenantUser.active()) {
            return;
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getExpirationMinutes(), ChronoUnit.MINUTES);

        passwordResetTokenRepository.invalidateAllByEmail(normalizedEmail, now);

        String token = generateSecureToken();

        PasswordResetToken passwordResetToken = new PasswordResetToken(
                null,
                normalizedEmail,
                token,
                expiresAt,
                false,
                null,
                null
        );

        passwordResetTokenRepository.save(passwordResetToken);

        sendPasswordResetNotificationUseCase.execute(
                new PasswordResetNotification(
                        normalizedEmail,
                        tenantContext.tenantSlug(),
                        token,
                        expiresAt
                )
        );

        ObjectNode data = objectMapper.createObjectNode()
                .put("tenantSlug", tenantContext.tenantSlug())
                .put("email", normalizedEmail)
                .put("expiresAt", expiresAt.toString())
                .put("status", "REQUESTED");

        publishNotificationSafely(new NotificationPublishRequest(
                tenantUser.id(),
                NotificationType.PASSWORD_RESET_REQUESTED,
                NotificationCategory.SECURITY,
                NotificationPriority.HIGH,
                "Password reset requested",
                "A password reset request was created for your account.",
                data,
                null,
                "/security/password-reset",
                expiresAt
        ));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.PASSWORD_RESET_REQUESTED,
                "USER",
                tenantUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "FORGOT_PASSWORD",
                        "email", normalizedEmail,
                        "tenantSlug", tenantContext.tenantSlug(),
                        "expiresAt", expiresAt.toString()
                ),
                IdentityAuditPayloads.of(
                        "resetTokenState", "NONE"
                ),
                IdentityAuditPayloads.of(
                        "resetTokenState", "REQUESTED",
                        "expiresAt", expiresAt.toString()
                )
        );
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void publishNotificationSafely(NotificationPublishRequest request) {
        try {
            notificationPublisherPort.publish(request);
        } catch (Exception exception) {
            log.warn("Unable to publish password reset notification: {}", exception.getMessage());
        }
    }
}

```

### `auth/application/usecase/GetCurrentAuthenticatedTenantUserUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.auth.application.dto.AuthenticatedTenantUserResponse;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetCurrentAuthenticatedTenantUserUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final TenantUserRepository tenantUserRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;

    public GetCurrentAuthenticatedTenantUserUseCase(
            SecurityContextFacade securityContextFacade,
            TenantUserRepository tenantUserRepository,
            TenantUserRoleRepository tenantUserRoleRepository
    ) {
        this.securityContextFacade = securityContextFacade;
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
    }

    public AuthenticatedTenantUserResponse execute() {
        String currentSubject = securityContextFacade.getCurrentSubject();
        String currentTenantSlug = securityContextFacade.getCurrentTenantSlug();

        if (currentSubject == null || currentSubject.isBlank()) {
            throw new AuthenticationFailedException("Authenticated subject is not available");
        }

        TenantUser tenantUser = tenantUserRepository.findById(parseSubjectAsUserId(currentSubject))
                .orElseThrow(() -> new AuthenticationFailedException("Authenticated user not found"));

        List<String> roles = tenantUserRoleRepository.findRoleNamesByUserId(tenantUser.id());

        return new AuthenticatedTenantUserResponse(
                tenantUser.id(),
                tenantUser.email(),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status().name(),
                currentTenantSlug,
                roles
        );
    }

    private UUID parseSubjectAsUserId(String subject) {
        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new AuthenticationFailedException("Authenticated subject is not a valid user id");
        }
    }
}

```

### `auth/application/usecase/GetCurrentTenantProfilePhotoUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.identity.auth.application.dto.CurrentTenantProfilePhotoResponse;
import com.financesystem.finance_api.modules.identity.auth.application.port.ProfilePhotoStoragePort;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.identity.auth.domain.model.TenantUserFaceLoginProfile;
import com.financesystem.finance_api.modules.identity.auth.domain.repository.TenantUserFaceLoginProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetCurrentTenantProfilePhotoUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final TenantUserFaceLoginProfileRepository faceLoginProfileRepository;
    private final ProfilePhotoStoragePort profilePhotoStoragePort;

    public GetCurrentTenantProfilePhotoUseCase(
            SecurityContextFacade securityContextFacade,
            TenantUserFaceLoginProfileRepository faceLoginProfileRepository,
            ProfilePhotoStoragePort profilePhotoStoragePort
    ) {
        this.securityContextFacade = securityContextFacade;
        this.faceLoginProfileRepository = faceLoginProfileRepository;
        this.profilePhotoStoragePort = profilePhotoStoragePort;
    }

    public CurrentTenantProfilePhotoResponse execute() {
        UUID currentUserId = parseCurrentUserId();
        String tenantSlug = securityContextFacade.getCurrentTenantSlug();

        TenantUserFaceLoginProfile profile = faceLoginProfileRepository.findByUserId(currentUserId)
                .filter(found -> found.profilePhotoUrl() != null && !found.profilePhotoUrl().isBlank())
                .orElseThrow(() -> new AuthenticationFailedException("Profile photo not found"));

        ProfilePhotoStoragePort.PhotoFile photoFile = profilePhotoStoragePort.read(
                tenantSlug,
                currentUserId,
                profile.profilePhotoContentType()
        );
        return new CurrentTenantProfilePhotoResponse(
                photoFile.bytes(),
                photoFile.contentType(),
                photoFile.filename()
        );
    }

    private UUID parseCurrentUserId() {
        String currentSubject = securityContextFacade.getCurrentSubject();
        if (currentSubject == null || currentSubject.isBlank()) {
            throw new AuthenticationFailedException("Authenticated subject is not available");
        }

        try {
            return UUID.fromString(currentSubject.trim());
        } catch (IllegalArgumentException exception) {
            throw new AuthenticationFailedException("Authenticated subject is not a valid user id");
        }
    }
}

```

### `auth/application/usecase/GetCurrentTenantProfileUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.identity.auth.application.dto.CurrentTenantProfileResponse;
import com.financesystem.finance_api.modules.identity.auth.domain.repository.TenantUserFaceLoginProfileRepository;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetCurrentTenantProfileUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final TenantUserRepository tenantUserRepository;
    private final TenantUserFaceLoginProfileRepository faceLoginProfileRepository;

    public GetCurrentTenantProfileUseCase(
            SecurityContextFacade securityContextFacade,
            TenantUserRepository tenantUserRepository,
            TenantUserFaceLoginProfileRepository faceLoginProfileRepository
    ) {
        this.securityContextFacade = securityContextFacade;
        this.tenantUserRepository = tenantUserRepository;
        this.faceLoginProfileRepository = faceLoginProfileRepository;
    }

    public CurrentTenantProfileResponse execute() {
        String currentSubject = securityContextFacade.getCurrentSubject();
        String currentTenantSlug = securityContextFacade.getCurrentTenantSlug();

        if (currentSubject == null || currentSubject.isBlank()) {
            throw new AuthenticationFailedException("Authenticated subject is not available");
        }

        TenantUser tenantUser = tenantUserRepository.findById(parseSubjectAsUserId(currentSubject))
                .orElseThrow(() -> new AuthenticationFailedException("Authenticated user not found"));

        var faceProfile = faceLoginProfileRepository.findByUserId(tenantUser.id()).orElse(null);

        return new CurrentTenantProfileResponse(
                tenantUser.id(),
                tenantUser.email(),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status().name(),
                currentTenantSlug,
                faceProfile != null && faceProfile.enabled(),
                faceProfile != null && faceProfile.profilePhotoUrl() != null && !faceProfile.profilePhotoUrl().isBlank(),
                faceProfile != null ? faceProfile.profilePhotoUrl() : null,
                faceProfile != null ? faceProfile.profilePhotoContentType() : null,
                tenantUser.createdAt(),
                tenantUser.updatedAt()
        );
    }

    private UUID parseSubjectAsUserId(String subject) {
        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new AuthenticationFailedException("Authenticated subject is not a valid user id");
        }
    }
}

```

### `auth/application/usecase/GetPublicTenantProfilePhotoUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.identity.auth.application.dto.CurrentTenantProfilePhotoResponse;
import com.financesystem.finance_api.modules.identity.auth.application.port.ProfilePhotoStoragePort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetPublicTenantProfilePhotoUseCase {

    private final ProfilePhotoStoragePort profilePhotoStoragePort;

    public GetPublicTenantProfilePhotoUseCase(ProfilePhotoStoragePort profilePhotoStoragePort) {
        this.profilePhotoStoragePort = profilePhotoStoragePort;
    }

    public CurrentTenantProfilePhotoResponse execute(String tenantSlug, UUID userId) {
        if (tenantSlug == null || tenantSlug.isBlank()) {
            throw new BusinessException("Tenant slug is required");
        }
        if (userId == null) {
            throw new BusinessException("User id is required");
        }

        ProfilePhotoStoragePort.PhotoFile photoFile = profilePhotoStoragePort.readPublic(tenantSlug, userId);
        return new CurrentTenantProfilePhotoResponse(
                photoFile.bytes(),
                photoFile.contentType(),
                photoFile.filename()
        );
    }
}

```

### `auth/application/usecase/LoginTenantUserUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.auth.application.dto.AuthTokenResponse;
import com.financesystem.finance_api.modules.identity.auth.application.dto.LoginRequest;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuditTrailService auditTrailService;

    public LoginTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.auditTrailService = auditTrailService;
    }

    public AuthTokenResponse execute(LoginRequest request) {
        TenantContext tenantContext = TenantContextHolder.getRequired();
        String normalizedEmail = request.email().trim().toLowerCase();

        TenantUser tenantUser = tenantUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        if (!tenantUser.active()) {
            throw new AuthenticationFailedException("User is inactive");
        }

        if (!passwordEncoder.matches(request.password(), tenantUser.passwordHash())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        List<String> roles = tenantUserRoleRepository.findRoleNamesByUserId(tenantUser.id()).stream()
                .filter(this::isAuthorizableRole)
                .toList();
        List<String> permissions = tenantUserRoleRepository.findPermissionCodesByUserId(tenantUser.id());
        String subject = tenantUser.id().toString();

        String accessToken = jwtTokenService.generateAccessToken(
                subject,
                tenantUser.email(),
                fullName(tenantUser.firstName(), tenantUser.lastName()),
                tenantContext.tenantSlug(),
                roles,
                permissions
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                subject,
                tenantContext.tenantSlug()
        );

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LOGIN,
                "USER",
                tenantUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "LOGIN",
                        "email", tenantUser.email(),
                        "tenantSlug", tenantContext.tenantSlug(),
                        "roles", roles,
                        "permissionCount", permissions.size()
                ),
                IdentityAuditPayloads.of(
                        "authenticated", false,
                        "tokenIssued", false
                ),
                IdentityAuditPayloads.of(
                        "authenticated", true,
                        "tokenIssued", true,
                        "roleCount", roles.size(),
                        "permissionCount", permissions.size()
                )
        );

        return new AuthTokenResponse(
                "Bearer",
                accessToken,
                refreshToken,
                jwtProperties.getAccessExpirationMs()
        );
    }

    private boolean isAuthorizableRole(String roleName) {
        if (roleName == null) {
            return false;
        }

        return "ADMIN".equalsIgnoreCase(roleName.trim())
                || "OWNER_ADMIN".equalsIgnoreCase(roleName.trim());
    }

    private String fullName(String firstName, String lastName) {
        StringBuilder builder = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            builder.append(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(lastName.trim());
        }
        String value = builder.toString().trim();
        return value.isEmpty() ? null : value;
    }
}

```

### `auth/application/usecase/LoginTenantUserWithFaceUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.identity.auth.application.config.FaceRecognitionProperties;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.auth.application.dto.AuthTokenResponse;
import com.financesystem.finance_api.modules.identity.auth.application.exception.FaceRecognitionUnavailableException;
import com.financesystem.finance_api.modules.identity.auth.application.port.FaceRecognitionPort;
import com.financesystem.finance_api.modules.identity.auth.application.port.ProfilePhotoStoragePort;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.identity.auth.domain.model.TenantUserFaceLoginProfile;
import com.financesystem.finance_api.modules.identity.auth.domain.repository.TenantUserFaceLoginProfileRepository;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Service
public class LoginTenantUserWithFaceUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final TenantUserFaceLoginProfileRepository faceLoginProfileRepository;
    private final FaceRecognitionPort faceRecognitionPort;
    private final ProfilePhotoStoragePort profilePhotoStoragePort;
    private final FaceRecognitionProperties faceRecognitionProperties;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuditTrailService auditTrailService;

    public LoginTenantUserWithFaceUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            TenantUserFaceLoginProfileRepository faceLoginProfileRepository,
            FaceRecognitionPort faceRecognitionPort,
            ProfilePhotoStoragePort profilePhotoStoragePort,
            FaceRecognitionProperties faceRecognitionProperties,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.faceLoginProfileRepository = faceLoginProfileRepository;
        this.faceRecognitionPort = faceRecognitionPort;
        this.profilePhotoStoragePort = profilePhotoStoragePort;
        this.faceRecognitionProperties = faceRecognitionProperties;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.auditTrailService = auditTrailService;
    }

    public AuthTokenResponse execute(String email, MultipartFile image) {
        if (!faceRecognitionPort.isConfigured()) {
            throw new FaceRecognitionUnavailableException("Face recognition is not configured", null);
        }

        TenantContext tenantContext = TenantContextHolder.getRequired();
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new AuthenticationFailedException("Email is required");
        }

        TenantUser tenantUser = tenantUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        if (!tenantUser.active()) {
            throw new AuthenticationFailedException("User is inactive");
        }

        TenantUserFaceLoginProfile faceProfile = faceLoginProfileRepository.findByUserId(tenantUser.id())
                .filter(TenantUserFaceLoginProfile::enabled)
                .orElseThrow(() -> new AuthenticationFailedException("Face login is not enabled for this user"));

        byte[] imageBytes = readBytes(image);
        FaceRecognitionPort.DetectedFace detectedFace = faceRecognitionPort.detectFace(
                imageBytes,
                image != null ? image.getOriginalFilename() : null,
                image != null ? image.getContentType() : null
        );

        FaceRecognitionPort.FaceComparisonResult comparison = compareWithStoredProfile(
                detectedFace,
                faceProfile,
                tenantContext.tenantSlug()
        );

        if (comparison.confidence() < faceRecognitionProperties.getConfidenceThreshold()) {
            throw new AuthenticationFailedException(formatFaceVerificationFailed(comparison.confidence()));
        }

        List<String> roles = tenantUserRoleRepository.findRoleNamesByUserId(tenantUser.id()).stream()
                .filter(this::isAuthorizableRole)
                .toList();
        List<String> permissions = tenantUserRoleRepository.findPermissionCodesByUserId(tenantUser.id());
        String subject = tenantUser.id().toString();

        String accessToken = jwtTokenService.generateAccessToken(
                subject,
                tenantUser.email(),
                fullName(tenantUser.firstName(), tenantUser.lastName()),
                tenantContext.tenantSlug(),
                roles,
                permissions
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                subject,
                tenantContext.tenantSlug()
        );

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LOGIN,
                "USER",
                tenantUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "FACE_LOGIN",
                        "email", tenantUser.email(),
                        "tenantSlug", tenantContext.tenantSlug(),
                        "roles", roles,
                        "permissionCount", permissions.size(),
                        "confidence", comparison.confidence(),
                        "confidenceThreshold", faceRecognitionProperties.getConfidenceThreshold()
                ),
                IdentityAuditPayloads.of(
                        "authenticated", false,
                        "tokenIssued", false
                ),
                IdentityAuditPayloads.of(
                        "authenticated", true,
                        "tokenIssued", true,
                        "roleCount", roles.size(),
                        "permissionCount", permissions.size()
                )
        );

        return new AuthTokenResponse(
                "Bearer",
                accessToken,
                refreshToken,
                jwtProperties.getAccessExpirationMs()
        );
    }

    private boolean isAuthorizableRole(String roleName) {
        if (roleName == null) {
            return false;
        }

        return "ADMIN".equalsIgnoreCase(roleName.trim())
                || "OWNER_ADMIN".equalsIgnoreCase(roleName.trim());
    }

    private byte[] readBytes(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new AuthenticationFailedException("Face image is required");
        }

        try {
            return image.getBytes();
        } catch (IOException exception) {
            throw new AuthenticationFailedException("Could not read the provided face image");
        }
    }

    private FaceRecognitionPort.FaceComparisonResult compareWithStoredProfile(
            FaceRecognitionPort.DetectedFace detectedFace,
            TenantUserFaceLoginProfile faceProfile,
            String tenantSlug
    ) {
        if (faceProfile.faceToken() != null && !faceProfile.faceToken().isBlank()) {
            return faceRecognitionPort.compareFaceTokens(detectedFace.faceToken(), faceProfile.faceToken());
        }

        if (faceProfile.profilePhotoUrl() == null || faceProfile.profilePhotoUrl().isBlank()) {
            throw new AuthenticationFailedException("Face login is not enabled for this user");
        }

        ProfilePhotoStoragePort.PhotoFile profilePhoto = profilePhotoStoragePort.read(
                tenantSlug,
                faceProfile.userId(),
                faceProfile.profilePhotoContentType()
        );

        FaceRecognitionPort.DetectedFace storedFace = faceRecognitionPort.detectFace(
                profilePhoto.bytes(),
                profilePhoto.filename(),
                profilePhoto.contentType()
        );
        return faceRecognitionPort.compareFaceTokens(detectedFace.faceToken(), storedFace.faceToken());
    }

    private String fullName(String firstName, String lastName) {
        StringBuilder builder = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            builder.append(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(lastName.trim());
        }
        String value = builder.toString().trim();
        return value.isEmpty() ? null : value;
    }

    private String formatFaceVerificationFailed(double confidence) {
        double threshold = faceRecognitionProperties.getConfidenceThreshold();
        return String.format(
                Locale.US,
                "Verificación facial fallida: confianza %.2f%% por debajo del mínimo requerido %.2f%%",
                confidence,
                threshold
        );
    }
}

```

### `auth/application/usecase/LogoutTenantUserUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import org.springframework.stereotype.Service;

@Service
public class LogoutTenantUserUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final AuditTrailService auditTrailService;

    public LogoutTenantUserUseCase(
            SecurityContextFacade securityContextFacade,
            AuditTrailService auditTrailService
    ) {
        this.securityContextFacade = securityContextFacade;
        this.auditTrailService = auditTrailService;
    }

    public void execute() {
        String currentSubject = securityContextFacade.getCurrentSubject();

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LOGOUT,
                "USER",
                currentSubject,
                IdentityAuditPayloads.of(
                        "operation", "LOGOUT",
                        "subject", currentSubject,
                        "tenantSlug", securityContextFacade.getCurrentTenantSlug()
                ),
                IdentityAuditPayloads.of(
                        "authenticated", true
                ),
                IdentityAuditPayloads.of(
                        "authenticated", false
                )
        );
    }
}

```

### `auth/application/usecase/RefreshTenantTokenUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.jwt.JwtClaimNames;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.auth.application.dto.AuthTokenResponse;
import com.financesystem.finance_api.modules.identity.auth.application.dto.RefreshTokenRequest;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RefreshTenantTokenUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuditTrailService auditTrailService;

    public RefreshTenantTokenUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.auditTrailService = auditTrailService;
    }

    public AuthTokenResponse execute(RefreshTokenRequest request) {
        TenantContext tenantContext = TenantContextHolder.getRequired();

        Claims claims = jwtTokenService.parseRefreshToken(request.refreshToken());

        String subject = claims.getSubject();
        String tokenTenant = claims.get(JwtClaimNames.TENANT, String.class);

        if (subject == null || subject.isBlank()) {
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        if (tokenTenant == null || tokenTenant.isBlank()) {
            throw new AuthenticationFailedException("Invalid refresh token tenant");
        }

        if (!tokenTenant.equalsIgnoreCase(tenantContext.tenantSlug())) {
            throw new AuthenticationFailedException("Refresh token tenant does not match current tenant");
        }

        UUID userId = parseSubjectAsUserId(subject);

        TenantUser tenantUser = tenantUserRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationFailedException("User not found for refresh token"));

        if (!tenantUser.active()) {
            throw new AuthenticationFailedException("User is inactive");
        }

        List<String> roles = tenantUserRoleRepository.findRoleNamesByUserId(tenantUser.id()).stream()
                .filter(this::isAuthorizableRole)
                .toList();
        List<String> permissions = tenantUserRoleRepository.findPermissionCodesByUserId(tenantUser.id());

        String newAccessToken = jwtTokenService.generateAccessToken(
                tenantUser.id().toString(),
                tenantUser.email(),
                fullName(tenantUser.firstName(), tenantUser.lastName()),
                tenantContext.tenantSlug(),
                roles,
                permissions
        );

        String newRefreshToken = jwtTokenService.generateRefreshToken(
                tenantUser.id().toString(),
                tenantContext.tenantSlug()
        );

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TOKEN_REFRESHED,
                "USER",
                tenantUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "REFRESH_TOKEN",
                        "email", tenantUser.email(),
                        "tenantSlug", tenantContext.tenantSlug(),
                        "roles", roles,
                        "permissionCount", permissions.size()
                ),
                IdentityAuditPayloads.of(
                        "refreshTokenValidated", true,
                        "tenantSlug", tenantContext.tenantSlug()
                ),
                IdentityAuditPayloads.of(
                        "accessTokenIssued", true,
                        "refreshTokenRotated", true,
                        "roleCount", roles.size(),
                        "permissionCount", permissions.size()
                )
        );

        return new AuthTokenResponse(
                "Bearer",
                newAccessToken,
                newRefreshToken,
                jwtProperties.getAccessExpirationMs()
        );
    }

    private boolean isAuthorizableRole(String roleName) {
        if (roleName == null) {
            return false;
        }

        return "ADMIN".equalsIgnoreCase(roleName.trim())
                || "OWNER_ADMIN".equalsIgnoreCase(roleName.trim());
    }

    private UUID parseSubjectAsUserId(String subject) {
        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new AuthenticationFailedException("Invalid refresh token subject");
        }
    }

    private String fullName(String firstName, String lastName) {
        StringBuilder builder = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            builder.append(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(lastName.trim());
        }
        String value = builder.toString().trim();
        return value.isEmpty() ? null : value;
    }
}

```

### `auth/application/usecase/ResetPasswordUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.notifications.application.dto.NotificationPublishRequest;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationCategory;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPriority;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationType;
import com.financesystem.finance_api.modules.governance.notifications.domain.port.NotificationPublisherPort;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.auth.application.dto.ResetPasswordRequest;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.InvalidPasswordResetTokenException;
import com.financesystem.finance_api.modules.identity.auth.domain.model.PasswordResetToken;
import com.financesystem.finance_api.modules.identity.auth.domain.repository.PasswordResetTokenRepository;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;

@Service
public class ResetPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordUseCase.class);

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final TenantUserRepository tenantUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditTrailService auditTrailService;
    private final NotificationPublisherPort notificationPublisherPort;
    private final ObjectMapper objectMapper;

    public ResetPasswordUseCase(
            PasswordResetTokenRepository passwordResetTokenRepository,
            TenantUserRepository tenantUserRepository,
            PasswordEncoder passwordEncoder,
            AuditTrailService auditTrailService,
            NotificationPublisherPort notificationPublisherPort,
            ObjectMapper objectMapper
    ) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.tenantUserRepository = tenantUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditTrailService = auditTrailService;
        this.notificationPublisherPort = notificationPublisherPort;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void execute(ResetPasswordRequest request) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.token().trim())
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Password reset token is invalid"));

        if (passwordResetToken.used()) {
            throw new InvalidPasswordResetTokenException("Password reset token has already been used");
        }

        if (passwordResetToken.expiresAt().isBefore(Instant.now())) {
            throw new InvalidPasswordResetTokenException("Password reset token has expired");
        }

        TenantUser tenantUser = tenantUserRepository.findByEmail(passwordResetToken.email())
                .orElseThrow(() -> new BusinessException("User associated with password reset token was not found"));

        if (!tenantUser.active()) {
            throw new BusinessException("User is inactive");
        }

        TenantUser updatedUser = new TenantUser(
                tenantUser.id(),
                tenantUser.email(),
                passwordEncoder.encode(request.newPassword()),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status(),
                tenantUser.createdAt(),
                tenantUser.updatedAt()
        );

        tenantUserRepository.save(updatedUser);

        Instant now = Instant.now();
        passwordResetTokenRepository.markUsed(passwordResetToken.token(), now);
        passwordResetTokenRepository.invalidateAllByEmail(passwordResetToken.email(), now);

        ObjectNode data = objectMapper.createObjectNode()
                .put("email", tenantUser.email())
                .put("status", "COMPLETED")
                .put("completedAt", now.toString());

        publishNotificationSafely(new NotificationPublishRequest(
                tenantUser.id(),
                NotificationType.PASSWORD_RESET_COMPLETED,
                NotificationCategory.SECURITY,
                NotificationPriority.HIGH,
                "Password reset completed",
                "Your password was reset successfully.",
                data,
                null,
                "/security/password-reset",
                now.plusSeconds(3600)
        ));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.PASSWORD_RESET_COMPLETED,
                "USER",
                tenantUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "RESET_PASSWORD",
                        "email", tenantUser.email()
                ),
                IdentityAuditPayloads.of(
                        "resetTokenState", "ISSUED",
                        "used", false
                ),
                IdentityAuditPayloads.of(
                        "resetTokenState", "USED",
                        "used", true,
                        "completedAt", now.toString()
                )
        );
    }

    private void publishNotificationSafely(NotificationPublishRequest request) {
        try {
            notificationPublisherPort.publish(request);
        } catch (Exception exception) {
            log.warn("Unable to publish password reset completion notification: {}", exception.getMessage());
        }
    }
}

```

### `auth/application/usecase/UpdateCurrentTenantProfileUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.auth.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.identity.auth.application.dto.CurrentTenantProfileResponse;
import com.financesystem.finance_api.modules.identity.auth.application.dto.UpdateCurrentTenantProfileRequest;
import com.financesystem.finance_api.modules.identity.auth.application.port.FaceRecognitionPort;
import com.financesystem.finance_api.modules.identity.auth.application.port.ProfilePhotoStoragePort;
import com.financesystem.finance_api.modules.identity.auth.domain.model.TenantUserFaceLoginProfile;
import com.financesystem.finance_api.modules.identity.auth.domain.repository.TenantUserFaceLoginProfileRepository;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class UpdateCurrentTenantProfileUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final TenantUserRepository tenantUserRepository;
    private final TenantUserFaceLoginProfileRepository faceLoginProfileRepository;
    private final FaceRecognitionPort faceRecognitionPort;
    private final ProfilePhotoStoragePort profilePhotoStoragePort;

    public UpdateCurrentTenantProfileUseCase(
            SecurityContextFacade securityContextFacade,
            TenantUserRepository tenantUserRepository,
            TenantUserFaceLoginProfileRepository faceLoginProfileRepository,
            FaceRecognitionPort faceRecognitionPort,
            ProfilePhotoStoragePort profilePhotoStoragePort
    ) {
        this.securityContextFacade = securityContextFacade;
        this.tenantUserRepository = tenantUserRepository;
        this.faceLoginProfileRepository = faceLoginProfileRepository;
        this.faceRecognitionPort = faceRecognitionPort;
        this.profilePhotoStoragePort = profilePhotoStoragePort;
    }

    @Transactional
    public CurrentTenantProfileResponse execute(UpdateCurrentTenantProfileRequest request, MultipartFile photo) {
        UUID currentUserId = parseCurrentUserId();

        if ((request.firstName() == null || request.firstName().isBlank())
                && (request.lastName() == null || request.lastName().isBlank())
                && (photo == null || photo.isEmpty())) {
            throw new BusinessException("No profile fields were provided");
        }

        TenantUser existingUser = tenantUserRepository.findById(currentUserId)
                .orElseThrow(() -> new TenantUserNotFoundException("Authenticated user not found"));

        TenantUser updatedUser = new TenantUser(
                existingUser.id(),
                existingUser.email(),
                existingUser.passwordHash(),
                normalizeOptional(request.firstName(), existingUser.firstName()),
                normalizeOptional(request.lastName(), existingUser.lastName()),
                existingUser.active(),
                existingUser.status(),
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        tenantUserRepository.save(updatedUser);

        if (photo != null && !photo.isEmpty()) {
            updateFaceProfile(currentUserId, photo);
        }

        return toResponse(currentUserId);
    }

    @Transactional
    public CurrentTenantProfileResponse updatePhoto(MultipartFile photo) {
        UUID currentUserId = parseCurrentUserId();
        updateFaceProfile(currentUserId, photo);
        return toResponse(currentUserId);
    }

    @Transactional
    public CurrentTenantProfileResponse removePhoto() {
        UUID currentUserId = parseCurrentUserId();
        String tenantSlug = currentTenantSlug();
        faceLoginProfileRepository.findByUserId(currentUserId).ifPresent(profile -> {
            if (profile.profilePhotoUrl() != null && !profile.profilePhotoUrl().isBlank()) {
                profilePhotoStoragePort.delete(tenantSlug, currentUserId);
            }
            faceLoginProfileRepository.save(new TenantUserFaceLoginProfile(
                    profile.userId(),
                    profile.faceToken(),
                    profile.faceId(),
                    null,
                    null,
                    false,
                    profile.enrolledAt(),
                    profile.updatedAt()
            ));
        });
        return toResponse(currentUserId);
    }

    private void updateFaceProfile(UUID userId, MultipartFile photo) {
        byte[] bytes = readBytes(photo);
        String contentType = photo.getContentType();
        String tenantSlug = currentTenantSlug();

        String faceToken = null;
        String faceId = null;
        try {
            FaceRecognitionPort.DetectedFace detectedFace = faceRecognitionPort.detectFace(
                    bytes,
                    photo != null ? photo.getOriginalFilename() : null,
                    contentType
            );
            faceToken = detectedFace.faceToken();
            faceId = detectedFace.faceId();
        } catch (RuntimeException exception) {
            // Save the photo anyway; Face++ can be retried later without losing the profile image.
        }

        ProfilePhotoStoragePort.StoredProfilePhoto storedPhoto = profilePhotoStoragePort.store(
                tenantSlug,
                userId,
                bytes,
                contentType
        );

        faceLoginProfileRepository.save(new TenantUserFaceLoginProfile(
                userId,
                faceToken,
                faceId,
                storedPhoto.url(),
                contentType,
                true,
                null,
                null
        ));
    }

    private CurrentTenantProfileResponse toResponse(UUID currentUserId) {
        TenantUser tenantUser = tenantUserRepository.findById(currentUserId)
                .orElseThrow(() -> new TenantUserNotFoundException("Authenticated user not found"));
        var faceProfile = faceLoginProfileRepository.findByUserId(currentUserId).orElse(null);
        return new CurrentTenantProfileResponse(
                tenantUser.id(),
                tenantUser.email(),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status().name(),
                securityContextFacade.getCurrentTenantSlug(),
                faceProfile != null && faceProfile.enabled() && faceProfile.faceToken() != null && !faceProfile.faceToken().isBlank(),
                faceProfile != null && faceProfile.profilePhotoUrl() != null && !faceProfile.profilePhotoUrl().isBlank(),
                faceProfile != null ? faceProfile.profilePhotoUrl() : null,
                faceProfile != null ? faceProfile.profilePhotoContentType() : null,
                tenantUser.createdAt(),
                tenantUser.updatedAt()
        );
    }

    private UUID parseCurrentUserId() {
        String currentSubject = securityContextFacade.getCurrentSubject();
        if (currentSubject == null || currentSubject.isBlank()) {
            throw new BusinessException("Authenticated subject is not available");
        }

        try {
            return UUID.fromString(currentSubject.trim());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Authenticated subject is not a valid user id");
        }
    }

    private byte[] readBytes(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new BusinessException("La foto de perfil es obligatoria");
        }

        try {
            return photo.getBytes();
        } catch (IOException exception) {
            throw new BusinessException("No se pudo leer la foto de perfil");
        }
    }

    private String normalizeOptional(String value, String fallback) {
        if (value == null) {
            return fallback;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }

    private String currentTenantSlug() {
        String tenantSlug = securityContextFacade.getCurrentTenantSlug();
        if (tenantSlug == null || tenantSlug.isBlank()) {
            throw new BusinessException("Tenant slug is not available");
        }
        return tenantSlug;
    }
}

```

### `auth/domain/exception/AuthenticationFailedException.java`

```java
package com.financesystem.finance_api.modules.identity.auth.domain.exception;

public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
```

### `auth/domain/exception/InvalidPasswordResetTokenException.java`

```java
package com.financesystem.finance_api.modules.identity.auth.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class InvalidPasswordResetTokenException extends BusinessException {

    public InvalidPasswordResetTokenException(String message) {
        super(message);
    }
}
```

### `auth/domain/model/PasswordResetToken.java`

```java
package com.financesystem.finance_api.modules.identity.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PasswordResetToken(
        UUID id,
        String email,
        String token,
        Instant expiresAt,
        boolean used,
        Instant usedAt,
        Instant createdAt
) {
}
```

### `auth/domain/model/TenantUserFaceLoginProfile.java`

```java
package com.financesystem.finance_api.modules.identity.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TenantUserFaceLoginProfile(
        UUID userId,
        String faceToken,
        String faceId,
        String profilePhotoUrl,
        String profilePhotoContentType,
        boolean enabled,
        Instant enrolledAt,
        Instant updatedAt
) {
}

```

### `auth/domain/repository/PasswordResetTokenRepository.java`

```java
package com.financesystem.finance_api.modules.identity.auth.domain.repository;

import com.financesystem.finance_api.modules.identity.auth.domain.model.PasswordResetToken;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository {

    PasswordResetToken save(PasswordResetToken passwordResetToken);

    Optional<PasswordResetToken> findByToken(String token);

    void invalidateAllByEmail(String email, Instant usedAt);

    void markUsed(String token, Instant usedAt);
}
```

### `auth/domain/repository/TenantUserFaceLoginProfileRepository.java`

```java
package com.financesystem.finance_api.modules.identity.auth.domain.repository;

import com.financesystem.finance_api.modules.identity.auth.domain.model.TenantUserFaceLoginProfile;

import java.util.Optional;
import java.util.UUID;

public interface TenantUserFaceLoginProfileRepository {

    TenantUserFaceLoginProfile save(TenantUserFaceLoginProfile profile);

    Optional<TenantUserFaceLoginProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}

```

### `auth/infrastructure/api/AuthController.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.identity.auth.application.dto.*;
import com.financesystem.finance_api.modules.identity.auth.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginTenantUserUseCase loginTenantUserUseCase;
    private final RefreshTenantTokenUseCase refreshTenantTokenUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final GetCurrentAuthenticatedTenantUserUseCase getCurrentAuthenticatedTenantUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final LogoutTenantUserUseCase logoutTenantUserUseCase;
    private final LoginTenantUserWithFaceUseCase loginTenantUserWithFaceUseCase;
    private final GetCurrentTenantProfileUseCase getCurrentTenantProfileUseCase;
    private final UpdateCurrentTenantProfileUseCase updateCurrentTenantProfileUseCase;
    private final GetCurrentTenantProfilePhotoUseCase getCurrentTenantProfilePhotoUseCase;

    public AuthController(
            LoginTenantUserUseCase loginTenantUserUseCase,
            RefreshTenantTokenUseCase refreshTenantTokenUseCase,
            ForgotPasswordUseCase forgotPasswordUseCase,
            ResetPasswordUseCase resetPasswordUseCase,
            GetCurrentAuthenticatedTenantUserUseCase getCurrentAuthenticatedTenantUserUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            LogoutTenantUserUseCase logoutTenantUserUseCase,
            LoginTenantUserWithFaceUseCase loginTenantUserWithFaceUseCase,
            GetCurrentTenantProfileUseCase getCurrentTenantProfileUseCase,
            UpdateCurrentTenantProfileUseCase updateCurrentTenantProfileUseCase,
            GetCurrentTenantProfilePhotoUseCase getCurrentTenantProfilePhotoUseCase
    ) {
        this.loginTenantUserUseCase = loginTenantUserUseCase;
        this.refreshTenantTokenUseCase = refreshTenantTokenUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.getCurrentAuthenticatedTenantUserUseCase = getCurrentAuthenticatedTenantUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.logoutTenantUserUseCase = logoutTenantUserUseCase;
        this.loginTenantUserWithFaceUseCase = loginTenantUserWithFaceUseCase;
        this.getCurrentTenantProfileUseCase = getCurrentTenantProfileUseCase;
        this.updateCurrentTenantProfileUseCase = updateCurrentTenantProfileUseCase;
        this.getCurrentTenantProfilePhotoUseCase = getCurrentTenantProfilePhotoUseCase;
    }

    @PostMapping("/login")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(
                "Login successful",
                loginTenantUserUseCase.execute(request)
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(
                "Token refreshed successfully",
                refreshTenantTokenUseCase.execute(request)
        );
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.execute(request);

        return ApiResponse.success(
                "If the user exists, a password reset email has been sent",
                Map.of("status", "ok")
        );
    }

    @PostMapping("/reset-password")
    public ApiResponse<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.execute(request);

        return ApiResponse.success(
                "Password reset successfully",
                Map.of("status", "ok")
        );
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Map<String, String>> logout() {
        logoutTenantUserUseCase.execute();

        return ApiResponse.success(
                "Logout successful",
                Map.of("status", "ok")
        );
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<AuthenticatedTenantUserResponse> me() {
        return ApiResponse.success(
                "Authenticated user retrieved successfully",
                getCurrentAuthenticatedTenantUserUseCase.execute()
        );
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<CurrentTenantProfileResponse> profile() {
        return ApiResponse.success(
                "Current profile retrieved successfully",
                getCurrentTenantProfileUseCase.execute()
        );
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<CurrentTenantProfileResponse> updateProfile(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        UpdateCurrentTenantProfileRequest request = new UpdateCurrentTenantProfileRequest(firstName, lastName);
        return ApiResponse.success(
                "Profile updated successfully",
                updateCurrentTenantProfileUseCase.execute(request, photo)
        );
    }

    @PutMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<CurrentTenantProfileResponse> updateProfilePhoto(
            @RequestPart("photo") MultipartFile photo
    ) {
        return ApiResponse.success(
                "Profile photo updated successfully",
                updateCurrentTenantProfileUseCase.updatePhoto(photo)
        );
    }

    @GetMapping("/profile/photo")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<byte[]> profilePhoto() {
        CurrentTenantProfilePhotoResponse photo = getCurrentTenantProfilePhotoUseCase.execute();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + (photo.filename() == null ? "profile-photo" : photo.filename()) + "\"")
                .contentType(MediaType.parseMediaType(
                        photo.contentType() == null || photo.contentType().isBlank()
                                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                                : photo.contentType()
                ))
                .body(photo.bytes());
    }

    @DeleteMapping("/profile/photo")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<CurrentTenantProfileResponse> removeProfilePhoto() {
        return ApiResponse.success(
                "Profile photo removed successfully",
                updateCurrentTenantProfileUseCase.removePhoto()
        );
    }

    @PostMapping(value = "/face/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AuthTokenResponse> faceLogin(
            @RequestParam("email") String email,
            @RequestPart("image") MultipartFile image
    ) {
        return ApiResponse.success(
                "Face login successful",
                loginTenantUserWithFaceUseCase.execute(email, image)
        );
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        changePasswordUseCase.execute(request);

        return ApiResponse.success(
                "Password changed successfully",
                Map.of("status", "ok")
        );
    }
}

```

### `auth/infrastructure/api/PublicAuthController.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.api;

import com.financesystem.finance_api.modules.identity.auth.application.dto.CurrentTenantProfilePhotoResponse;
import com.financesystem.finance_api.modules.identity.auth.application.usecase.GetPublicTenantProfilePhotoUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/auth")
public class PublicAuthController {

    private final GetPublicTenantProfilePhotoUseCase getPublicTenantProfilePhotoUseCase;

    public PublicAuthController(GetPublicTenantProfilePhotoUseCase getPublicTenantProfilePhotoUseCase) {
        this.getPublicTenantProfilePhotoUseCase = getPublicTenantProfilePhotoUseCase;
    }

    @GetMapping({"/profile/photo/{tenantSlug}/{userId}", "/profile/photo/{tenantSlug}/{userId}.{extension}"})
    public ResponseEntity<byte[]> profilePhoto(
            @PathVariable String tenantSlug,
            @PathVariable String userId,
            @PathVariable(value = "extension", required = false) String extension
    ) {
        CurrentTenantProfilePhotoResponse photo = getPublicTenantProfilePhotoUseCase.execute(tenantSlug, parseUuid(userId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + (photo.filename() == null ? "profile-photo" : photo.filename()) + "\"")
                .contentType(MediaType.parseMediaType(
                        photo.contentType() == null || photo.contentType().isBlank()
                                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                                : photo.contentType()
                ))
                .body(photo.bytes());
    }

    private UUID parseUuid(String raw) {
        String value = raw == null ? null : raw.trim();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }

        int dotIndex = value.indexOf('.');
        if (dotIndex > 0) {
            value = value.substring(0, dotIndex);
        }

        return UUID.fromString(value);
    }
}

```

### `auth/infrastructure/face/FacePlusPlusFaceRecognitionAdapter.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.face;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.identity.auth.application.config.FaceRecognitionProperties;
import com.financesystem.finance_api.modules.identity.auth.application.exception.FaceRecognitionUnavailableException;
import com.financesystem.finance_api.modules.identity.auth.application.port.FaceRecognitionPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Component
public class FacePlusPlusFaceRecognitionAdapter implements FaceRecognitionPort {

    private static final Semaphore FACEPP_GATE = new Semaphore(1);

    private final FaceRecognitionProperties properties;
    private final RestClient restClient;

    public FacePlusPlusFaceRecognitionAdapter(FaceRecognitionProperties properties) {
        this.properties = properties;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getTimeoutMs());
        factory.setReadTimeout(properties.getTimeoutMs());

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @Override
    public DetectedFace detectFace(byte[] imageBytes, String filename, String contentType) {
        ensureConfigured();
        Map<String, Object> response = postForm("/detect", buildDetectForm(imageBytes, filename, contentType));
        validateFacePlusPlusResponse(response);

        Object facesValue = response.get("faces");
        if (!(facesValue instanceof List<?> faces) || faces.isEmpty()) {
            throw new BusinessException("No se detectó un rostro válido en la imagen");
        }
        if (faces.size() != 1) {
            throw new BusinessException("La imagen debe contener un solo rostro");
        }

        Object firstFace = faces.get(0);
        if (!(firstFace instanceof Map<?, ?> faceMap)) {
            throw new BusinessException("Respuesta de Face++ inválida");
        }

        String faceToken = stringValue(faceMap.get("face_token"));
        String faceId = stringValue(faceMap.get("face_id"));

        if (!StringUtils.hasText(faceToken)) {
            throw new BusinessException("Face++ no devolvió un token de rostro válido");
        }

        return new DetectedFace(faceToken, faceId);
    }

    @Override
    public FaceComparisonResult compareFaceTokens(String faceToken1, String faceToken2) {
        ensureConfigured();
        Map<String, Object> response = postForm("/compare", buildCompareForm(faceToken1, faceToken2));
        validateFacePlusPlusResponse(response);

        Number confidence = numberValue(response.get("confidence"));
        if (confidence == null) {
            throw new BusinessException("Face++ no devolvió la confianza de comparación");
        }

        return new FaceComparisonResult(confidence.doubleValue());
    }

    @Override
    public boolean isConfigured() {
        return properties.isConfigured();
    }

    private void ensureConfigured() {
        if (!properties.isConfigured()) {
            throw new FaceRecognitionUnavailableException("Face recognition is not configured", null);
        }
    }

    private Map<String, Object> postForm(String path, MultiValueMap<String, Object> form) {
        FaceRecognitionUnavailableException lastUnavailable = null;
        boolean acquired = false;

        try {
            acquired = FACEPP_GATE.tryAcquire(30, TimeUnit.SECONDS);
            if (!acquired) {
                throw new FaceRecognitionUnavailableException(
                        "Face++ is busy, please retry",
                        null
                );
            }

            for (String baseUrl : List.of(properties.getBaseUrl(), properties.getFallbackBaseUrl())) {
                if (!StringUtils.hasText(baseUrl)) {
                    continue;
                }

                for (int attempt = 1; attempt <= 2; attempt++) {
                    try {
                        RestClient client = restClient.mutate().baseUrl(baseUrl).build();
                        ResponseEntity<Map> response = client.post()
                                .uri(path)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .body(form)
                                .retrieve()
                                .onStatus(
                                        status -> status.is4xxClientError() || status.is5xxServerError(),
                                        (request, response1) -> {
                                            throw new FaceRecognitionUnavailableException(
                                                    describeFacePlusPlusError(baseUrl, path, response1),
                                                    null
                                            );
                                        }
                                )
                                .toEntity(Map.class);

                        @SuppressWarnings("unchecked")
                        Map<String, Object> body = (Map<String, Object>) response.getBody();
                        if (body == null) {
                            throw new FaceRecognitionUnavailableException(
                                    "Face++ devolvió una respuesta vacía desde " + baseUrl + path,
                                    null
                            );
                        }

                        return body;
                    } catch (FaceRecognitionUnavailableException exception) {
                        lastUnavailable = exception;
                        if (!isConcurrencyLimit(exception) || attempt == 2) {
                            break;
                        }
                        sleepQuietly(500L);
                    } catch (Exception exception) {
                        lastUnavailable = new FaceRecognitionUnavailableException(
                                "No fue posible contactar Face++ en " + baseUrl + path + ": " + rootMessage(exception),
                                exception
                        );
                        break;
                    }
                }
            }

            if (lastUnavailable != null) {
                throw lastUnavailable;
            }
            throw new FaceRecognitionUnavailableException("Face++ is unavailable", null);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new FaceRecognitionUnavailableException("Face++ request was interrupted", exception);
        } finally {
            if (acquired) {
                FACEPP_GATE.release();
            }
        }
    }

    private ByteArrayResource byteArrayResource(byte[] bytes, String filename) {
        return new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return StringUtils.hasText(filename) ? filename : "face.jpg";
            }
        };
    }

    private MultiValueMap<String, Object> buildDetectForm(byte[] imageBytes, String filename, String contentType) {
        ByteArrayResource file = byteArrayResource(imageBytes, filename);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(parseMediaType(contentType, "image/jpeg"));

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("api_key", properties.getApiKey());
        form.add("api_secret", properties.getApiSecret());
        form.add("return_face_id", "1");
        form.add("return_landmark", "0");
        form.add("image_file", new HttpEntity<>(file, headers));
        return form;
    }

    private MultiValueMap<String, Object> buildCompareForm(String faceToken1, String faceToken2) {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("api_key", properties.getApiKey());
        form.add("api_secret", properties.getApiSecret());
        form.add("face_token1", faceToken1);
        form.add("face_token2", faceToken2);
        return form;
    }

    private MediaType parseMediaType(String contentType, String fallback) {
        String value = StringUtils.hasText(contentType) ? contentType : fallback;

        try {
            MediaType parsed = MediaType.parseMediaType(value);
            return new MediaType(parsed.getType(), parsed.getSubtype());
        } catch (Exception exception) {
            return MediaType.parseMediaType(fallback);
        }
    }

    private void validateFacePlusPlusResponse(Map<String, Object> response) {
        if (response == null) {
            throw new BusinessException("Face++ devolvió una respuesta vacía");
        }

        Object errorMessage = response.get("error_message");
        if (errorMessage != null && StringUtils.hasText(String.valueOf(errorMessage))) {
            throw new BusinessException(String.valueOf(errorMessage));
        }
    }

    private String describeFacePlusPlusError(String baseUrl, String path, ClientHttpResponse response) {
        String status = "unknown";
        String body = "";

        try {
            status = String.valueOf(response.getStatusCode());
        } catch (IOException ignored) {
        }

        try {
            body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }

        if (!StringUtils.hasText(body)) {
            return "Face++ respondió con estado " + status + " en " + baseUrl + path;
        }

        return "Face++ respondió con estado " + status + " en " + baseUrl + path + ": " + body;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String rootMessage(Exception exception) {
        Throwable current = exception;
        String last = exception.getMessage();
        while (current != null) {
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                last = current.getMessage();
            }
            current = current.getCause();
        }
        return last == null || last.isBlank() ? "unknown error" : last;
    }

    private Number numberValue(Object value) {
        if (value instanceof Number number) {
            return number;
        }
        if (value != null) {
            try {
                return Double.parseDouble(String.valueOf(value));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private boolean isConcurrencyLimit(FaceRecognitionUnavailableException exception) {
        String message = exception.getMessage();
        return message != null && message.contains("CONCURRENCY_LIMIT_EXCEEDED");
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}

```

### `auth/infrastructure/persistence/PasswordResetTokenRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.auth.domain.model.PasswordResetToken;
import com.financesystem.finance_api.modules.identity.auth.domain.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final TenantPasswordResetTokenJpaRepository jpaRepository;

    public PasswordResetTokenRepositoryAdapter(TenantPasswordResetTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        TenantPasswordResetTokenEntity entity = toEntity(passwordResetToken);
        TenantPasswordResetTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public void invalidateAllByEmail(String email, Instant usedAt) {
        jpaRepository.invalidateAllByEmail(email, usedAt);
    }

    @Override
    public void markUsed(String token, Instant usedAt) {
        jpaRepository.markUsed(token, usedAt);
    }

    private TenantPasswordResetTokenEntity toEntity(PasswordResetToken passwordResetToken) {
        TenantPasswordResetTokenEntity entity = new TenantPasswordResetTokenEntity();
        entity.setId(passwordResetToken.id());
        entity.setEmail(passwordResetToken.email());
        entity.setToken(passwordResetToken.token());
        entity.setExpiresAt(passwordResetToken.expiresAt());
        entity.setUsed(passwordResetToken.used());
        entity.setUsedAt(passwordResetToken.usedAt());
        return entity;
    }

    private PasswordResetToken toDomain(TenantPasswordResetTokenEntity entity) {
        return new PasswordResetToken(
                entity.getId(),
                entity.getEmail(),
                entity.getToken(),
                entity.getExpiresAt(),
                entity.isUsed(),
                entity.getUsedAt(),
                entity.getCreatedAt()
        );
    }
}
```

### `auth/infrastructure/persistence/TenantPasswordResetTokenEntity.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_password_reset_tokens")
public class TenantPasswordResetTokenEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column
    private Instant usedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }    public Instant getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Instant usedAt) {
        this.usedAt = usedAt;
    }    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

### `auth/infrastructure/persistence/TenantPasswordResetTokenJpaRepository.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.persistence;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface TenantPasswordResetTokenJpaRepository extends JpaRepository<TenantPasswordResetTokenEntity, UUID> {

    Optional<TenantPasswordResetTokenEntity> findByToken(String token);

    @Modifying
    @Transactional
    @Query("""
            update TenantPasswordResetTokenEntity t
               set t.used = true,
                   t.usedAt = :usedAt
             where t.email = :email
               and t.used = false
            """)
    void invalidateAllByEmail(String email, Instant usedAt);

    @Modifying
    @Transactional
    @Query("""
            update TenantPasswordResetTokenEntity t
               set t.used = true,
                   t.usedAt = :usedAt
             where t.token = :token
               and t.used = false
            """)
    void markUsed(String token, Instant usedAt);
}
```

### `auth/infrastructure/persistence/TenantUserFaceLoginProfileEntity.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_user_face_login_profiles")
public class TenantUserFaceLoginProfileEntity {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "face_token", length = 255)
    private String faceToken;

    @Column(name = "face_id", length = 255)
    private String faceId;

    @Column(name = "profile_photo_url", length = 512)
    private String profilePhotoUrl;

    @Column(name = "profile_photo_content_type", length = 100)
    private String profilePhotoContentType;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private Instant enrolledAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @jakarta.persistence.PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (enrolledAt == null) {
            enrolledAt = now;
        }
        updatedAt = now;
        enabled = true;
    }

    @jakarta.persistence.PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getProfilePhotoContentType() {
        return profilePhotoContentType;
    }

    public void setProfilePhotoContentType(String profilePhotoContentType) {
        this.profilePhotoContentType = profilePhotoContentType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Instant enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

```

### `auth/infrastructure/persistence/TenantUserFaceLoginProfileJpaRepository.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantUserFaceLoginProfileJpaRepository extends JpaRepository<TenantUserFaceLoginProfileEntity, UUID> {
}

```

### `auth/infrastructure/persistence/TenantUserFaceLoginProfileRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.auth.domain.model.TenantUserFaceLoginProfile;
import com.financesystem.finance_api.modules.identity.auth.domain.repository.TenantUserFaceLoginProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TenantUserFaceLoginProfileRepositoryAdapter implements TenantUserFaceLoginProfileRepository {

    private final TenantUserFaceLoginProfileJpaRepository jpaRepository;

    public TenantUserFaceLoginProfileRepositoryAdapter(
            TenantUserFaceLoginProfileJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TenantUserFaceLoginProfile save(TenantUserFaceLoginProfile profile) {
        TenantUserFaceLoginProfileEntity entity = toEntity(profile);
        TenantUserFaceLoginProfileEntity saved = jpaRepository.saveAndFlush(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TenantUserFaceLoginProfile> findByUserId(UUID userId) {
        return jpaRepository.findById(userId).map(this::toDomain);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.existsById(userId);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteById(userId);
    }

    private TenantUserFaceLoginProfileEntity toEntity(TenantUserFaceLoginProfile profile) {
        TenantUserFaceLoginProfileEntity entity = new TenantUserFaceLoginProfileEntity();
        entity.setUserId(profile.userId());
        entity.setFaceToken(profile.faceToken());
        entity.setFaceId(profile.faceId());
        entity.setProfilePhotoUrl(profile.profilePhotoUrl());
        entity.setProfilePhotoContentType(profile.profilePhotoContentType());
        entity.setEnabled(profile.enabled());
        entity.setEnrolledAt(profile.enrolledAt());
        entity.setUpdatedAt(profile.updatedAt());
        return entity;
    }

    private TenantUserFaceLoginProfile toDomain(TenantUserFaceLoginProfileEntity entity) {
        return new TenantUserFaceLoginProfile(
                entity.getUserId(),
                entity.getFaceToken(),
                entity.getFaceId(),
                entity.getProfilePhotoUrl(),
                entity.getProfilePhotoContentType(),
                entity.isEnabled(),
                entity.getEnrolledAt(),
                entity.getUpdatedAt()
        );
    }
}

```

### `auth/infrastructure/storage/LocalProfilePhotoStorageAdapter.java`

```java
package com.financesystem.finance_api.modules.identity.auth.infrastructure.storage;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.identity.auth.application.config.ProfilePhotoStorageProperties;
import com.financesystem.finance_api.modules.identity.auth.application.port.ProfilePhotoStoragePort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class LocalProfilePhotoStorageAdapter implements ProfilePhotoStoragePort {

    private static final String PUBLIC_URL_PREFIX = "/api/public/auth/profile/photo";
    private final ProfilePhotoStorageProperties properties;

    public LocalProfilePhotoStorageAdapter(ProfilePhotoStorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public StoredProfilePhoto store(String tenantSlug, UUID userId, byte[] bytes, String contentType) {
        if (!properties.isConfigured()) {
            throw new BusinessException("Profile photo storage is not configured");
        }
        if (!StringUtils.hasText(tenantSlug) || userId == null) {
            throw new BusinessException("Tenant slug and user id are required");
        }
        if (bytes == null || bytes.length == 0) {
            throw new BusinessException("Photo bytes are required");
        }

        Path userDir = resolveUserDir(tenantSlug, userId);
        try {
            deleteDirectoryContents(userDir);
            Files.createDirectories(userDir);
            Files.write(userDir.resolve(photoFilename(contentType)), bytes);
        } catch (IOException exception) {
            throw new BusinessException("No se pudo guardar la foto de perfil");
        }

        return new StoredProfilePhoto(publicUrl(tenantSlug, userId, contentType));
    }

    @Override
    public PhotoFile read(String tenantSlug, UUID userId, String contentType) {
        if (!properties.isConfigured()) {
            throw new BusinessException("Profile photo storage is not configured");
        }
        Path file = resolveUserDir(tenantSlug, userId).resolve(photoFilename(contentType));
        if (!Files.exists(file)) {
            throw new BusinessException("Profile photo not found");
        }

        try {
            return new PhotoFile(Files.readAllBytes(file), normalizedContentType(contentType), photoFilename(contentType));
        } catch (IOException exception) {
            throw new BusinessException("No se pudo leer la foto de perfil");
        }
    }

    @Override
    public PhotoFile readPublic(String tenantSlug, UUID userId) {
        if (!properties.isConfigured()) {
            throw new BusinessException("Profile photo storage is not configured");
        }

        Path userDir = resolveUserDir(tenantSlug, userId);
        if (!Files.exists(userDir)) {
            throw new BusinessException("Profile photo not found");
        }

        try (Stream<Path> stream = Files.list(userDir)) {
            Path file = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("profile."))
                    .min(Comparator.comparing(Path::toString))
                    .orElseThrow(() -> new BusinessException("Profile photo not found"));

            String filename = file.getFileName().toString();
            return new PhotoFile(
                    Files.readAllBytes(file),
                    contentTypeFromFilename(filename),
                    filename
            );
        } catch (IOException exception) {
            throw new BusinessException("No se pudo leer la foto de perfil");
        }
    }

    @Override
    public void delete(String tenantSlug, UUID userId) {
        if (!properties.isConfigured()) {
            return;
        }
        Path userDir = resolveUserDir(tenantSlug, userId);
        try {
            deleteDirectoryContents(userDir);
        } catch (IOException exception) {
            throw new BusinessException("No se pudo eliminar la foto de perfil");
        }
    }

    private Path resolveUserDir(String tenantSlug, UUID userId) {
        return Paths.get(properties.getRootDir(), safeSegment(tenantSlug), userId.toString());
    }

    private void deleteDirectoryContents(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (var stream = Files.walk(directory)) {
            stream.sorted((left, right) -> right.compareTo(left))
                    .filter(path -> !path.equals(directory))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    });
        } catch (RuntimeException exception) {
            if (exception.getCause() instanceof IOException ioException) {
                throw ioException;
            }
            throw exception;
        }
    }

    private String publicUrl(String tenantSlug, UUID userId, String contentType) {
        return PUBLIC_URL_PREFIX + "/" + safeSegment(tenantSlug) + "/" + userId + extensionFor(normalizedContentType(contentType));
    }

    private String photoFilename(String contentType) {
        String normalized = normalizedContentType(contentType);
        return switch (normalized) {
            case "image/jpeg" -> "profile.jpg";
            case "image/png" -> "profile.png";
            case "image/webp" -> "profile.webp";
            case "image/gif" -> "profile.gif";
            default -> "profile.bin";
        };
    }

    private String normalizedContentType(String contentType) {
        return StringUtils.hasText(contentType) ? contentType.split(";")[0].trim().toLowerCase() : "application/octet-stream";
    }

    private String safeSegment(String value) {
        return value == null ? "unknown" : value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> "";
        };
    }

    private String contentTypeFromFilename(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }
}

```

### `users/application/dto/CreateTenantUserRequest.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantUserRequest(
        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName
) {
}
```

### `users/application/dto/TenantUserResponse.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.dto;

import java.time.Instant;
import java.util.UUID;

public record TenantUserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean active,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
```

### `users/application/dto/UpdateTenantUserRequest.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTenantUserRequest(
        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName
) {
}
```

### `users/application/mapper/TenantUserMapper.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.mapper;

import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import org.springframework.stereotype.Component;

@Component
public class TenantUserMapper {

    public TenantUserResponse toResponse(TenantUser tenantUser) {
        return new TenantUserResponse(
                tenantUser.id(),
                tenantUser.email(),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status().name(),
                tenantUser.createdAt(),
                tenantUser.updatedAt()
        );
    }
}
```

### `users/application/usecase/ActivateTenantUserUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.TenantPlanEnforcementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActivateTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;
    private final AuditTrailService auditTrailService;
    private final TenantPlanEnforcementService tenantPlanEnforcementService;

    public ActivateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper,
            AuditTrailService auditTrailService,
            TenantPlanEnforcementService tenantPlanEnforcementService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
        this.auditTrailService = auditTrailService;
        this.tenantPlanEnforcementService = tenantPlanEnforcementService;
    }

    @Transactional
    public TenantUserResponse execute(UUID id) {
        TenantUser existingUser = tenantUserRepository.findById(id)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + id));

        if (!existingUser.active()) {
            tenantPlanEnforcementService.assertCanActivateUser(
                    tenantUserRepository.countActiveUsers()
            );
        }

        TenantUser updatedUser = new TenantUser(
                existingUser.id(),
                existingUser.email(),
                existingUser.passwordHash(),
                existingUser.firstName(),
                existingUser.lastName(),
                true,
                TenantUserStatus.ACTIVE,
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        TenantUser savedUser = tenantUserRepository.save(updatedUser);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.USER_ACTIVATED,
                "USER",
                savedUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "ACTIVATE_USER",
                        "email", savedUser.email(),
                        "active", true
                ),
                IdentityAuditPayloads.userState(
                        existingUser.email(),
                        existingUser.firstName(),
                        existingUser.lastName(),
                        existingUser.active(),
                        existingUser.status().name()
                ),
                IdentityAuditPayloads.userState(
                        savedUser.email(),
                        savedUser.firstName(),
                        savedUser.lastName(),
                        savedUser.active(),
                        savedUser.status().name()
                )
        );

        return tenantUserMapper.toResponse(savedUser);
    }
}

```

### `users/application/usecase/CreateTenantUserUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.users.application.dto.CreateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserAlreadyExistsException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.TenantPlanEnforcementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreateTenantUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateTenantUserUseCase.class);

    private final TenantUserRepository tenantUserRepository;
    private final TenantRoleRepository tenantRoleRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final TenantUserMapper tenantUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditTrailService auditTrailService;
    private final TenantPlanEnforcementService tenantPlanEnforcementService;

    public CreateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantRoleRepository tenantRoleRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            TenantUserMapper tenantUserMapper,
            PasswordEncoder passwordEncoder,
            AuditTrailService auditTrailService,
            TenantPlanEnforcementService tenantPlanEnforcementService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
        this.tenantUserMapper = tenantUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditTrailService = auditTrailService;
        this.tenantPlanEnforcementService = tenantPlanEnforcementService;
    }

    @Transactional
    public TenantUserResponse execute(CreateTenantUserRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (tenantUserRepository.existsByEmail(normalizedEmail)) {
            throw new TenantUserAlreadyExistsException(
                    "A tenant user with email '" + normalizedEmail + "' already exists"
            );
        }

        tenantPlanEnforcementService.assertCanCreateUser(
                tenantUserRepository.countActiveUsers()
        );

        TenantUser tenantUserToCreate = new TenantUser(
                null,
                normalizedEmail,
                passwordEncoder.encode(request.password()),
                normalizeNullable(request.firstName()),
                normalizeNullable(request.lastName()),
                true,
                TenantUserStatus.ACTIVE,
                null,
                null
        );

        TenantUser createdTenantUser = tenantUserRepository.save(tenantUserToCreate);

        boolean defaultRoleAssigned = tenantRoleRepository.findByName("USER")
                .map(role -> role.id())
                .map(userRoleId -> {
                    tenantUserRoleRepository.replaceUserRoles(createdTenantUser.id(), List.of(userRoleId));
                    return true;
                })
                .orElseGet(() -> {
                    logger.warn(
                            "Default tenant role 'USER' was not found for user '{}'; user will be created without roles",
                            createdTenantUser.email()
                    );
                    return false;
                });

        auditTrailService.recordTenantEvent(
                AuditEventTypes.USER_CREATED,
                "USER",
                createdTenantUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "CREATE_USER",
                        "email", createdTenantUser.email(),
                        "firstName", createdTenantUser.firstName(),
                        "lastName", createdTenantUser.lastName(),
                        "defaultRole", "USER",
                        "defaultRoleAssigned", defaultRoleAssigned
                ),
                null,
                IdentityAuditPayloads.userState(
                        createdTenantUser.email(),
                        createdTenantUser.firstName(),
                        createdTenantUser.lastName(),
                        createdTenantUser.active(),
                        createdTenantUser.status().name()
                )
        );

        return tenantUserMapper.toResponse(createdTenantUser);
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

```

### `users/application/usecase/DeactivateTenantUserUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeactivateTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;
    private final AuditTrailService auditTrailService;

    public DeactivateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public TenantUserResponse execute(UUID id) {
        TenantUser existingUser = tenantUserRepository.findById(id)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + id));

        TenantUser updatedUser = new TenantUser(
                existingUser.id(),
                existingUser.email(),
                existingUser.passwordHash(),
                existingUser.firstName(),
                existingUser.lastName(),
                false,
                TenantUserStatus.INACTIVE,
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        TenantUser savedUser = tenantUserRepository.save(updatedUser);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.USER_DEACTIVATED,
                "USER",
                savedUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "DEACTIVATE_USER",
                        "email", savedUser.email(),
                        "active", false
                ),
                IdentityAuditPayloads.userState(
                        existingUser.email(),
                        existingUser.firstName(),
                        existingUser.lastName(),
                        existingUser.active(),
                        existingUser.status().name()
                ),
                IdentityAuditPayloads.userState(
                        savedUser.email(),
                        savedUser.firstName(),
                        savedUser.lastName(),
                        savedUser.active(),
                        savedUser.status().name()
                )
        );

        return tenantUserMapper.toResponse(savedUser);
    }
}

```

### `users/application/usecase/GetTenantUserByIdUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetTenantUserByIdUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;

    public GetTenantUserByIdUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
    }

    public TenantUserResponse execute(UUID id) {
        return tenantUserRepository.findById(id)
                .map(tenantUserMapper::toResponse)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + id));
    }
}
```

### `users/application/usecase/ListTenantUsersUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTenantUsersUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;

    public ListTenantUsersUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
    }

    public List<TenantUserResponse> execute() {
        return tenantUserRepository.findAll()
                .stream()
                .map(tenantUserMapper::toResponse)
                .toList();
    }
}
```

### `users/application/usecase/UpdateTenantUserUseCase.java`

```java
package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.dto.UpdateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserAlreadyExistsException;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;
    private final AuditTrailService auditTrailService;

    public UpdateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public TenantUserResponse execute(UUID id, UpdateTenantUserRequest request) {
        TenantUser existingUser = tenantUserRepository.findById(id)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + id));

        String normalizedEmail = request.email().trim().toLowerCase();

        tenantUserRepository.findByEmail(normalizedEmail)
                .filter(foundUser -> !foundUser.id().equals(id))
                .ifPresent(foundUser -> {
                    throw new TenantUserAlreadyExistsException(
                            "A tenant user with email '" + normalizedEmail + "' already exists"
                    );
                });

        TenantUser updatedUser = new TenantUser(
                existingUser.id(),
                normalizedEmail,
                existingUser.passwordHash(),
                normalizeNullable(request.firstName()),
                normalizeNullable(request.lastName()),
                existingUser.active(),
                existingUser.status(),
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        TenantUser savedUser = tenantUserRepository.save(updatedUser);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.USER_UPDATED,
                "USER",
                savedUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "UPDATE_USER",
                        "previousEmail", existingUser.email(),
                        "newEmail", savedUser.email(),
                        "firstName", savedUser.firstName(),
                        "lastName", savedUser.lastName()
                ),
                IdentityAuditPayloads.userState(
                        existingUser.email(),
                        existingUser.firstName(),
                        existingUser.lastName(),
                        existingUser.active(),
                        existingUser.status().name()
                ),
                IdentityAuditPayloads.userState(
                        savedUser.email(),
                        savedUser.firstName(),
                        savedUser.lastName(),
                        savedUser.active(),
                        savedUser.status().name()
                )
        );

        return tenantUserMapper.toResponse(savedUser);
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

```

### `users/domain/exception/TenantUserAlreadyExistsException.java`

```java
package com.financesystem.finance_api.modules.identity.users.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class TenantUserAlreadyExistsException extends BusinessException {

    public TenantUserAlreadyExistsException(String message) {
        super(message);
    }
}
```

### `users/domain/exception/TenantUserNotFoundException.java`

```java
package com.financesystem.finance_api.modules.identity.users.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class TenantUserNotFoundException extends ResourceNotFoundException {

    public TenantUserNotFoundException(String message) {
        super(message);
    }
}
```

### `users/domain/model/TenantUser.java`

```java
package com.financesystem.finance_api.modules.identity.users.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TenantUser(
        UUID id,
        String email,
        String passwordHash,
        String firstName,
        String lastName,
        boolean active,
        TenantUserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
```

### `users/domain/model/TenantUserStatus.java`

```java
package com.financesystem.finance_api.modules.identity.users.domain.model;

public enum TenantUserStatus {
    ACTIVE,
    INACTIVE
}
```

### `users/domain/repository/TenantUserRepository.java`

```java
package com.financesystem.finance_api.modules.identity.users.domain.repository;

import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantUserRepository {

    TenantUser save(TenantUser tenantUser);

    Optional<TenantUser> findById(UUID id);

    Optional<TenantUser> findByEmail(String email);

    List<TenantUser> findAll();

    boolean existsByEmail(String email);

    long countActiveUsers();
}

```

### `users/infrastructure/api/TenantUserController.java`

```java
package com.financesystem.finance_api.modules.identity.users.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.modules.identity.users.application.dto.CreateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.dto.UpdateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class TenantUserController {

    private final CreateTenantUserUseCase createTenantUserUseCase;
    private final ListTenantUsersUseCase listTenantUsersUseCase;
    private final GetTenantUserByIdUseCase getTenantUserByIdUseCase;
    private final UpdateTenantUserUseCase updateTenantUserUseCase;
    private final ActivateTenantUserUseCase activateTenantUserUseCase;
    private final DeactivateTenantUserUseCase deactivateTenantUserUseCase;

    public TenantUserController(
            CreateTenantUserUseCase createTenantUserUseCase,
            ListTenantUsersUseCase listTenantUsersUseCase,
            GetTenantUserByIdUseCase getTenantUserByIdUseCase,
            UpdateTenantUserUseCase updateTenantUserUseCase,
            ActivateTenantUserUseCase activateTenantUserUseCase,
            DeactivateTenantUserUseCase deactivateTenantUserUseCase
    ) {
        this.createTenantUserUseCase = createTenantUserUseCase;
        this.listTenantUsersUseCase = listTenantUsersUseCase;
        this.getTenantUserByIdUseCase = getTenantUserByIdUseCase;
        this.updateTenantUserUseCase = updateTenantUserUseCase;
        this.activateTenantUserUseCase = activateTenantUserUseCase;
        this.deactivateTenantUserUseCase = deactivateTenantUserUseCase;
    }

    // @PostMapping
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @PreAuthorize("hasAuthority('users.create')")
    public ApiResponse<TenantUserResponse> createUser(@Valid @RequestBody CreateTenantUserRequest request) {
        return ApiResponse.success(
                "Tenant user created successfully",
                createTenantUserUseCase.execute(request)
        );
    }

    // @GetMapping
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @PreAuthorize("hasAuthority('users.list')")
    public ApiResponse<Page<TenantUserResponse>> listUsers(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(
                "Tenant users retrieved successfully",
                PaginationSupport.page(listTenantUsersUseCase.execute(), pageable)
        );
    }

    // @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('users.detail')")
    public ApiResponse<TenantUserResponse> getUserById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant user retrieved successfully",
                getTenantUserByIdUseCase.execute(id)
        );
    }

    // @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('users.update')")
    public ApiResponse<TenantUserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantUserRequest request
    ) {
        return ApiResponse.success(
                "Tenant user updated successfully",
                updateTenantUserUseCase.execute(id, request)
        );
    }

    // @PatchMapping("/{id}/activate")
    // @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('users.activate')")
    public ApiResponse<TenantUserResponse> activateUser(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant user activated successfully",
                activateTenantUserUseCase.execute(id)
        );
    }

    // @PatchMapping("/{id}/deactivate")
    // @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('users.deactivate')")
    public ApiResponse<TenantUserResponse> deactivateUser(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant user deactivated successfully",
                deactivateTenantUserUseCase.execute(id)
        );
    }
}

```

### `users/infrastructure/persistence/TenantUserEntity.java`

```java
package com.financesystem.finance_api.modules.identity.users.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_users")
public class TenantUserEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TenantUserStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }    public TenantUserStatus getStatus() {
        return status;
    }

    public void setStatus(TenantUserStatus status) {
        this.status = status;
    }    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
```

### `users/infrastructure/persistence/TenantUserJpaRepository.java`

```java
package com.financesystem.finance_api.modules.identity.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantUserJpaRepository extends JpaRepository<TenantUserEntity, UUID> {

    Optional<TenantUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByActiveTrue();
}

```

### `users/infrastructure/persistence/TenantUserRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.identity.users.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TenantUserRepositoryAdapter implements TenantUserRepository {

    private final TenantUserJpaRepository jpaRepository;

    public TenantUserRepositoryAdapter(TenantUserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TenantUser save(TenantUser tenantUser) {
        TenantUserEntity entity = toEntity(tenantUser);
        TenantUserEntity saved = jpaRepository.saveAndFlush(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TenantUser> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TenantUser> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<TenantUser> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public long countActiveUsers() {
        return jpaRepository.countByActiveTrue();
    }

    private TenantUserEntity toEntity(TenantUser tenantUser) {
        TenantUserEntity entity = new TenantUserEntity();
        entity.setId(tenantUser.id());
        entity.setEmail(tenantUser.email());
        entity.setPasswordHash(tenantUser.passwordHash());
        entity.setFirstName(tenantUser.firstName());
        entity.setLastName(tenantUser.lastName());
        entity.setActive(tenantUser.active());
        entity.setStatus(tenantUser.status());
        return entity;
    }

    private TenantUser toDomain(TenantUserEntity entity) {
        return new TenantUser(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.isActive(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

