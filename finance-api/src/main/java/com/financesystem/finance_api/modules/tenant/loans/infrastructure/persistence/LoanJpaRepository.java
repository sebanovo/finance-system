package com.financesystem.finance_api.modules.tenant.loans.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanJpaRepository extends JpaRepository<LoanEntity, UUID> {

    List<LoanEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    List<LoanEntity> findAllByOrderByCreatedAtDesc();
}
