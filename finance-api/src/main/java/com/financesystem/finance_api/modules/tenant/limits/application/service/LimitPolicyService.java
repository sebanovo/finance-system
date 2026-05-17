package com.financesystem.finance_api.modules.tenant.limits.application.service;

import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitEvaluationRequest;
import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitEvaluationResponse;

import java.util.List;
import java.util.UUID;

public interface LimitPolicyService {

    LimitEvaluationResponse evaluate(LimitEvaluationRequest request);

    void registerUsage(LimitEvaluationRequest request, List<UUID> ruleIds);
}
