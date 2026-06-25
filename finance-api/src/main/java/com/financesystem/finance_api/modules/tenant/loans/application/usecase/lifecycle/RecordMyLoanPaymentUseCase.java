package com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanDetailResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.RecordLoanPaymentRequest;
import com.financesystem.finance_api.modules.tenant.loans.domain.exception.LoanNotFoundException;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RecordMyLoanPaymentUseCase {

    private final LoanRepository loanRepository;
    private final RecordLoanPaymentUseCase recordLoanPaymentUseCase;
    private final SecurityContextFacade securityContextFacade;

    public RecordMyLoanPaymentUseCase(
            LoanRepository loanRepository,
            RecordLoanPaymentUseCase recordLoanPaymentUseCase,
            SecurityContextFacade securityContextFacade
    ) {
        this.loanRepository = loanRepository;
        this.recordLoanPaymentUseCase = recordLoanPaymentUseCase;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional
    public LoanDetailResponse execute(UUID loanId, RecordLoanPaymentRequest request) {
        UUID currentUserId = UUID.fromString(securityContextFacade.getCurrentSubject());
        loanRepository.findById(loanId)
                .filter(l -> l.userId().equals(currentUserId))
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));
        return recordLoanPaymentUseCase.execute(loanId, request);
    }
}
