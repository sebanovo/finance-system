package com.financesystem.finance_api.modules.tenant.loans.application.mapper;

import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanDetailResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanInstallmentResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanResponse;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class LoanMapper {

    public LoanResponse toResponse(Loan loan, List<LoanInstallment> installments) {
        BigDecimal totalDue = installments.stream()
                .map(LoanInstallment::totalDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = installments.stream()
                .map(LoanInstallment::paidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = totalDue.subtract(totalPaid);

        return new LoanResponse(
                loan.id(),
                loan.userId(),
                loan.accountId(),
                loan.principal(),
                loan.currency().name(),
                loan.annualInterestRate(),
                loan.termMonths(),
                loan.interestMethod() == null ? null : loan.interestMethod().name(),
                loan.status().name(),
                loan.purpose(),
                loan.statusReason(),
                totalDue,
                totalPaid,
                outstanding,
                loan.disbursedAt(),
                loan.closedAt(),
                loan.createdAt(),
                loan.updatedAt()
        );
    }

    public LoanInstallmentResponse toInstallmentResponse(LoanInstallment installment) {
        return new LoanInstallmentResponse(
                installment.id(),
                installment.number(),
                installment.dueDate(),
                installment.principalDue(),
                installment.interestDue(),
                installment.totalDue(),
                installment.paidAmount(),
                installment.status().name(),
                installment.paidAt()
        );
    }

    public LoanDetailResponse toDetailResponse(Loan loan, List<LoanInstallment> installments) {
        return new LoanDetailResponse(
                toResponse(loan, installments),
                installments.stream().map(this::toInstallmentResponse).toList()
        );
    }
}
