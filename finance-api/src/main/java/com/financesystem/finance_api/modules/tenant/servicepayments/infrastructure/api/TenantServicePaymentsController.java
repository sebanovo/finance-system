package com.financesystem.finance_api.modules.tenant.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderCatalogResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.ListServiceProviderCatalogUseCase;
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
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class TenantServicePaymentsController {

    private final ListTenantServiceProvidersUseCase listTenantServiceProvidersUseCase;
    private final ListServiceProviderCatalogUseCase listServiceProviderCatalogUseCase;
    private final QueryTenantServiceBillsUseCase queryTenantServiceBillsUseCase;
    private final CreateBankServicePaymentUseCase createBankServicePaymentUseCase;
    private final ListTenantServicePaymentsUseCase listTenantServicePaymentsUseCase;
    private final GetTenantServicePaymentUseCase getTenantServicePaymentUseCase;

    public TenantServicePaymentsController(
            ListTenantServiceProvidersUseCase listTenantServiceProvidersUseCase,
            ListServiceProviderCatalogUseCase listServiceProviderCatalogUseCase,
            QueryTenantServiceBillsUseCase queryTenantServiceBillsUseCase,
            CreateBankServicePaymentUseCase createBankServicePaymentUseCase,
            ListTenantServicePaymentsUseCase listTenantServicePaymentsUseCase,
            GetTenantServicePaymentUseCase getTenantServicePaymentUseCase
    ) {
        this.listTenantServiceProvidersUseCase = listTenantServiceProvidersUseCase;
        this.listServiceProviderCatalogUseCase = listServiceProviderCatalogUseCase;
        this.queryTenantServiceBillsUseCase = queryTenantServiceBillsUseCase;
        this.createBankServicePaymentUseCase = createBankServicePaymentUseCase;
        this.listTenantServicePaymentsUseCase = listTenantServicePaymentsUseCase;
        this.getTenantServicePaymentUseCase = getTenantServicePaymentUseCase;
    }

    @GetMapping("/service-providers")
    @PreAuthorize("hasAuthority('service-providers.read')")
    public ApiResponse<Page<ServiceProviderResponse>> listServiceProviders(
            @ParameterObject PlatformServiceProviderFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Service providers retrieved successfully",
                listTenantServiceProvidersUseCase.execute(filter, pageable)
        );
    }

    @GetMapping("/service-providers/catalog")
    @PreAuthorize("hasAuthority('service-providers.read')")
    public ApiResponse<java.util.List<ServiceProviderCatalogResponse>> listServiceProviderCatalog() {
        return ApiResponse.success(
                "Service provider catalog retrieved successfully",
                listServiceProviderCatalogUseCase.execute()
        );
    }

    @PostMapping("/service-bills/query")
    @PreAuthorize("hasAuthority('service-bills.query')")
    public ApiResponse<QueryServiceBillsResponse> queryServiceBills(@Valid @RequestBody QueryTenantServiceBillsRequest request) {
        return ApiResponse.success(
                "Service bills retrieved successfully",
                queryTenantServiceBillsUseCase.execute(request)
        );
    }

    @PostMapping("/service-payments")
    @PreAuthorize("hasAuthority('service-payments.create')")
    public ApiResponse<ServicePaymentResponse> createServicePayment(@Valid @RequestBody CreateBankServicePaymentRequest request) {
        return ApiResponse.success(
                "Service payment completed successfully",
                createBankServicePaymentUseCase.execute(request)
        );
    }

    @GetMapping("/service-payments")
    @PreAuthorize("hasAuthority('service-payments.read')")
    public ApiResponse<Page<ServicePaymentResponse>> listServicePayments(
            @ParameterObject TenantServicePaymentFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Service payments retrieved successfully",
                listTenantServicePaymentsUseCase.execute(filter, pageable)
        );
    }

    @GetMapping("/service-payments/{paymentId}")
    @PreAuthorize("hasAuthority('service-payments.detail')")
    public ApiResponse<ServicePaymentResponse> getServicePayment(@PathVariable UUID paymentId) {
        return ApiResponse.success(
                "Service payment retrieved successfully",
                getTenantServicePaymentUseCase.execute(paymentId)
        );
    }
}
