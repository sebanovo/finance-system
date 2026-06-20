package com.financesystem.finance_api.modules.tenant.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.MyServiceEnrollmentFilter;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollment;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.repository.TenantServiceEnrollmentRepository;
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
public class TenantServiceEnrollmentRepositoryAdapter implements TenantServiceEnrollmentRepository {

    private final TenantServiceEnrollmentJpaRepository jpaRepository;

    public TenantServiceEnrollmentRepositoryAdapter(TenantServiceEnrollmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TenantServiceEnrollment save(TenantServiceEnrollment enrollment) {
        return toDomain(jpaRepository.save(toEntity(enrollment)));
    }

    @Override
    public Optional<TenantServiceEnrollment> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TenantServiceEnrollment> findByUserIdAndProviderIdAndServiceCustomerCode(UUID userId, UUID providerId, String serviceCustomerCode) {
        return jpaRepository.findByUserIdAndProviderIdAndServiceCustomerCode(userId, providerId, serviceCustomerCode).map(this::toDomain);
    }

    @Override
    public Page<TenantServiceEnrollment> findAll(MyServiceEnrollmentFilter filter, UUID userId, Pageable pageable) {
        Specification<TenantServiceEnrollmentEntity> specification = Specification.where((root, query, cb) ->
                cb.equal(root.get("userId"), userId));

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) -> cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (filter.category() != null) {
                specification = specification.and((root, query, cb) -> cb.equal(root.get("providerCategory"), filter.category().name()));
            }
            if (filter.status() != null) {
                specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), filter.status()));
            }
            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("providerCode")), like));
                    predicates.add(cb.like(cb.lower(root.get("providerName")), like));
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCode")), like));
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerName")), like));
                    predicates.add(cb.like(cb.lower(root.get("alias")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private TenantServiceEnrollmentEntity toEntity(TenantServiceEnrollment enrollment) {
        TenantServiceEnrollmentEntity entity = new TenantServiceEnrollmentEntity();
        entity.setId(enrollment.id());
        entity.setUserId(enrollment.userId());
        entity.setProviderId(enrollment.providerId());
        entity.setProviderCode(enrollment.providerCode());
        entity.setProviderName(enrollment.providerName());
        entity.setProviderCategory(enrollment.providerCategory());
        entity.setServiceCustomerCode(enrollment.serviceCustomerCode());
        entity.setServiceCustomerName(enrollment.serviceCustomerName());
        entity.setAlias(enrollment.alias());
        entity.setStatus(enrollment.status());
        return entity;
    }

    private TenantServiceEnrollment toDomain(TenantServiceEnrollmentEntity entity) {
        return new TenantServiceEnrollment(
                entity.getId(),
                entity.getUserId(),
                entity.getProviderId(),
                entity.getProviderCode(),
                entity.getProviderName(),
                entity.getProviderCategory(),
                entity.getServiceCustomerCode(),
                entity.getServiceCustomerName(),
                entity.getAlias(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
