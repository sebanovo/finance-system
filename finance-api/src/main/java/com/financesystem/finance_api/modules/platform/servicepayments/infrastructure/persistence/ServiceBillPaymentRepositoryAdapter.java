package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPayment;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceBillPaymentRepositoryAdapter implements ServiceBillPaymentRepository {

    private final ServiceBillPaymentJpaRepository jpaRepository;

    public ServiceBillPaymentRepositoryAdapter(ServiceBillPaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceBillPayment save(ServiceBillPayment serviceBillPayment) {
        return toDomain(jpaRepository.save(toEntity(serviceBillPayment)));
    }

    @Override
    public Optional<ServiceBillPayment> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBillPayment> findByBillId(UUID billId) {
        return jpaRepository.findByBillId(billId).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBillPayment> findByIdempotencyKey(UUID paidByTenantId, UUID paidByUserId, String idempotencyKey) {
        return jpaRepository.findByPaidByTenantIdAndPaidByUserIdAndIdempotencyKey(paidByTenantId, paidByUserId, idempotencyKey).map(this::toDomain);
    }

    @Override
    public boolean existsByBillId(UUID billId) {
        return jpaRepository.existsByBillId(billId);
    }

    @Override
    public Page<ServiceBillPayment> findAll(PlatformServiceBillPaymentFilter filter, Pageable pageable) {
        Specification<ServiceBillPaymentEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (StringUtils.hasText(filter.tenantSlug())) {
                String slug = filter.tenantSlug().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("paidByTenantSlug")), "%" + slug + "%"));
            }
            if (filter.userId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("paidByUserId"), filter.userId()));
            }
            if (StringUtils.hasText(filter.accountNumber())) {
                String accountNumber = filter.accountNumber().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("paidByAccountNumber")), "%" + accountNumber + "%"));
            }
            if (filter.billId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("billId"), filter.billId()));
            }
            if (StringUtils.hasText(filter.receiptNumber())) {
                String receipt = filter.receiptNumber().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("receiptNumber")), "%" + receipt + "%"));
            }
            if (filter.paidAtFrom() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("paidAt"), filter.paidAtFrom()));
            }
            if (filter.paidAtTo() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("paidAt"), filter.paidAtTo()));
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceBillPaymentEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceBillPaymentEntity toEntity(ServiceBillPayment payment) {
        ServiceBillPaymentEntity entity = new ServiceBillPaymentEntity();
        entity.setId(payment.id());
        entity.setBillId(payment.billId());
        entity.setProviderId(payment.providerId());
        entity.setPaidByTenantId(payment.paidByTenantId());
        entity.setPaidByTenantSlug(payment.paidByTenantSlug());
        entity.setPaidByUserId(payment.paidByUserId());
        entity.setPaidByAccountId(payment.paidByAccountId());
        entity.setPaidByAccountNumber(payment.paidByAccountNumber());
        entity.setPaidTransactionId(payment.paidTransactionId());
        entity.setAmount(payment.amount());
        entity.setCurrency(payment.currency());
        entity.setReceiptNumber(payment.receiptNumber());
        entity.setIdempotencyKey(payment.idempotencyKey());
        entity.setStatus(payment.status());
        entity.setPaidAt(payment.paidAt());
        return entity;
    }

    private ServiceBillPayment toDomain(ServiceBillPaymentEntity entity) {
        return new ServiceBillPayment(
                entity.getId(),
                entity.getBillId(),
                entity.getProviderId(),
                entity.getPaidByTenantId(),
                entity.getPaidByTenantSlug(),
                entity.getPaidByUserId(),
                entity.getPaidByAccountId(),
                entity.getPaidByAccountNumber(),
                entity.getPaidTransactionId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getReceiptNumber(),
                entity.getIdempotencyKey(),
                entity.getStatus(),
                entity.getPaidAt(),
                entity.getCreatedAt()
        );
    }
}
