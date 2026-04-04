package com.financesystem.finance.modules.platform.plans.application.usecase;

import com.financesystem.finance.modules.platform.plans.application.dto.CreatePlatformPlanRequest;
import com.financesystem.finance.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance.modules.platform.plans.application.mapper.PlatformPlanMapper;
import com.financesystem.finance.modules.platform.plans.domain.exception.PlatformPlanAlreadyExistsException;
import com.financesystem.finance.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance.modules.platform.plans.domain.repository.PlatformPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePlatformPlanUseCase {

    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformPlanMapper platformPlanMapper;

    public CreatePlatformPlanUseCase(
            PlatformPlanRepository platformPlanRepository,
            PlatformPlanMapper platformPlanMapper
    ) {
        this.platformPlanRepository = platformPlanRepository;
        this.platformPlanMapper = platformPlanMapper;
    }

    @Transactional
    public PlatformPlanResponse execute(CreatePlatformPlanRequest request) {
        String normalizedCode = request.code().trim().toUpperCase();

        if (platformPlanRepository.existsByCode(normalizedCode)) {
            throw new PlatformPlanAlreadyExistsException("A platform plan with code '" + normalizedCode + "' already exists");
        }

        PlatformPlan planToCreate = new PlatformPlan(
                null,
                normalizedCode,
                request.name().trim(),
                request.description() == null ? null : request.description().trim(),
                request.maxUsers(),
                request.maxRoles(),
                true,
                null,
                null
        );

        PlatformPlan createdPlan = platformPlanRepository.save(planToCreate);
        return platformPlanMapper.toResponse(createdPlan);
    }
}