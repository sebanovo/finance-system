package com.financesystem.finance_api.modules.tenant.accounting.application.usecase.journal;

import com.financesystem.finance_api.modules.tenant.accounting.application.dto.JournalEntryResponse;
import com.financesystem.finance_api.modules.tenant.accounting.application.mapper.AccountingMapper;
import com.financesystem.finance_api.modules.tenant.accounting.domain.exception.JournalEntryNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetJournalEntryByIdUseCase {

    private final JournalEntryRepository journalEntryRepository;
    private final AccountingMapper accountingMapper;

    public GetJournalEntryByIdUseCase(
            JournalEntryRepository journalEntryRepository,
            AccountingMapper accountingMapper
    ) {
        this.journalEntryRepository = journalEntryRepository;
        this.accountingMapper = accountingMapper;
    }

    @Transactional(readOnly = true)
    public JournalEntryResponse execute(UUID id) {
        return journalEntryRepository.findById(id)
                .map(accountingMapper::toResponse)
                .orElseThrow(() -> new JournalEntryNotFoundException("Journal entry not found with id: " + id));
    }
}
