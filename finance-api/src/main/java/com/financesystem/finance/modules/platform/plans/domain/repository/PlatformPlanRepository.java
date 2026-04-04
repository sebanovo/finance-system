package com.financesystem.finance.modules.platform.plans.domain.repository;

import com.financesystem.finance.modules.platform.plans.domain.model.PlatformPlan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatformPlanRepository {

    PlatformPlan save(PlatformPlan plan);

    Optional<PlatformPlan> findById(UUID id);

    List<PlatformPlan> findAll();

    boolean existsByCode(String code);
}