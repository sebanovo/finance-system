package com.financesystem.finance_api.modules.tenant.accounting.application.mapper;

import com.financesystem.finance_api.modules.tenant.accounting.application.dto.AccountingPeriodResponse;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.JournalEntryResponse;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.JournalLineResponse;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriod;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.JournalEntry;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.JournalLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountingMapper {

    public AccountingPeriodResponse toResponse(AccountingPeriod period) {
        return new AccountingPeriodResponse(
                period.id(),
                period.periodCode(),
                period.periodType().name(),
                period.status().name(),
                period.startDate(),
                period.endDate(),
                period.closedAt(),
                period.description(),
                period.createdAt(),
                period.updatedAt()
        );
    }

    public JournalEntryResponse toResponse(JournalEntry entry) {
        return new JournalEntryResponse(
                entry.id(),
                entry.entryNumber(),
                entry.sourceTransactionId(),
                entry.entryType().name(),
                entry.status().name(),
                entry.description(),
                entry.reference(),
                entry.totalDebits(),
                entry.totalCredits(),
                balanced(entry.totalDebits(), entry.totalCredits()),
                entry.postedAt(),
                entry.createdAt(),
                entry.updatedAt(),
                entry.lines().stream().map(this::toLineResponse).toList()
        );
    }

    public JournalLineResponse toLineResponse(JournalLine line) {
        return new JournalLineResponse(
                line.id(),
                line.lineNo(),
                line.accountCode(),
                line.accountName(),
                line.lineType().name(),
                line.amount(),
                line.currency(),
                line.description(),
                line.createdAt()
        );
    }

    private boolean balanced(BigDecimal debits, BigDecimal credits) {
        BigDecimal safeDebits = debits == null ? BigDecimal.ZERO : debits;
        BigDecimal safeCredits = credits == null ? BigDecimal.ZERO : credits;
        return safeDebits.compareTo(safeCredits) == 0;
    }
}
