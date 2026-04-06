package com.financesystem.finance.modules.platform.subscriptions.infrastructure.api;

import com.financesystem.finance.common.response.ApiResponse;
import com.financesystem.finance.modules.platform.subscriptions.application.dto.AssignPlatformSubscriptionRequest;
import com.financesystem.finance.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance.modules.platform.subscriptions.application.usecase.AssignPlatformSubscriptionUseCase;
import com.financesystem.finance.modules.platform.subscriptions.application.usecase.GetPlatformSubscriptionByIdUseCase;
import com.financesystem.finance.modules.platform.subscriptions.application.usecase.ListPlatformSubscriptionsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform/subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class PlatformSubscriptionController {

    private final AssignPlatformSubscriptionUseCase assignPlatformSubscriptionUseCase;
    private final ListPlatformSubscriptionsUseCase listPlatformSubscriptionsUseCase;
    private final GetPlatformSubscriptionByIdUseCase getPlatformSubscriptionByIdUseCase;

    public PlatformSubscriptionController(
            AssignPlatformSubscriptionUseCase assignPlatformSubscriptionUseCase,
            ListPlatformSubscriptionsUseCase listPlatformSubscriptionsUseCase,
            GetPlatformSubscriptionByIdUseCase getPlatformSubscriptionByIdUseCase
    ) {
        this.assignPlatformSubscriptionUseCase = assignPlatformSubscriptionUseCase;
        this.listPlatformSubscriptionsUseCase = listPlatformSubscriptionsUseCase;
        this.getPlatformSubscriptionByIdUseCase = getPlatformSubscriptionByIdUseCase;
    }

    @PostMapping("/assign")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformSubscriptionResponse> assignSubscription(
            @Valid @RequestBody AssignPlatformSubscriptionRequest request
    ) {
        return ApiResponse.success(
                "Platform subscription assigned successfully",
                assignPlatformSubscriptionUseCase.execute(request)
        );
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<List<PlatformSubscriptionResponse>> listSubscriptions() {
        return ApiResponse.success(
                "Platform subscriptions retrieved successfully",
                listPlatformSubscriptionsUseCase.execute()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformSubscriptionResponse> getSubscriptionById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Platform subscription retrieved successfully",
                getPlatformSubscriptionByIdUseCase.execute(id)
        );
    }
}
