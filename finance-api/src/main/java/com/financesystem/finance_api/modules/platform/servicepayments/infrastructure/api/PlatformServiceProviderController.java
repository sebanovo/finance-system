package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-providers")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceProviderController {

    private final CreateServiceProviderUseCase createServiceProviderUseCase;
    private final ListServiceProvidersUseCase listServiceProvidersUseCase;
    private final ListServiceProviderCatalogUseCase listServiceProviderCatalogUseCase;
    private final GetServiceProviderUseCase getServiceProviderUseCase;
    private final UpdateServiceProviderUseCase updateServiceProviderUseCase;
    private final ChangeServiceProviderStatusUseCase changeServiceProviderStatusUseCase;

    public PlatformServiceProviderController(
            CreateServiceProviderUseCase createServiceProviderUseCase,
            ListServiceProvidersUseCase listServiceProvidersUseCase,
            ListServiceProviderCatalogUseCase listServiceProviderCatalogUseCase,
            GetServiceProviderUseCase getServiceProviderUseCase,
            UpdateServiceProviderUseCase updateServiceProviderUseCase,
            ChangeServiceProviderStatusUseCase changeServiceProviderStatusUseCase
    ) {
        this.createServiceProviderUseCase = createServiceProviderUseCase;
        this.listServiceProvidersUseCase = listServiceProvidersUseCase;
        this.listServiceProviderCatalogUseCase = listServiceProviderCatalogUseCase;
        this.getServiceProviderUseCase = getServiceProviderUseCase;
        this.updateServiceProviderUseCase = updateServiceProviderUseCase;
        this.changeServiceProviderStatusUseCase = changeServiceProviderStatusUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> create(@Valid @RequestBody CreateServiceProviderRequest request) {
        return ApiResponse.success("Service provider created successfully", createServiceProviderUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceProviderResponse>> list(
            @ParameterObject PlatformServiceProviderFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success("Service providers retrieved successfully", listServiceProvidersUseCase.execute(filter, pageable));
    }

    @GetMapping("/catalog")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<java.util.List<ServiceProviderCatalogResponse>> catalog() {
        return ApiResponse.success(
                "Service provider catalog retrieved successfully",
                listServiceProviderCatalogUseCase.execute()
        );
    }

    @GetMapping("/{providerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> detail(@PathVariable UUID providerId) {
        return ApiResponse.success("Service provider retrieved successfully", getServiceProviderUseCase.execute(providerId));
    }

    @PatchMapping("/{providerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> update(
            @PathVariable UUID providerId,
            @Valid @RequestBody UpdateServiceProviderRequest request
    ) {
        return ApiResponse.success("Service provider updated successfully", updateServiceProviderUseCase.execute(providerId, request));
    }

    @PatchMapping("/{providerId}/status")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> changeStatus(
            @PathVariable UUID providerId,
            @Valid @RequestBody ChangeServiceProviderStatusRequest request
    ) {
        return ApiResponse.success(
                "Service provider status changed successfully",
                changeServiceProviderStatusUseCase.execute(providerId, request)
        );
    }
}
