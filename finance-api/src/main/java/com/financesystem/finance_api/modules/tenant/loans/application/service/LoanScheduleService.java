package com.financesystem.finance_api.modules.tenant.loans.application.service;

import com.financesystem.finance_api.modules.tenant.loans.domain.model.InterestMethod;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallment;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallmentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the repayment schedule (installments) for a loan. The last installment
 * absorbs any rounding remainder so the sum of installments equals the exact
 * principal and total interest.
 */
@Service
public class LoanScheduleService {

    private static final int MONEY_SCALE = 2;
    private static final int CALC_SCALE = 12;
    private static final BigDecimal MONTHS_PER_YEAR = BigDecimal.valueOf(12);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public List<LoanInstallment> generateSchedule(Loan loan, LocalDate firstDueDate) {
        InterestMethod method = loan.interestMethod() == null ? InterestMethod.FLAT : loan.interestMethod();
        return switch (method) {
            case FRENCH -> generateFrench(loan, firstDueDate);
            case FLAT -> generateFlat(loan, firstDueDate);
        };
    }

    private List<LoanInstallment> generateFlat(Loan loan, LocalDate firstDueDate) {
        int n = loan.termMonths();
        BigDecimal principal = loan.principal().setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal totalInterest = principal
                .multiply(loan.annualInterestRate().divide(HUNDRED, CALC_SCALE, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(n).divide(MONTHS_PER_YEAR, CALC_SCALE, RoundingMode.HALF_UP))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal basePrincipal = principal.divide(BigDecimal.valueOf(n), MONEY_SCALE, RoundingMode.DOWN);
        BigDecimal baseInterest = totalInterest.divide(BigDecimal.valueOf(n), MONEY_SCALE, RoundingMode.DOWN);

        List<LoanInstallment> installments = new ArrayList<>(n);
        BigDecimal principalAccum = BigDecimal.ZERO;
        BigDecimal interestAccum = BigDecimal.ZERO;

        for (int i = 1; i <= n; i++) {
            BigDecimal principalDue;
            BigDecimal interestDue;
            if (i < n) {
                principalDue = basePrincipal;
                interestDue = baseInterest;
                principalAccum = principalAccum.add(basePrincipal);
                interestAccum = interestAccum.add(baseInterest);
            } else {
                principalDue = principal.subtract(principalAccum);
                interestDue = totalInterest.subtract(interestAccum);
            }
            installments.add(buildInstallment(loan, i, firstDueDate.plusMonths(i - 1L), principalDue, interestDue));
        }
        return installments;
    }

    private List<LoanInstallment> generateFrench(Loan loan, LocalDate firstDueDate) {
        int n = loan.termMonths();
        BigDecimal principal = loan.principal().setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = loan.annualInterestRate()
                .divide(HUNDRED, CALC_SCALE, RoundingMode.HALF_UP)
                .divide(MONTHS_PER_YEAR, CALC_SCALE, RoundingMode.HALF_UP);

        if (monthlyRate.signum() == 0) {
            return generateFlat(loan, firstDueDate);
        }

        // A = P * i / (1 - (1+i)^-n)
        BigDecimal onePlusI = BigDecimal.ONE.add(monthlyRate);
        BigDecimal denom = BigDecimal.ONE.subtract(pow(onePlusI, -n));
        BigDecimal installmentAmount = principal.multiply(monthlyRate)
                .divide(denom, MONEY_SCALE, RoundingMode.HALF_UP);

        List<LoanInstallment> installments = new ArrayList<>(n);
        BigDecimal balance = principal;

        for (int i = 1; i <= n; i++) {
            BigDecimal interestDue = balance.multiply(monthlyRate).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            BigDecimal principalDue;
            if (i < n) {
                principalDue = installmentAmount.subtract(interestDue);
                balance = balance.subtract(principalDue);
            } else {
                principalDue = balance;
                balance = BigDecimal.ZERO;
            }
            installments.add(buildInstallment(loan, i, firstDueDate.plusMonths(i - 1L), principalDue, interestDue));
        }
        return installments;
    }

    private LoanInstallment buildInstallment(
            Loan loan,
            int number,
            LocalDate dueDate,
            BigDecimal principalDue,
            BigDecimal interestDue
    ) {
        BigDecimal p = principalDue.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal interest = interestDue.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        return new LoanInstallment(
                null,
                loan.id(),
                number,
                dueDate,
                p,
                interest,
                p.add(interest),
                BigDecimal.ZERO,
                LoanInstallmentStatus.PENDING,
                null,
                null,
                null
        );
    }

    private BigDecimal pow(BigDecimal base, int exponent) {
        if (exponent >= 0) {
            return base.pow(exponent);
        }
        return BigDecimal.ONE.divide(base.pow(-exponent), CALC_SCALE, RoundingMode.HALF_UP);
    }
}
