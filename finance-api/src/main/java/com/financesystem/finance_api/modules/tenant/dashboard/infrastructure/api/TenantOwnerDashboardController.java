package com.financesystem.finance_api.modules.tenant.dashboard.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.dashboard.application.dto.TenantOwnerDashboardResponse;
import com.financesystem.finance_api.modules.tenant.dashboard.application.usecase.GetTenantOwnerDashboardUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class TenantOwnerDashboardController {

    private final GetTenantOwnerDashboardUseCase getTenantOwnerDashboardUseCase;

    public TenantOwnerDashboardController(GetTenantOwnerDashboardUseCase getTenantOwnerDashboardUseCase) {
        this.getTenantOwnerDashboardUseCase = getTenantOwnerDashboardUseCase;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('OWNER_ADMIN')")
    public ApiResponse<TenantOwnerDashboardResponse> summary() {
        return ApiResponse.success(
                "Tenant owner dashboard retrieved successfully",
                getTenantOwnerDashboardUseCase.execute()
        );
    }
}
