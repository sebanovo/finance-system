package com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.audit.TenantAuditPayloads;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanDetailResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.RecordLoanPaymentRequest;
import com.financesystem.finance_api.modules.tenant.loans.application.mapper.LoanMapper;
import com.financesystem.finance_api.modules.tenant.loans.application.service.LoanNotificationService;
import com.financesystem.finance_api.modules.tenant.loans.domain.exception.InvalidLoanOperationException;
import com.financesystem.finance_api.modules.tenant.loans.domain.exception.LoanNotFoundException;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallment;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallmentStatus;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanPayment;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanStatus;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanInstallmentRepository;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanPaymentRepository;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateWithdrawalTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.TransactionProcessorService;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecordLoanPaymentUseCase {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final TransactionProcessorService transactionProcessorService;
    private final LoanMapper loanMapper;
    private final AuditTrailService auditTrailService;
    private final LoanNotificationService loanNotificationService;

    public RecordLoanPaymentUseCase(
            LoanRepository loanRepository,
            LoanInstallmentRepository loanInstallmentRepository,
            LoanPaymentRepository loanPaymentRepository,
            TransactionProcessorService transactionProcessorService,
            LoanMapper loanMapper,
            AuditTrailService auditTrailService,
            LoanNotificationService loanNotificationService
    ) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.loanPaymentRepository = loanPaymentRepository;
        this.transactionProcessorService = transactionProcessorService;
        this.loanMapper = loanMapper;
        this.auditTrailService = auditTrailService;
        this.loanNotificationService = loanNotificationService;
    }

    @Transactional
    public LoanDetailResponse execute(UUID loanId, RecordLoanPaymentRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));

        if (loan.status() != LoanStatus.DISBURSED) {
            throw new InvalidLoanOperationException(
                    "Only DISBURSED loans can receive payments (current status: " + loan.status() + ")");
        }

        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanIdOrderByNumber(loanId);

        BigDecimal outstanding = installments.stream()
                .map(i -> i.totalDue().subtract(i.paidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal amount = request.amount();
        if (amount.compareTo(outstanding) > 0) {
            throw new InvalidLoanOperationException(
                    "Payment amount " + amount.toPlainString()
                            + " exceeds the outstanding balance " + outstanding.toPlainString());
        }

        // Debit the borrower's account through the existing transaction engine.
        String idempotencyKey = request.idempotencyKey() == null || request.idempotencyKey().isBlank()
                ? "loan-pay-" + loanId + "-" + System.currentTimeMillis()
                : request.idempotencyKey().trim();

        TransactionResponse withdrawal = transactionProcessorService.createWithdrawal(
                new CreateWithdrawalTransactionRequest(
                        loan.accountId(),
                        amount,
                        loan.currency(),
                        TransactionChannel.SYSTEM,
                        "Loan repayment " + loanId,
                        idempotencyKey
                ),
                FxOperationCode.LOAN_REPAYMENT
        );

        Instant now = Instant.now();
        List<LoanInstallment> updated = applyWaterfall(installments, amount, now);
        loanInstallmentRepository.saveAll(updated);

        loanPaymentRepository.save(new LoanPayment(
                null, loanId, amount, withdrawal.id(), now, null
        ));

        boolean fullyPaid = updated.stream()
                .allMatch(i -> i.status() == LoanInstallmentStatus.PAID);

        Loan resultingLoan = loan;
        if (fullyPaid) {
            resultingLoan = new Loan(
                    loan.id(), loan.userId(), loan.accountId(), loan.principal(), loan.currency(),
                    loan.annualInterestRate(), loan.termMonths(), loan.interestMethod(),
                    LoanStatus.PAID_OFF, loan.purpose(), loan.statusReason(),
                    loan.disbursedAt(), now, loan.createdAt(), loan.updatedAt()
            );
            resultingLoan = loanRepository.save(resultingLoan);
        }

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LOAN_PAYMENT_RECORDED,
                "LOAN",
                loanId.toString(),
                TenantAuditPayloads.details(
                        "amount", amount.toPlainString(),
                        "transactionId", withdrawal.id().toString(),
                        "fullyPaid", fullyPaid
                ),
                null,
                null
        );

        if (fullyPaid) {
            auditTrailService.recordTenantEvent(
                    AuditEventTypes.LOAN_PAID_OFF,
                    "LOAN",
                    loanId.toString(),
                    TenantAuditPayloads.details("closedAt", now.toString()),
                    TenantAuditPayloads.details("status", LoanStatus.DISBURSED.name()),
                    TenantAuditPayloads.details("status", LoanStatus.PAID_OFF.name())
            );
        }

        BigDecimal outstandingAfter = updated.stream()
                .map(i -> i.totalDue().subtract(i.paidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        loanNotificationService.paymentRecorded(resultingLoan, amount, outstandingAfter);
        if (fullyPaid) {
            loanNotificationService.loanPaidOff(resultingLoan);
        }

        return loanMapper.toDetailResponse(resultingLoan, updated);
    }

    private List<LoanInstallment> applyWaterfall(
            List<LoanInstallment> installments,
            BigDecimal amount,
            Instant now
    ) {
        List<LoanInstallment> result = new ArrayList<>(installments.size());
        BigDecimal remaining = amount;

        for (LoanInstallment installment : installments) {
            if (installment.status() == LoanInstallmentStatus.PAID || remaining.signum() <= 0) {
                result.add(installment);
                continue;
            }

            BigDecimal due = installment.totalDue().subtract(installment.paidAmount());
            BigDecimal applied = remaining.min(due);
            BigDecimal newPaid = installment.paidAmount().add(applied);
            remaining = remaining.subtract(applied);

            boolean settled = newPaid.compareTo(installment.totalDue()) >= 0;
            LoanInstallmentStatus newStatus = settled
                    ? LoanInstallmentStatus.PAID
                    : LoanInstallmentStatus.PARTIAL;
            Instant paidAt = settled ? now : installment.paidAt();

            result.add(new LoanInstallment(
                    installment.id(), installment.loanId(), installment.number(), installment.dueDate(),
                    installment.principalDue(), installment.interestDue(), installment.totalDue(),
                    newPaid, newStatus, paidAt, installment.createdAt(), installment.updatedAt()
            ));
        }
        return result;
    }
}
