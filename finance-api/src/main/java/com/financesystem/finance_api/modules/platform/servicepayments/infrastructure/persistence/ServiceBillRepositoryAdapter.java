package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceBillRepositoryAdapter implements ServiceBillRepository {

    private final ServiceBillJpaRepository jpaRepository;

    public ServiceBillRepositoryAdapter(ServiceBillJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceBill save(ServiceBill serviceBill) {
        return toDomain(jpaRepository.save(toEntity(serviceBill)));
    }

    @Override
    public Optional<ServiceBill> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBill> findByIdForUpdate(UUID id) {
        return jpaRepository.findLockedById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBill> findByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod) {
        return jpaRepository.findByProviderIdAndServiceCustomerCodeAndBillingPeriod(providerId, serviceCustomerCode, billingPeriod).map(this::toDomain);
    }

    @Override
    public boolean existsByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod) {
        return jpaRepository.existsByProviderIdAndServiceCustomerCodeAndBillingPeriod(providerId, serviceCustomerCode, billingPeriod);
    }

    @Override
    public List<ServiceBill> findAllByProviderIdAndServiceCustomerCodeAndStatus(
            UUID providerId,
            String serviceCustomerCode,
            ServiceBillStatus status
    ) {
        Specification<ServiceBillEntity> specification = alwaysTrue()
                .and((root, query, cb) -> cb.equal(root.get("providerId"), providerId))
                .and((root, query, cb) -> cb.equal(root.get("serviceCustomerCode"), serviceCustomerCode))
                .and((root, query, cb) -> cb.equal(root.get("status"), status));

        return jpaRepository.findAll(specification).stream()
                .map(this::toDomain)
                .sorted(
                        Comparator.comparing(ServiceBill::dueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(ServiceBill::billingPeriod, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(ServiceBill::id, Comparator.nullsLast(Comparator.naturalOrder()))
                )
                .toList();
    }

    @Override
    public Page<ServiceBill> findAll(PlatformServiceBillFilter filter, Pageable pageable) {
        Specification<ServiceBillEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (StringUtils.hasText(filter.serviceCustomerCode())) {
                String code = filter.serviceCustomerCode().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("serviceCustomerCode")), "%" + code + "%"));
            }
            if (filter.status() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("status"), filter.status()));
            }
            if (StringUtils.hasText(filter.billingPeriod())) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("billingPeriod"), filter.billingPeriod().trim()));
            }
            if (filter.dueDateFrom() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("dueDate"), filter.dueDateFrom()));
            }
            if (filter.dueDateTo() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("dueDate"), filter.dueDateTo()));
            }
            if (StringUtils.hasText(filter.paidByTenantSlug())) {
                String slug = filter.paidByTenantSlug().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("paidByTenantSlug")), "%" + slug + "%"));
            }
            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCode")), like));
                    predicates.add(cb.like(cb.lower(root.get("customerName")), like));
                    predicates.add(cb.like(cb.lower(root.get("billingPeriod")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceBillEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceBillEntity toEntity(ServiceBill serviceBill) {
        ServiceBillEntity entity = new ServiceBillEntity();
        entity.setId(serviceBill.id());
        entity.setProviderId(serviceBill.providerId());
        entity.setServiceCustomerId(serviceBill.serviceCustomerId());
        entity.setServiceCustomerCode(serviceBill.serviceCustomerCode());
        entity.setCustomerName(serviceBill.customerName());
        entity.setBillingPeriod(serviceBill.billingPeriod());
        entity.setAmount(serviceBill.amount());
        entity.setCurrency(serviceBill.currency());
        entity.setDueDate(serviceBill.dueDate());
        entity.setStatus(serviceBill.status());
        entity.setPaidByTenantId(serviceBill.paidByTenantId());
        entity.setPaidByTenantSlug(serviceBill.paidByTenantSlug());
        entity.setPaidByUserId(serviceBill.paidByUserId());
        entity.setPaidByAccountId(serviceBill.paidByAccountId());
        entity.setPaidByAccountNumber(serviceBill.paidByAccountNumber());
        entity.setPaidTransactionId(serviceBill.paidTransactionId());
        entity.setPaidAt(serviceBill.paidAt());
        entity.setCreatedBySuperadminId(serviceBill.createdBySuperadminId());
        return entity;
    }

    private ServiceBill toDomain(ServiceBillEntity entity) {
        return new ServiceBill(
                entity.getId(),
                entity.getProviderId(),
                entity.getServiceCustomerId(),
                entity.getServiceCustomerCode(),
                entity.getCustomerName(),
                entity.getBillingPeriod(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getDueDate(),
                entity.getStatus(),
                entity.getPaidByTenantId(),
                entity.getPaidByTenantSlug(),
                entity.getPaidByUserId(),
                entity.getPaidByAccountId(),
                entity.getPaidByAccountNumber(),
                entity.getPaidTransactionId(),
                entity.getPaidAt(),
                entity.getCreatedBySuperadminId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
