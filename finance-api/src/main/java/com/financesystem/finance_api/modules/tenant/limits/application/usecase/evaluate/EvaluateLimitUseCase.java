package com.financesystem.finance_api.modules.tenant.limits.application.usecase.evaluate;

import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitEvaluationRequest;
import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitEvaluationResponse;
import com.financesystem.finance_api.modules.tenant.limits.application.service.LimitPolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluateLimitUseCase {

    private final LimitPolicyService limitPolicyService;

    public EvaluateLimitUseCase(LimitPolicyService limitPolicyService) {
        this.limitPolicyService = limitPolicyService;
    }

    @Transactional(readOnly = true)
    public LimitEvaluationResponse execute(LimitEvaluationRequest request) {
        return limitPolicyService.evaluate(request);
    }
}
