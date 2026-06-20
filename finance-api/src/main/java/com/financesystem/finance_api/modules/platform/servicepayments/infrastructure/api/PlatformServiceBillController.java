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
@RequestMapping("/api/platform/service-bills")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceBillController {

    private final CreateServiceBillUseCase createServiceBillUseCase;
    private final ListServiceBillsUseCase listServiceBillsUseCase;
    private final GetServiceBillUseCase getServiceBillUseCase;
    private final CancelServiceBillUseCase cancelServiceBillUseCase;

    public PlatformServiceBillController(
            CreateServiceBillUseCase createServiceBillUseCase,
            ListServiceBillsUseCase listServiceBillsUseCase,
            GetServiceBillUseCase getServiceBillUseCase,
            CancelServiceBillUseCase cancelServiceBillUseCase
    ) {
        this.createServiceBillUseCase = createServiceBillUseCase;
        this.listServiceBillsUseCase = listServiceBillsUseCase;
        this.getServiceBillUseCase = getServiceBillUseCase;
        this.cancelServiceBillUseCase = cancelServiceBillUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillResponse> create(@Valid @RequestBody CreateServiceBillRequest request) {
        return ApiResponse.success("Service bill created successfully", createServiceBillUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceBillResponse>> list(
            @ParameterObject PlatformServiceBillFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success("Service bills retrieved successfully", listServiceBillsUseCase.execute(filter, pageable));
    }

    @GetMapping("/{billId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillResponse> detail(@PathVariable UUID billId) {
        return ApiResponse.success("Service bill retrieved successfully", getServiceBillUseCase.execute(billId));
    }

    @PatchMapping("/{billId}/cancel")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillResponse> cancel(
            @PathVariable UUID billId,
            @Valid @RequestBody CancelServiceBillRequest request
    ) {
        return ApiResponse.success("Service bill cancelled successfully", cancelServiceBillUseCase.execute(billId, request));
    }
}
