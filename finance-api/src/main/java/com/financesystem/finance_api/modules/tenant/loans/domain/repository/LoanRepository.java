package com.financesystem.finance_api.modules.tenant.loans.domain.repository;

import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(UUID id);

    List<Loan> findAll();

    List<Loan> findAllByUserId(UUID userId);
}
