package com.financesystem.finance.modules.platform.onboarding.infrastructure.api;

import com.financesystem.finance.common.response.ApiResponse;
import com.financesystem.finance.modules.platform.onboarding.application.dto.PublicSignupRequest;
import com.financesystem.finance.modules.platform.onboarding.application.dto.PublicSignupResponse;
import com.financesystem.finance.modules.platform.onboarding.application.usecase.PublicSignupUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicOnboardingController {

    private final PublicSignupUseCase publicSignupUseCase;

    public PublicOnboardingController(PublicSignupUseCase publicSignupUseCase) {
        this.publicSignupUseCase = publicSignupUseCase;
    }

    @PostMapping("/signup")
    public ApiResponse<PublicSignupResponse> signup(@Valid @RequestBody PublicSignupRequest request) {
        return ApiResponse.success(
                "Public signup completed successfully",
                publicSignupUseCase.execute(request)
        );
    }
}
