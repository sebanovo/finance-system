package com.financesystem.finance_api.modules.tenant.limits.domain.repository;

import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitUsage;

import java.util.Optional;
import java.util.UUID;

public interface LimitUsageRepository {

    LimitUsage save(LimitUsage limitUsage);

    Optional<LimitUsage> findByRuleIdAndScopeKeyAndPeriodKey(
            UUID limitRuleId,
            String scopeKey,
            String periodKey
    );
}
