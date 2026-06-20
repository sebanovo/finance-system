package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceCustomerFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
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
public class ServiceCustomerRepositoryAdapter implements ServiceCustomerRepository {

    private final ServiceCustomerJpaRepository jpaRepository;

    public ServiceCustomerRepositoryAdapter(ServiceCustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceCustomer save(ServiceCustomer serviceCustomer) {
        return toDomain(jpaRepository.save(toEntity(serviceCustomer)));
    }

    @Override
    public Optional<ServiceCustomer> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceCustomer> findByProviderAndCode(UUID providerId, String serviceCustomerCode) {
        return jpaRepository.findByProviderIdAndServiceCustomerCode(providerId, serviceCustomerCode).map(this::toDomain);
    }

    @Override
    public boolean existsByProviderAndCode(UUID providerId, String serviceCustomerCode) {
        return jpaRepository.existsByProviderIdAndServiceCustomerCode(providerId, serviceCustomerCode);
    }

    @Override
    public Page<ServiceCustomer> findAll(PlatformServiceCustomerFilter filter, Pageable pageable) {
        Specification<ServiceCustomerEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (filter.status() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("status"), filter.status()));
            }
            if (StringUtils.hasText(filter.serviceCustomerCode())) {
                String code = filter.serviceCustomerCode().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("serviceCustomerCode")), "%" + code + "%"));
            }
            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCode")), like));
                    predicates.add(cb.like(cb.lower(root.get("customerName")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceCustomerEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceCustomerEntity toEntity(ServiceCustomer serviceCustomer) {
        ServiceCustomerEntity entity = new ServiceCustomerEntity();
        entity.setId(serviceCustomer.id());
        entity.setProviderId(serviceCustomer.providerId());
        entity.setServiceCustomerCode(serviceCustomer.serviceCustomerCode());
        entity.setCustomerName(serviceCustomer.customerName());
        entity.setStatus(serviceCustomer.status());
        return entity;
    }

    private ServiceCustomer toDomain(ServiceCustomerEntity entity) {
        return new ServiceCustomer(
                entity.getId(),
                entity.getProviderId(),
                entity.getServiceCustomerCode(),
                entity.getCustomerName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
