package com.financesystem.finance.modules.platform.plans.application.mapper;

import com.financesystem.finance.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance.modules.platform.plans.domain.model.PlatformPlan;
import org.springframework.stereotype.Component;

@Component
public class PlatformPlanMapper {

    public PlatformPlanResponse toResponse(PlatformPlan plan) {
        return new PlatformPlanResponse(
                plan.id(),
                plan.code(),
                plan.name(),
                plan.description(),
                plan.maxUsers(),
                plan.maxRoles(),
                plan.planType(),
                plan.trialDays(),
                plan.active(),
                plan.createdAt(),
                plan.updatedAt()
        );
    }
}
