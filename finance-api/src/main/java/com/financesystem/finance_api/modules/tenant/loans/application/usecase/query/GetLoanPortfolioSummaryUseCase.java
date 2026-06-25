package com.financesystem.finance_api.modules.tenant.loans.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanSummaryResponse;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanStatus;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanInstallmentRepository;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GetLoanPortfolioSummaryUseCase {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    public GetLoanPortfolioSummaryUseCase(
            LoanRepository loanRepository,
            LoanInstallmentRepository loanInstallmentRepository
    ) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
    }

    @Transactional(readOnly = true)
    public LoanSummaryResponse execute() {
        List<Loan> loans = loanRepository.findAll();

        long requested = count(loans, LoanStatus.REQUESTED);
        long approved = count(loans, LoanStatus.APPROVED);
        long disbursed = count(loans, LoanStatus.DISBURSED);
        long paidOff = count(loans, LoanStatus.PAID_OFF);
        long rejected = count(loans, LoanStatus.REJECTED);

        BigDecimal totalDisbursedPrincipal = loans.stream()
                .filter(l -> l.status() == LoanStatus.DISBURSED || l.status() == LoanStatus.PAID_OFF)
                .map(Loan::principal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = loans.stream()
                .filter(l -> l.status() == LoanStatus.DISBURSED)
                .map(l -> loanInstallmentRepository.findByLoanIdOrderByNumber(l.id()).stream()
                        .map(i -> i.totalDue().subtract(i.paidAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new LoanSummaryResponse(
                loans.size(), requested, approved, disbursed, paidOff, rejected,
                totalDisbursedPrincipal, totalOutstanding
        );
    }

    private long count(List<Loan> loans, LoanStatus status) {
        return loans.stream().filter(l -> l.status() == status).count();
    }
}
