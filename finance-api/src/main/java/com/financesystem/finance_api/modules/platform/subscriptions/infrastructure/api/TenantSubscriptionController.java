package com.financesystem.finance_api.modules.platform.subscriptions.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance_api.modules.platform.subscriptions.application.usecase.GetCurrentTenantSubscriptionUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@SecurityRequirement(name = "bearerAuth")
public class TenantSubscriptionController {

    private final GetCurrentTenantSubscriptionUseCase getCurrentTenantSubscriptionUseCase;

    public TenantSubscriptionController(
            GetCurrentTenantSubscriptionUseCase getCurrentTenantSubscriptionUseCase
    ) {
        this.getCurrentTenantSubscriptionUseCase = getCurrentTenantSubscriptionUseCase;
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PlatformSubscriptionResponse> getCurrentSubscription() {
        String tenantSlug = TenantContextHolder.getRequired().tenantSlug();

        return ApiResponse.success(
                "Current tenant subscription retrieved successfully",
                getCurrentTenantSubscriptionUseCase.execute(tenantSlug)
        );
    }
}
