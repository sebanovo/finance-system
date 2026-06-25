package com.financesystem.finance_api.modules.tenant.loans.domain.repository;

import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanPayment;

import java.util.List;
import java.util.UUID;

public interface LoanPaymentRepository {

    LoanPayment save(LoanPayment payment);

    List<LoanPayment> findByLoanId(UUID loanId);
}
