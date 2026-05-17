package com.financesystem.finance_api.modules.tenant.accounting.domain.repository;

import com.financesystem.finance_api.modules.tenant.accounting.domain.model.JournalEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JournalEntryRepository {

    JournalEntry save(JournalEntry journalEntry);

    Optional<JournalEntry> findById(UUID id);

    List<JournalEntry> findAll();

    List<JournalEntry> findAllByTransactionId(UUID transactionId);
}
