package com.financesystem.finance_api.modules.tenant.loans.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.mapper.LoanMapper;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanInstallmentRepository;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListLoansUseCase {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final LoanMapper loanMapper;

    public ListLoansUseCase(
            LoanRepository loanRepository,
            LoanInstallmentRepository loanInstallmentRepository,
            LoanMapper loanMapper
    ) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.loanMapper = loanMapper;
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> execute() {
        List<Loan> loans = loanRepository.findAll();
        return loans.stream()
                .map(loan -> loanMapper.toResponse(
                        loan,
                        loanInstallmentRepository.findByLoanIdOrderByNumber(loan.id())
                ))
                .toList();
    }
}
