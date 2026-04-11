package com.financesystem.finance_api.modules.identity.users.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.identity.users.application.dto.CreateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.dto.UpdateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<TenantUserResponse> createUser(@Valid @RequestBody CreateTenantUserRequest request) {
        return ApiResponse.success(
                "Tenant user created successfully",
                createTenantUserUseCase.execute(request)
        );
    }

    // @GetMapping
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<List<TenantUserResponse>> listUsers() {
        return ApiResponse.success(
                "Tenant users retrieved successfully",
                listTenantUsersUseCase.execute()
        );
    }

    // @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<TenantUserResponse> getUserById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant user retrieved successfully",
                getTenantUserByIdUseCase.execute(id)
        );
    }

    // @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<TenantUserResponse> activateUser(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant user activated successfully",
                activateTenantUserUseCase.execute(id)
        );
    }

    // @PatchMapping("/{id}/deactivate")
    // @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<TenantUserResponse> deactivateUser(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant user deactivated successfully",
                deactivateTenantUserUseCase.execute(id)
        );
    }
}