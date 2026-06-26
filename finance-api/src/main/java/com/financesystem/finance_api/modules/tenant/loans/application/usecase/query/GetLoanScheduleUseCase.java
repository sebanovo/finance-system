package com.financesystem.finance_api.modules.tenant.loans.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanInstallmentResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.mapper.LoanMapper;
import com.financesystem.finance_api.modules.tenant.loans.domain.exception.LoanNotFoundException;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanInstallmentRepository;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetLoanScheduleUseCase {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final LoanMapper loanMapper;

    public GetLoanScheduleUseCase(
            LoanRepository loanRepository,
            LoanInstallmentRepository loanInstallmentRepository,
            LoanMapper loanMapper
    ) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.loanMapper = loanMapper;
    }

    @Transactional(readOnly = true)
    public List<LoanInstallmentResponse> execute(UUID loanId) {
        if (loanRepository.findById(loanId).isEmpty()) {
            throw new LoanNotFoundException("Loan not found: " + loanId);
        }
        return loanInstallmentRepository.findByLoanIdOrderByNumber(loanId).stream()
                .map(loanMapper::toInstallmentResponse)
                .toList();
    }
}
