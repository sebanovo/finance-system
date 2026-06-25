package com.financesystem.finance_api.modules.tenant.loans.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanPaymentJpaRepository extends JpaRepository<LoanPaymentEntity, UUID> {

    List<LoanPaymentEntity> findByLoanIdOrderByPaidAtAsc(UUID loanId);
}
