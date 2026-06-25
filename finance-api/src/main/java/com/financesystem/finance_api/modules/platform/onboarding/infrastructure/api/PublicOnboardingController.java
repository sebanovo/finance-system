package com.financesystem.finance_api.modules.platform.onboarding.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicPaidSignupRequest;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicPaidSignupResponse;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicSignupRequest;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicSignupResponse;
import com.financesystem.finance_api.modules.platform.onboarding.application.usecase.PublicPaidSignupUseCase;
import com.financesystem.finance_api.modules.platform.onboarding.application.usecase.PublicSignupUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicOnboardingController {

    private final PublicSignupUseCase publicSignupUseCase;
    private final PublicPaidSignupUseCase publicPaidSignupUseCase;

    public PublicOnboardingController(
            PublicSignupUseCase publicSignupUseCase,
            PublicPaidSignupUseCase publicPaidSignupUseCase
    ) {
        this.publicSignupUseCase = publicSignupUseCase;
        this.publicPaidSignupUseCase = publicPaidSignupUseCase;
    }

    @PostMapping("/signup")
    public ApiResponse<PublicSignupResponse> signup(@Valid @RequestBody PublicSignupRequest request) {
        return ApiResponse.success(
                "Public signup completed successfully",
                publicSignupUseCase.execute(request)
        );
    }

    @PostMapping("/signup/checkout")
    public ApiResponse<PublicPaidSignupResponse> paidSignup(@Valid @RequestBody PublicPaidSignupRequest request) {
        return ApiResponse.success(
                "Public paid signup checkout created successfully",
                publicPaidSignupUseCase.execute(request)
        );
    }
}
