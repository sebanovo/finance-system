package com.financesystem.finance_api.modules.tenant.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/me")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class MyServicePaymentsController {

    private final ListMyServiceProvidersUseCase listMyServiceProvidersUseCase;
    private final CreateMyServiceEnrollmentUseCase createMyServiceEnrollmentUseCase;
    private final ListMyServiceEnrollmentsUseCase listMyServiceEnrollmentsUseCase;
    private final DeleteMyServiceEnrollmentUseCase deleteMyServiceEnrollmentUseCase;
    private final QueryMyServiceBillsUseCase queryMyServiceBillsUseCase;
    private final CreateMyServicePaymentUseCase createMyServicePaymentUseCase;
    private final ListMyServicePaymentsUseCase listMyServicePaymentsUseCase;
    private final GetMyServicePaymentUseCase getMyServicePaymentUseCase;

    public MyServicePaymentsController(
            ListMyServiceProvidersUseCase listMyServiceProvidersUseCase,
            CreateMyServiceEnrollmentUseCase createMyServiceEnrollmentUseCase,
            ListMyServiceEnrollmentsUseCase listMyServiceEnrollmentsUseCase,
            DeleteMyServiceEnrollmentUseCase deleteMyServiceEnrollmentUseCase,
            QueryMyServiceBillsUseCase queryMyServiceBillsUseCase,
            CreateMyServicePaymentUseCase createMyServicePaymentUseCase,
            ListMyServicePaymentsUseCase listMyServicePaymentsUseCase,
            GetMyServicePaymentUseCase getMyServicePaymentUseCase
    ) {
        this.listMyServiceProvidersUseCase = listMyServiceProvidersUseCase;
        this.createMyServiceEnrollmentUseCase = createMyServiceEnrollmentUseCase;
        this.listMyServiceEnrollmentsUseCase = listMyServiceEnrollmentsUseCase;
        this.deleteMyServiceEnrollmentUseCase = deleteMyServiceEnrollmentUseCase;
        this.queryMyServiceBillsUseCase = queryMyServiceBillsUseCase;
        this.createMyServicePaymentUseCase = createMyServicePaymentUseCase;
        this.listMyServicePaymentsUseCase = listMyServicePaymentsUseCase;
        this.getMyServicePaymentUseCase = getMyServicePaymentUseCase;
    }

    @GetMapping("/service-providers")
    @PreAuthorize("hasAuthority('me.service-providers.read')")
    public ApiResponse<Page<ServiceProviderResponse>> listServiceProviders(
            @ParameterObject PlatformServiceProviderFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Service providers retrieved successfully",
                listMyServiceProvidersUseCase.execute(filter, pageable)
        );
    }

    @GetMapping("/service-enrollments")
    @PreAuthorize("hasAuthority('me.service-enrollments.read')")
    public ApiResponse<Page<ServiceEnrollmentResponse>> listServiceEnrollments(
            @ParameterObject MyServiceEnrollmentFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Service enrollments retrieved successfully",
                listMyServiceEnrollmentsUseCase.execute(filter, pageable)
        );
    }

    @PostMapping("/service-enrollments")
    @PreAuthorize("hasAuthority('me.service-enrollments.create')")
    public ApiResponse<ServiceEnrollmentResponse> createServiceEnrollment(@Valid @RequestBody CreateServiceEnrollmentRequest request) {
        return ApiResponse.success(
                "Service enrollment created successfully",
                createMyServiceEnrollmentUseCase.execute(request)
        );
    }

    @DeleteMapping("/service-enrollments/{enrollmentId}")
    @PreAuthorize("hasAuthority('me.service-enrollments.delete')")
    public ApiResponse<ServiceEnrollmentResponse> deleteServiceEnrollment(@PathVariable UUID enrollmentId) {
        return ApiResponse.success(
                "Service enrollment deleted successfully",
                deleteMyServiceEnrollmentUseCase.execute(enrollmentId)
        );
    }

    @PostMapping("/service-bills/query")
    @PreAuthorize("hasAuthority('me.service-bills.query')")
    public ApiResponse<QueryServiceBillsResponse> queryServiceBills(@Valid @RequestBody QueryServiceBillsRequest request) {
        return ApiResponse.success(
                "Service bills retrieved successfully",
                queryMyServiceBillsUseCase.execute(request)
        );
    }

    @PostMapping("/service-payments")
    @PreAuthorize("hasAuthority('me.service-payments.create')")
    public ApiResponse<ServicePaymentResponse> createServicePayment(@Valid @RequestBody CreateServicePaymentRequest request) {
        return ApiResponse.success(
                "Service payment completed successfully",
                createMyServicePaymentUseCase.execute(request)
        );
    }

    @GetMapping("/service-payments")
    @PreAuthorize("hasAuthority('me.service-payments.read')")
    public ApiResponse<Page<ServicePaymentResponse>> listServicePayments(
            @ParameterObject MyServicePaymentFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Service payments retrieved successfully",
                listMyServicePaymentsUseCase.execute(filter, pageable)
        );
    }

    @GetMapping("/service-payments/{paymentId}")
    @PreAuthorize("hasAuthority('me.service-payments.detail')")
    public ApiResponse<ServicePaymentResponse> getServicePayment(@PathVariable UUID paymentId) {
        return ApiResponse.success(
                "Service payment retrieved successfully",
                getMyServicePaymentUseCase.execute(paymentId)
        );
    }
}
