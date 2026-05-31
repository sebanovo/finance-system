package com.financesystem.finance_api.modules.tenant.dashboard.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.dashboard.application.dto.TenantCustomerDashboardResponse;
import com.financesystem.finance_api.modules.tenant.dashboard.application.usecase.GetTenantCustomerDashboardUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class MyDashboardController {

    private final GetTenantCustomerDashboardUseCase getTenantCustomerDashboardUseCase;

    public MyDashboardController(GetTenantCustomerDashboardUseCase getTenantCustomerDashboardUseCase) {
        this.getTenantCustomerDashboardUseCase = getTenantCustomerDashboardUseCase;
    }

    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<TenantCustomerDashboardResponse> summary() {
        return ApiResponse.success(
                "Customer dashboard retrieved successfully",
                getTenantCustomerDashboardUseCase.execute()
        );
    }
}
