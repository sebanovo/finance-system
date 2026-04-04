package com.financesystem.finance.modules.identity.access.application.usecase;

import com.financesystem.finance.modules.identity.access.application.dto.AssignUserRolesRequest;
import com.financesystem.finance.modules.identity.access.application.dto.TenantRoleResponse;
import com.financesystem.finance.modules.identity.access.application.dto.UserRolesResponse;
import com.financesystem.finance.modules.identity.access.application.mapper.TenantRoleMapper;
import com.financesystem.finance.modules.identity.access.domain.exception.TenantRoleNotFoundException;
import com.financesystem.finance.modules.identity.access.domain.model.TenantRole;
import com.financesystem.finance.modules.identity.access.domain.repository.TenantRolePermissionRepository;
import com.financesystem.finance.modules.identity.access.domain.repository.TenantRoleRepository;
import com.financesystem.finance.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
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

    public AssignUserRolesUseCase(
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

    @Transactional
    public UserRolesResponse execute(UUID userId, AssignUserRolesRequest request) {
        tenantUserRepository.findById(userId)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + userId));

        List<UUID> roleIds = request.roleIds() == null ? List.of() : request.roleIds();

        List<TenantRole> roles = tenantRoleRepository.findAllByIds(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new TenantRoleNotFoundException("One or more tenant roles do not exist");
        }

        tenantUserRoleRepository.replaceUserRoles(userId, roleIds);

        List<TenantRoleResponse> roleResponses = roles.stream()
                .map(role -> tenantRoleMapper.toResponse(
                        role,
                        tenantRolePermissionRepository.findPermissionCodesByRoleId(role.id()).stream().toList()
                ))
                .toList();

        return new UserRolesResponse(userId, roleResponses);
    }
}