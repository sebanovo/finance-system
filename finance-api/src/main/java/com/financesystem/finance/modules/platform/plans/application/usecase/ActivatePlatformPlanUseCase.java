package com.financesystem.finance.modules.platform.plans.application.usecase;

import com.financesystem.finance.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance.modules.platform.plans.application.mapper.PlatformPlanMapper;
import com.financesystem.finance.modules.platform.plans.domain.exception.PlatformPlanNotFoundException;
import com.financesystem.finance.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance.modules.platform.plans.domain.repository.PlatformPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActivatePlatformPlanUseCase {

    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformPlanMapper platformPlanMapper;

    public ActivatePlatformPlanUseCase(
            PlatformPlanRepository platformPlanRepository,
            PlatformPlanMapper platformPlanMapper
    ) {
        this.platformPlanRepository = platformPlanRepository;
        this.platformPlanMapper = platformPlanMapper;
    }

    @Transactional
    public PlatformPlanResponse execute(UUID id) {
        PlatformPlan plan = platformPlanRepository.findById(id)
                .orElseThrow(() -> new PlatformPlanNotFoundException("Platform plan not found with id: " + id));

        PlatformPlan updated = new PlatformPlan(
                plan.id(),
                plan.code(),
                plan.name(),
                plan.description(),
                plan.maxUsers(),
                plan.maxRoles(),
                true,
                plan.createdAt(),
                plan.updatedAt()
        );

        PlatformPlan saved = platformPlanRepository.save(updated);
        return platformPlanMapper.toResponse(saved);
    }
}