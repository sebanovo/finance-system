package com.financesystem.finance_api.modules.platform.onboarding.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicSignupRequest;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicSignupResponse;
import com.financesystem.finance_api.modules.platform.onboarding.application.usecase.PublicSignupDirectUseCase;
import com.financesystem.finance_api.modules.platform.onboarding.application.usecase.PublicSignupUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicOnboardingController {

    private final PublicSignupUseCase publicSignupUseCase;
    private final PublicSignupDirectUseCase publicSignupDirectUseCase;

    public PublicOnboardingController(
            PublicSignupUseCase publicSignupUseCase,
            PublicSignupDirectUseCase publicSignupDirectUseCase
    ) {
        this.publicSignupUseCase = publicSignupUseCase;
        this.publicSignupDirectUseCase = publicSignupDirectUseCase;
    }

    @PostMapping("/signup")
    public ApiResponse<PublicSignupResponse> signup(@Valid @RequestBody PublicSignupRequest request) {
        return ApiResponse.success(
                "Public signup completed successfully",
                publicSignupUseCase.execute(request)
        );
    }

    @PostMapping("/signup-direct")
    public ApiResponse<PublicSignupResponse> signupDirect(@Valid @RequestBody PublicSignupRequest request) {
        return ApiResponse.success(
                "Public signup completed without email verification",
                publicSignupDirectUseCase.execute(request)
        );
    }
}
