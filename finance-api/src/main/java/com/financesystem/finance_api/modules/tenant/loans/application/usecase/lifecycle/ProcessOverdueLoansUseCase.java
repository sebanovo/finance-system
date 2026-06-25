package com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle;

import com.financesystem.finance_api.modules.tenant.loans.application.dto.ProcessOverdueLoansResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.service.LoanNotificationService;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallment;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallmentStatus;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanStatus;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanInstallmentRepository;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Flags unpaid installments past their due date as OVERDUE and notifies the
 * borrower. Idempotent: already-OVERDUE installments are not re-processed.
 * Designed to be triggered on-demand (admin endpoint) or by a future
 * per-tenant scheduled job.
 */
@Service
public class ProcessOverdueLoansUseCase {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final LoanNotificationService loanNotificationService;

    public ProcessOverdueLoansUseCase(
            LoanRepository loanRepository,
            LoanInstallmentRepository loanInstallmentRepository,
            LoanNotificationService loanNotificationService
    ) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.loanNotificationService = loanNotificationService;
    }

    @Transactional
    public ProcessOverdueLoansResponse execute() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<LoanInstallment> candidates = loanInstallmentRepository.findOverdueCandidates(today);

        Map<UUID, List<LoanInstallment>> byLoan = new LinkedHashMap<>();
        for (LoanInstallment candidate : candidates) {
            byLoan.computeIfAbsent(candidate.loanId(), k -> new ArrayList<>()).add(candidate);
        }

        int overdueCount = 0;
        int affectedLoans = 0;

        for (Map.Entry<UUID, List<LoanInstallment>> entry : byLoan.entrySet()) {
            Loan loan = loanRepository.findById(entry.getKey()).orElse(null);
            if (loan == null || loan.status() != LoanStatus.DISBURSED) {
                continue;
            }

            List<LoanInstallment> updated = entry.getValue().stream()
                    .map(this::markOverdue)
                    .toList();
            loanInstallmentRepository.saveAll(updated);
            overdueCount += updated.size();
            affectedLoans++;

            // Notify once per loan, referencing the earliest overdue installment.
            updated.stream()
                    .min((a, b) -> Integer.compare(a.number(), b.number()))
                    .ifPresent(earliest -> loanNotificationService.installmentOverdue(loan, earliest));
        }

        return new ProcessOverdueLoansResponse(overdueCount, affectedLoans);
    }

    private LoanInstallment markOverdue(LoanInstallment i) {
        return new LoanInstallment(
                i.id(), i.loanId(), i.number(), i.dueDate(),
                i.principalDue(), i.interestDue(), i.totalDue(), i.paidAmount(),
                LoanInstallmentStatus.OVERDUE, i.paidAt(), i.createdAt(), i.updatedAt()
        );
    }
}
