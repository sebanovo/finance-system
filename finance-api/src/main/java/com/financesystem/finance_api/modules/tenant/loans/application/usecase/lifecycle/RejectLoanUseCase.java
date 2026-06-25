package com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.audit.TenantAuditPayloads;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.mapper.LoanMapper;
import com.financesystem.finance_api.modules.tenant.loans.application.service.LoanNotificationService;
import com.financesystem.finance_api.modules.tenant.loans.domain.exception.InvalidLoanOperationException;
import com.financesystem.finance_api.modules.tenant.loans.domain.exception.LoanNotFoundException;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanStatus;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RejectLoanUseCase {

    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final AuditTrailService auditTrailService;
    private final LoanNotificationService loanNotificationService;

    public RejectLoanUseCase(
            LoanRepository loanRepository,
            LoanMapper loanMapper,
            AuditTrailService auditTrailService,
            LoanNotificationService loanNotificationService
    ) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.auditTrailService = auditTrailService;
        this.loanNotificationService = loanNotificationService;
    }

    @Transactional
    public LoanResponse execute(UUID loanId, String reason) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));

        if (loan.status() != LoanStatus.REQUESTED) {
            throw new InvalidLoanOperationException(
                    "Only REQUESTED loans can be rejected (current status: " + loan.status() + ")");
        }

        String normalizedReason = reason == null || reason.isBlank() ? null : reason.trim();

        Loan rejected = new Loan(
                loan.id(), loan.userId(), loan.accountId(), loan.principal(), loan.currency(),
                loan.annualInterestRate(), loan.termMonths(), loan.interestMethod(),
                LoanStatus.REJECTED, loan.purpose(), normalizedReason,
                loan.disbursedAt(), loan.closedAt(), loan.createdAt(), loan.updatedAt()
        );

        Loan saved = loanRepository.save(rejected);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LOAN_REJECTED,
                "LOAN",
                saved.id().toString(),
                TenantAuditPayloads.details("status", saved.status().name(), "reason", normalizedReason),
                TenantAuditPayloads.details("status", LoanStatus.REQUESTED.name()),
                TenantAuditPayloads.details("status", LoanStatus.REJECTED.name())
        );

        loanNotificationService.loanRejected(saved);

        return loanMapper.toResponse(saved, List.of());
    }
}
