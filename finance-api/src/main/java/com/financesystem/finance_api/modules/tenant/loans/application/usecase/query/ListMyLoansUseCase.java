package com.financesystem.finance_api.modules.tenant.loans.application.usecase.query;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.mapper.LoanMapper;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanInstallmentRepository;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListMyLoansUseCase {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final LoanMapper loanMapper;
    private final SecurityContextFacade securityContextFacade;

    public ListMyLoansUseCase(
            LoanRepository loanRepository,
            LoanInstallmentRepository loanInstallmentRepository,
            LoanMapper loanMapper,
            SecurityContextFacade securityContextFacade
    ) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.loanMapper = loanMapper;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> execute() {
        UUID currentUserId = UUID.fromString(securityContextFacade.getCurrentSubject());
        List<Loan> loans = loanRepository.findAllByUserId(currentUserId);
        return loans.stream()
                .map(loan -> loanMapper.toResponse(
                        loan,
                        loanInstallmentRepository.findByLoanIdOrderByNumber(loan.id())
                ))
                .toList();
    }
}
