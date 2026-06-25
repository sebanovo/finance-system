package com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.audit.TenantAuditPayloads;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanDetailResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.mapper.LoanMapper;
import com.financesystem.finance_api.modules.tenant.loans.application.service.LoanNotificationService;
import com.financesystem.finance_api.modules.tenant.loans.application.service.LoanScheduleService;
import com.financesystem.finance_api.modules.tenant.loans.domain.exception.InvalidLoanOperationException;
import com.financesystem.finance_api.modules.tenant.loans.domain.exception.LoanNotFoundException;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallment;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanStatus;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanInstallmentRepository;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateDepositTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.TransactionProcessorService;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class DisburseLoanUseCase {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final AccountRepository accountRepository;
    private final LoanScheduleService loanScheduleService;
    private final TransactionProcessorService transactionProcessorService;
    private final LoanMapper loanMapper;
    private final AuditTrailService auditTrailService;
    private final LoanNotificationService loanNotificationService;

    public DisburseLoanUseCase(
            LoanRepository loanRepository,
            LoanInstallmentRepository loanInstallmentRepository,
            AccountRepository accountRepository,
            LoanScheduleService loanScheduleService,
            TransactionProcessorService transactionProcessorService,
            LoanMapper loanMapper,
            AuditTrailService auditTrailService,
            LoanNotificationService loanNotificationService
    ) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.accountRepository = accountRepository;
        this.loanScheduleService = loanScheduleService;
        this.transactionProcessorService = transactionProcessorService;
        this.loanMapper = loanMapper;
        this.auditTrailService = auditTrailService;
        this.loanNotificationService = loanNotificationService;
    }

    @Transactional
    public LoanDetailResponse execute(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));

        if (loan.status() != LoanStatus.APPROVED) {
            throw new InvalidLoanOperationException(
                    "Only APPROVED loans can be disbursed (current status: " + loan.status() + ")");
        }

        Account account = accountRepository.findById(loan.accountId())
                .orElseThrow(() -> new InvalidLoanOperationException(
                        "Disbursement account not found: " + loan.accountId()));

        if (!account.active()) {
            throw new InvalidLoanOperationException("The disbursement account is not active");
        }

        // Credit the borrower's account through the existing transaction engine,
        // which updates the balance and posts the accounting entries.
        TransactionResponse disbursement = transactionProcessorService.createDeposit(
                new CreateDepositTransactionRequest(
                        loan.accountId(),
                        loan.principal(),
                        loan.currency(),
                        TransactionChannel.SYSTEM,
                        "LOAN:" + loan.id(),
                        "Loan disbursement " + loan.id(),
                        "loan-disb-" + loan.id()
                ),
                FxOperationCode.LOAN_DISBURSEMENT
        );

        Instant now = Instant.now();
        LocalDate firstDueDate = LocalDate.ofInstant(now, ZoneOffset.UTC).plusMonths(1);

        Loan disbursedLoan = new Loan(
                loan.id(), loan.userId(), loan.accountId(), loan.principal(), loan.currency(),
                loan.annualInterestRate(), loan.termMonths(), loan.interestMethod(),
                LoanStatus.DISBURSED, loan.purpose(), null,
                now, loan.closedAt(), loan.createdAt(), loan.updatedAt()
        );
        Loan saved = loanRepository.save(disbursedLoan);

        List<LoanInstallment> schedule = loanScheduleService.generateSchedule(saved, firstDueDate);
        List<LoanInstallment> installments = loanInstallmentRepository.saveAll(schedule);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LOAN_DISBURSED,
                "LOAN",
                saved.id().toString(),
                TenantAuditPayloads.details(
                        "accountId", saved.accountId().toString(),
                        "principal", saved.principal().toPlainString(),
                        "currency", saved.currency().name(),
                        "transactionId", disbursement.id().toString(),
                        "installments", installments.size()
                ),
                TenantAuditPayloads.details("status", LoanStatus.APPROVED.name()),
                TenantAuditPayloads.details("status", LoanStatus.DISBURSED.name())
        );

        loanNotificationService.loanDisbursed(saved);

        return loanMapper.toDetailResponse(saved, installments);
    }
}
