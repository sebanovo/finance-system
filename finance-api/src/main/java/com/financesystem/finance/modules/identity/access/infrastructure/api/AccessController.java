package com.financesystem.finance.modules.identity.access.infrastructure.api;

import com.financesystem.finance.common.response.ApiResponse;
import com.financesystem.finance.modules.identity.access.application.dto.*;
import com.financesystem.finance.modules.identity.access.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SystemPermissionResponse>> listPermissions() {
        return ApiResponse.success(
                "System permissions retrieved successfully",
                listSystemPermissionsUseCase.execute()
        );
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<TenantRoleResponse>> listRoles() {
        return ApiResponse.success(
                "Tenant roles retrieved successfully",
                listTenantRolesUseCase.execute()
        );
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TenantRoleResponse> createRole(@Valid @RequestBody CreateTenantRoleRequest request) {
        return ApiResponse.success(
                "Tenant role created successfully",
                createTenantRoleUseCase.execute(request)
        );
    }

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TenantRoleResponse> getRoleById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant role retrieved successfully",
                getTenantRoleByIdUseCase.execute(id)
        );
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TenantRoleResponse> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantRoleRequest request
    ) {
        return ApiResponse.success(
                "Tenant role updated successfully",
                updateTenantRoleUseCase.execute(id, request)
        );
    }

    @PatchMapping("/roles/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TenantRoleResponse> activateRole(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant role activated successfully",
                activateTenantRoleUseCase.execute(id)
        );
    }

    @PatchMapping("/roles/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TenantRoleResponse> deactivateRole(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant role deactivated successfully",
                deactivateTenantRoleUseCase.execute(id)
        );
    }

    @GetMapping("/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserRolesResponse> getUserRoles(@PathVariable UUID userId) {
        return ApiResponse.success(
                "User roles retrieved successfully",
                getUserRolesUseCase.execute(userId)
        );
    }

    @PutMapping("/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
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