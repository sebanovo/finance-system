package com.financesystem.finance_api.modules.tenant.loans.application.usecase.create;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.CreateLoanRequest;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.CreateMyLoanRequest;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RequestMyLoanUseCase {

    private final RequestLoanUseCase requestLoanUseCase;
    private final SecurityContextFacade securityContextFacade;

    public RequestMyLoanUseCase(
            RequestLoanUseCase requestLoanUseCase,
            SecurityContextFacade securityContextFacade
    ) {
        this.requestLoanUseCase = requestLoanUseCase;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional
    public LoanResponse execute(CreateMyLoanRequest request) {
        UUID currentUserId = UUID.fromString(securityContextFacade.getCurrentSubject());
        return requestLoanUseCase.execute(new CreateLoanRequest(
                currentUserId,
                request.accountId(),
                request.principal(),
                request.annualInterestRate(),
                request.termMonths(),
                request.interestMethod(),
                request.purpose()
        ));
    }
}
