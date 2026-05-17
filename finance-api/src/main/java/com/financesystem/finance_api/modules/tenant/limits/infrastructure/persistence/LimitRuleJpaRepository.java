package com.financesystem.finance_api.modules.tenant.limits.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LimitRuleJpaRepository extends JpaRepository<LimitRuleEntity, UUID> {

    Optional<LimitRuleEntity> findByCode(String code);

    List<LimitRuleEntity> findAllByActiveTrueOrderByCreatedAtDesc();
}
