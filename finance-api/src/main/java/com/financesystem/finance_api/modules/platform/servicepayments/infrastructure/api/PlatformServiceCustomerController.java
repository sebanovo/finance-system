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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-customers")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceCustomerController {

    private final CreateServiceCustomerUseCase createServiceCustomerUseCase;
    private final ListServiceCustomersUseCase listServiceCustomersUseCase;
    private final GetServiceCustomerUseCase getServiceCustomerUseCase;
    private final UpdateServiceCustomerUseCase updateServiceCustomerUseCase;

    public PlatformServiceCustomerController(
            CreateServiceCustomerUseCase createServiceCustomerUseCase,
            ListServiceCustomersUseCase listServiceCustomersUseCase,
            GetServiceCustomerUseCase getServiceCustomerUseCase,
            UpdateServiceCustomerUseCase updateServiceCustomerUseCase
    ) {
        this.createServiceCustomerUseCase = createServiceCustomerUseCase;
        this.listServiceCustomersUseCase = listServiceCustomersUseCase;
        this.getServiceCustomerUseCase = getServiceCustomerUseCase;
        this.updateServiceCustomerUseCase = updateServiceCustomerUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceCustomerResponse> create(@Valid @RequestBody CreateServiceCustomerRequest request) {
        return ApiResponse.success("Service customer created successfully", createServiceCustomerUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceCustomerResponse>> list(
            @ParameterObject PlatformServiceCustomerFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success("Service customers retrieved successfully", listServiceCustomersUseCase.execute(filter, pageable));
    }

    @GetMapping("/{serviceCustomerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceCustomerResponse> detail(@PathVariable UUID serviceCustomerId) {
        return ApiResponse.success("Service customer retrieved successfully", getServiceCustomerUseCase.execute(serviceCustomerId));
    }

    @PatchMapping("/{serviceCustomerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceCustomerResponse> update(
            @PathVariable UUID serviceCustomerId,
            @Valid @RequestBody UpdateServiceCustomerRequest request
    ) {
        return ApiResponse.success("Service customer updated successfully", updateServiceCustomerUseCase.execute(serviceCustomerId, request));
    }
}
