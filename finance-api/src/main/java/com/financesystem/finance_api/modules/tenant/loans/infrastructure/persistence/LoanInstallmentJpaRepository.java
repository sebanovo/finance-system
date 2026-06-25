package com.financesystem.finance_api.modules.tenant.loans.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface LoanInstallmentJpaRepository extends JpaRepository<LoanInstallmentEntity, UUID> {

    List<LoanInstallmentEntity> findByLoanIdOrderByNumberAsc(UUID loanId);

    List<LoanInstallmentEntity> findByStatusInAndDueDateBefore(
            Collection<LoanInstallmentStatus> statuses, LocalDate date);
}
