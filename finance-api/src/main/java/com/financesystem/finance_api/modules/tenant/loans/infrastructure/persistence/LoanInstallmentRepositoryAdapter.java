package com.financesystem.finance_api.modules.tenant.loans.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallment;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallmentStatus;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanInstallmentRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class LoanInstallmentRepositoryAdapter implements LoanInstallmentRepository {

    private final LoanInstallmentJpaRepository jpaRepository;

    public LoanInstallmentRepositoryAdapter(LoanInstallmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LoanInstallment save(LoanInstallment installment) {
        return toDomain(jpaRepository.save(toEntity(installment)));
    }

    @Override
    public List<LoanInstallment> saveAll(List<LoanInstallment> installments) {
        return jpaRepository.saveAll(installments.stream().map(this::toEntity).toList())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<LoanInstallment> findByLoanIdOrderByNumber(UUID loanId) {
        return jpaRepository.findByLoanIdOrderByNumberAsc(loanId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<LoanInstallment> findOverdueCandidates(LocalDate today) {
        return jpaRepository.findByStatusInAndDueDateBefore(
                        List.of(LoanInstallmentStatus.PENDING, LoanInstallmentStatus.PARTIAL), today)
                .stream().map(this::toDomain).toList();
    }

    private LoanInstallmentEntity toEntity(LoanInstallment installment) {
        LoanInstallmentEntity entity = new LoanInstallmentEntity();
        entity.setId(installment.id());
        entity.setLoanId(installment.loanId());
        entity.setNumber(installment.number());
        entity.setDueDate(installment.dueDate());
        entity.setPrincipalDue(installment.principalDue());
        entity.setInterestDue(installment.interestDue());
        entity.setTotalDue(installment.totalDue());
        entity.setPaidAmount(installment.paidAmount());
        entity.setStatus(installment.status());
        entity.setPaidAt(installment.paidAt());
        entity.setCreatedAt(installment.createdAt());
        entity.setUpdatedAt(installment.updatedAt());
        return entity;
    }

    private LoanInstallment toDomain(LoanInstallmentEntity entity) {
        return new LoanInstallment(
                entity.getId(),
                entity.getLoanId(),
                entity.getNumber(),
                entity.getDueDate(),
                entity.getPrincipalDue(),
                entity.getInterestDue(),
                entity.getTotalDue(),
                entity.getPaidAmount(),
                entity.getStatus(),
                entity.getPaidAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
