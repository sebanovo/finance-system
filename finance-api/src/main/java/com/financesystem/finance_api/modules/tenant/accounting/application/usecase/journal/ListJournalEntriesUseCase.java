package com.financesystem.finance_api.modules.tenant.accounting.application.usecase.journal;

import com.financesystem.finance_api.modules.tenant.accounting.application.dto.JournalEntryResponse;
import com.financesystem.finance_api.modules.tenant.accounting.application.mapper.AccountingMapper;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListJournalEntriesUseCase {

    private final JournalEntryRepository journalEntryRepository;
    private final AccountingMapper accountingMapper;

    public ListJournalEntriesUseCase(
            JournalEntryRepository journalEntryRepository,
            AccountingMapper accountingMapper
    ) {
        this.journalEntryRepository = journalEntryRepository;
        this.accountingMapper = accountingMapper;
    }

    @Transactional(readOnly = true)
    public List<JournalEntryResponse> execute() {
        return journalEntryRepository.findAll()
                .stream()
                .map(accountingMapper::toResponse)
                .toList();
    }
}
