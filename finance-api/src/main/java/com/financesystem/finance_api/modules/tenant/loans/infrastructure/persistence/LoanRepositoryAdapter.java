package com.financesystem.finance_api.modules.tenant.loans.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LoanRepositoryAdapter implements LoanRepository {

    private final LoanJpaRepository jpaRepository;

    public LoanRepositoryAdapter(LoanJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Loan save(Loan loan) {
        return toDomain(jpaRepository.save(toEntity(loan)));
    }

    @Override
    public Optional<Loan> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Loan> findAllByUserId(UUID userId) {
        return jpaRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDomain).toList();
    }

    private LoanEntity toEntity(Loan loan) {
        LoanEntity entity = new LoanEntity();
        entity.setId(loan.id());
        entity.setUserId(loan.userId());
        entity.setAccountId(loan.accountId());
        entity.setPrincipal(loan.principal());
        entity.setCurrency(loan.currency());
        entity.setAnnualInterestRate(loan.annualInterestRate());
        entity.setTermMonths(loan.termMonths());
        entity.setInterestMethod(loan.interestMethod());
        entity.setStatus(loan.status());
        entity.setPurpose(loan.purpose());
        entity.setStatusReason(loan.statusReason());
        entity.setDisbursedAt(loan.disbursedAt());
        entity.setClosedAt(loan.closedAt());
        entity.setCreatedAt(loan.createdAt());
        entity.setUpdatedAt(loan.updatedAt());
        return entity;
    }

    private Loan toDomain(LoanEntity entity) {
        return new Loan(
                entity.getId(),
                entity.getUserId(),
                entity.getAccountId(),
                entity.getPrincipal(),
                entity.getCurrency(),
                entity.getAnnualInterestRate(),
                entity.getTermMonths(),
                entity.getInterestMethod(),
                entity.getStatus(),
                entity.getPurpose(),
                entity.getStatusReason(),
                entity.getDisbursedAt(),
                entity.getClosedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
