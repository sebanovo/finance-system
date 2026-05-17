package com.financesystem.finance_api.modules.tenant.limits.domain.repository;

import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LimitRuleRepository {

    LimitRule save(LimitRule limitRule);

    Optional<LimitRule> findById(UUID id);

    Optional<LimitRule> findByCode(String code);

    List<LimitRule> findAll();

    List<LimitRule> findAllActive();
}
