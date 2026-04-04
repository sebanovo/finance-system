package com.financesystem.finance.modules.platform.tenants.infrastructure.api;

import com.financesystem.finance.common.response.ApiResponse;
import com.financesystem.finance.modules.platform.tenants.application.dto.CreateTenantRequest;
import com.financesystem.finance.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance.modules.platform.tenants.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform/tenants")
@SecurityRequirement(name = "bearerAuth")
public class PlatformTenantController {

    private final CreateTenantUseCase createTenantUseCase;
    private final ListTenantsUseCase listTenantsUseCase;
    private final GetTenantByIdUseCase getTenantByIdUseCase;
    private final ActivateTenantUseCase activateTenantUseCase;
    private final DeactivateTenantUseCase deactivateTenantUseCase;

    public PlatformTenantController(
            CreateTenantUseCase createTenantUseCase,
            ListTenantsUseCase listTenantsUseCase,
            GetTenantByIdUseCase getTenantByIdUseCase,
            ActivateTenantUseCase activateTenantUseCase,
            DeactivateTenantUseCase deactivateTenantUseCase
    ) {
        this.createTenantUseCase = createTenantUseCase;
        this.listTenantsUseCase = listTenantsUseCase;
        this.getTenantByIdUseCase = getTenantByIdUseCase;
        this.activateTenantUseCase = activateTenantUseCase;
        this.deactivateTenantUseCase = deactivateTenantUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ApiResponse<PlatformTenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        return ApiResponse.success(
                "Tenant created successfully",
                createTenantUseCase.execute(request)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ApiResponse<List<PlatformTenantResponse>> listTenants() {
        return ApiResponse.success(
                "Tenants retrieved successfully",
                listTenantsUseCase.execute()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ApiResponse<PlatformTenantResponse> getTenantById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant retrieved successfully",
                getTenantByIdUseCase.execute(id)
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ApiResponse<PlatformTenantResponse> activateTenant(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant activated successfully",
                activateTenantUseCase.execute(id)
        );
    }

    @PatchMapping("/{id}/deactivate")
    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ApiResponse<PlatformTenantResponse> deactivateTenant(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant deactivated successfully",
                deactivateTenantUseCase.execute(id)
        );
    }
}
