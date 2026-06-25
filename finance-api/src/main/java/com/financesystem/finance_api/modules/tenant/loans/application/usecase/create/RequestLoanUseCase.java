package com.financesystem.finance_api.modules.tenant.loans.application.usecase.create;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.audit.TenantAuditPayloads;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.CreateLoanRequest;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.mapper.LoanMapper;
import com.financesystem.finance_api.modules.tenant.loans.domain.exception.InvalidLoanOperationException;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.InterestMethod;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanStatus;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RequestLoanUseCase {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final LoanMapper loanMapper;
    private final AuditTrailService auditTrailService;

    public RequestLoanUseCase(
            LoanRepository loanRepository,
            AccountRepository accountRepository,
            LoanMapper loanMapper,
            AuditTrailService auditTrailService
    ) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.loanMapper = loanMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public LoanResponse execute(CreateLoanRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new InvalidLoanOperationException(
                        "Disbursement account not found: " + request.accountId()));

        if (!account.userId().equals(request.userId())) {
            throw new InvalidLoanOperationException(
                    "The disbursement account does not belong to the borrower");
        }

        if (!account.active()) {
            throw new InvalidLoanOperationException("The disbursement account is not active");
        }

        InterestMethod interestMethod = request.interestMethod() == null
                ? InterestMethod.FLAT
                : request.interestMethod();

        Loan loan = new Loan(
                null,
                request.userId(),
                account.id(),
                request.principal(),
                account.currency(),
                request.annualInterestRate(),
                request.termMonths(),
                interestMethod,
                LoanStatus.REQUESTED,
                normalizeNullable(request.purpose()),
                null,
                null,
                null,
                null,
                null
        );

        Loan created = loanRepository.save(loan);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LOAN_REQUESTED,
                "LOAN",
                created.id().toString(),
                TenantAuditPayloads.details(
                        "userId", created.userId().toString(),
                        "accountId", created.accountId().toString(),
                        "principal", created.principal().toPlainString(),
                        "currency", created.currency().name(),
                        "annualInterestRate", created.annualInterestRate().toPlainString(),
                        "termMonths", created.termMonths(),
                        "interestMethod", created.interestMethod().name()
                ),
                null,
                null
        );

        return loanMapper.toResponse(created, List.of());
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
