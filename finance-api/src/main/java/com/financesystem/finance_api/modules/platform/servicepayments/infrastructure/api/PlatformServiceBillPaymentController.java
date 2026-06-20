package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.GetGlobalServiceBillPaymentUseCase;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.ListGlobalServiceBillPaymentsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-bill-payments")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceBillPaymentController {

    private final ListGlobalServiceBillPaymentsUseCase listGlobalServiceBillPaymentsUseCase;
    private final GetGlobalServiceBillPaymentUseCase getGlobalServiceBillPaymentUseCase;

    public PlatformServiceBillPaymentController(
            ListGlobalServiceBillPaymentsUseCase listGlobalServiceBillPaymentsUseCase,
            GetGlobalServiceBillPaymentUseCase getGlobalServiceBillPaymentUseCase
    ) {
        this.listGlobalServiceBillPaymentsUseCase = listGlobalServiceBillPaymentsUseCase;
        this.getGlobalServiceBillPaymentUseCase = getGlobalServiceBillPaymentUseCase;
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceBillPaymentResponse>> list(
            @ParameterObject PlatformServiceBillPaymentFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Service bill payments retrieved successfully",
                listGlobalServiceBillPaymentsUseCase.execute(filter, pageable)
        );
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillPaymentResponse> detail(@PathVariable UUID paymentId) {
        return ApiResponse.success(
                "Service bill payment retrieved successfully",
                getGlobalServiceBillPaymentUseCase.execute(paymentId)
        );
    }
}
