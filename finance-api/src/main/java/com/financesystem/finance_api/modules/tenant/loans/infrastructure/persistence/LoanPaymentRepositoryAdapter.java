package com.financesystem.finance_api.modules.tenant.loans.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanPayment;
import com.financesystem.finance_api.modules.tenant.loans.domain.repository.LoanPaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class LoanPaymentRepositoryAdapter implements LoanPaymentRepository {

    private final LoanPaymentJpaRepository jpaRepository;

    public LoanPaymentRepositoryAdapter(LoanPaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LoanPayment save(LoanPayment payment) {
        return toDomain(jpaRepository.save(toEntity(payment)));
    }

    @Override
    public List<LoanPayment> findByLoanId(UUID loanId) {
        return jpaRepository.findByLoanIdOrderByPaidAtAsc(loanId).stream().map(this::toDomain).toList();
    }

    private LoanPaymentEntity toEntity(LoanPayment payment) {
        LoanPaymentEntity entity = new LoanPaymentEntity();
        entity.setId(payment.id());
        entity.setLoanId(payment.loanId());
        entity.setAmount(payment.amount());
        entity.setTransactionId(payment.transactionId());
        entity.setPaidAt(payment.paidAt());
        entity.setCreatedAt(payment.createdAt());
        return entity;
    }

    private LoanPayment toDomain(LoanPaymentEntity entity) {
        return new LoanPayment(
                entity.getId(),
                entity.getLoanId(),
                entity.getAmount(),
                entity.getTransactionId(),
                entity.getPaidAt(),
                entity.getCreatedAt()
        );
    }
}
