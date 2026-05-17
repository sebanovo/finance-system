package com.financesystem.finance_api.modules.tenant.limits.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LimitUsageJpaRepository extends JpaRepository<LimitUsageEntity, UUID> {

    Optional<LimitUsageEntity> findByLimitRuleIdAndScopeKeyAndPeriodKey(
            UUID limitRuleId,
            String scopeKey,
            String periodKey
    );
}
