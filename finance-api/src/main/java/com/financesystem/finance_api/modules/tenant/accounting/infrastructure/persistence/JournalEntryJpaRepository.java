package com.financesystem.finance_api.modules.tenant.accounting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JournalEntryJpaRepository extends JpaRepository<JournalEntryEntity, UUID> {

    Optional<JournalEntryEntity> findByEntryNumber(String entryNumber);

    List<JournalEntryEntity> findAllByOrderByPostedAtDesc();

    List<JournalEntryEntity> findAllBySourceTransactionId(UUID sourceTransactionId);
}
