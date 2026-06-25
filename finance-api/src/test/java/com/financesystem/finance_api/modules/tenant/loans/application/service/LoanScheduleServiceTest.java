package com.financesystem.finance_api.modules.tenant.loans.application.service;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.InterestMethod;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallment;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanScheduleServiceTest {

    private final LoanScheduleService service = new LoanScheduleService();
    private final LocalDate firstDue = LocalDate.of(2026, 1, 15);

    private Loan loan(String principal, String rate, int term, InterestMethod method) {
        return new Loan(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal(principal), CurrencyCode.BOB, new BigDecimal(rate), term, method,
                LoanStatus.APPROVED, null, null, null, null, null, null
        );
    }

    private BigDecimal sum(List<LoanInstallment> items, java.util.function.Function<LoanInstallment, BigDecimal> f) {
        return items.stream().map(f).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Test
    void flatScheduleSplitsPrincipalAndInterestEvenly() {
        List<LoanInstallment> schedule = service.generateSchedule(loan("1200.00", "12.00", 6, InterestMethod.FLAT), firstDue);

        assertEquals(6, schedule.size());
        // interest = 1200 * 12% * (6/12) = 72.00 ; principal/n = 200 ; interest/n = 12
        assertEquals(new BigDecimal("1200.00"), sum(schedule, LoanInstallment::principalDue));
        assertEquals(new BigDecimal("72.00"), sum(schedule, LoanInstallment::interestDue));
        assertEquals(new BigDecimal("1272.00"), sum(schedule, LoanInstallment::totalDue));
        assertEquals(new BigDecimal("200.00"), schedule.get(0).principalDue());
        assertEquals(new BigDecimal("12.00"), schedule.get(0).interestDue());
        assertEquals(1, schedule.get(0).number());
        assertEquals(firstDue, schedule.get(0).dueDate());
        assertEquals(firstDue.plusMonths(5), schedule.get(5).dueDate());
    }

    @Test
    void flatScheduleLastInstallmentAbsorbsRoundingRemainder() {
        // principal 1000 / 3 = 333.33 -> last absorbs remainder so total == 1000.00
        List<LoanInstallment> schedule = service.generateSchedule(loan("1000.00", "10.00", 3, InterestMethod.FLAT), firstDue);

        assertEquals(3, schedule.size());
        assertEquals(new BigDecimal("1000.00"), sum(schedule, LoanInstallment::principalDue));
        // interest = 1000 * 10% * (3/12) = 25.00
        assertEquals(new BigDecimal("25.00"), sum(schedule, LoanInstallment::interestDue));
        assertEquals(new BigDecimal("333.34"), schedule.get(2).principalDue());
    }

    @Test
    void frenchScheduleHasFixedInstallmentAndClearsPrincipal() {
        List<LoanInstallment> schedule = service.generateSchedule(loan("1000.00", "12.00", 12, InterestMethod.FRENCH), firstDue);

        assertEquals(12, schedule.size());
        // Principal must sum exactly to the loan amount.
        assertEquals(new BigDecimal("1000.00"), sum(schedule, LoanInstallment::principalDue));
        // French amortization charges interest, so total > principal.
        assertTrue(sum(schedule, LoanInstallment::interestDue).compareTo(BigDecimal.ZERO) > 0);
        // Each total = principal + interest.
        for (LoanInstallment i : schedule) {
            assertEquals(i.principalDue().add(i.interestDue()), i.totalDue());
        }
    }

    @Test
    void frenchWithZeroRateFallsBackToFlatPrincipalOnly() {
        List<LoanInstallment> schedule = service.generateSchedule(loan("1200.00", "0.00", 6, InterestMethod.FRENCH), firstDue);

        assertEquals(new BigDecimal("1200.00"), sum(schedule, LoanInstallment::principalDue));
        assertEquals(new BigDecimal("0.00"), sum(schedule, LoanInstallment::interestDue));
    }
}
