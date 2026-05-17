package com.financesystem.finance_api.modules.tenant.accounting.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.accounting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.JournalEntryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JournalEntryRepositoryAdapter implements JournalEntryRepository {

    private final JournalEntryJpaRepository jpaRepository;

    public JournalEntryRepositoryAdapter(JournalEntryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public JournalEntry save(JournalEntry journalEntry) {
        JournalEntryEntity entity = toEntity(journalEntry);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<JournalEntry> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<JournalEntry> findAll() {
        return jpaRepository.findAllByOrderByPostedAtDesc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<JournalEntry> findAllByTransactionId(UUID transactionId) {
        return jpaRepository.findAllBySourceTransactionId(transactionId).stream().map(this::toDomain).toList();
    }

    private JournalEntryEntity toEntity(JournalEntry journalEntry) {
        JournalEntryEntity entity = new JournalEntryEntity();
        entity.setId(journalEntry.id());
        entity.setEntryNumber(journalEntry.entryNumber());
        entity.setSourceTransactionId(journalEntry.sourceTransactionId());
        entity.setPeriodId(journalEntry.periodId());
        entity.setEntryType(journalEntry.entryType().name());
        entity.setStatus(journalEntry.status().name());
        entity.setDescription(journalEntry.description());
        entity.setReference(journalEntry.reference());
        entity.setTotalDebits(journalEntry.totalDebits());
        entity.setTotalCredits(journalEntry.totalCredits());
        entity.setPostedAt(journalEntry.postedAt());
        List<JournalLineEntity> lines = journalEntry.lines().stream().map(this::toEntity).toList();
        lines.forEach(line -> line.setJournalEntry(entity));
        entity.setLines(lines);
        return entity;
    }

    private JournalLineEntity toEntity(JournalLine line) {
        JournalLineEntity entity = new JournalLineEntity();
        entity.setId(line.id());
        entity.setLineNo(line.lineNo());
        entity.setAccountCode(line.accountCode());
        entity.setAccountName(line.accountName());
        entity.setLineType(line.lineType().name());
        entity.setAmount(line.amount());
        entity.setCurrency(line.currency());
        entity.setDescription(line.description());
        return entity;
    }

    private JournalEntry toDomain(JournalEntryEntity entity) {
        List<JournalLineEntity> lineEntities = entity.getLines() == null ? List.of() : entity.getLines();

        return new JournalEntry(
                entity.getId(),
                entity.getEntryNumber(),
                entity.getSourceTransactionId(),
                entity.getPeriodId(),
                JournalEntryType.valueOf(entity.getEntryType()),
                JournalEntryStatus.valueOf(entity.getStatus()),
                entity.getDescription(),
                entity.getReference(),
                entity.getTotalDebits(),
                entity.getTotalCredits(),
                entity.getPostedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                lineEntities.stream().map(this::toDomain).toList()
        );
    }

    private JournalLine toDomain(JournalLineEntity entity) {
        return new JournalLine(
                entity.getId(),
                null,
                entity.getLineNo(),
                entity.getAccountCode(),
                entity.getAccountName(),
                JournalLineType.valueOf(entity.getLineType()),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }
}
