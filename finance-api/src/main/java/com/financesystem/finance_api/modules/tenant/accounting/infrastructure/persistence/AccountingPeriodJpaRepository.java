package com.financesystem.finance_api.modules.tenant.accounting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountingPeriodJpaRepository extends JpaRepository<AccountingPeriodEntity, UUID> {

    Optional<AccountingPeriodEntity> findByPeriodCode(String periodCode);
}
