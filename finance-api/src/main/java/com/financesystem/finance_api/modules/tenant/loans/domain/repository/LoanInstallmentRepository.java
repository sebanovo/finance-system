package com.financesystem.finance_api.modules.tenant.loans.domain.repository;

import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallment;

import java.time.LocalDate;
import java.util.List;

public interface LoanInstallmentRepository {

    LoanInstallment save(LoanInstallment installment);

    List<LoanInstallment> saveAll(List<LoanInstallment> installments);

    List<LoanInstallment> findByLoanIdOrderByNumber(java.util.UUID loanId);

    /** Unpaid installments (PENDING/PARTIAL) whose due date is before the given date. */
    List<LoanInstallment> findOverdueCandidates(LocalDate today);
}
