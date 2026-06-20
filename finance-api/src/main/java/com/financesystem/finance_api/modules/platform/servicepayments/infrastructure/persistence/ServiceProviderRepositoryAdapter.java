package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
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
public class ServiceProviderRepositoryAdapter implements ServiceProviderRepository {

    private final ServiceProviderJpaRepository jpaRepository;

    public ServiceProviderRepositoryAdapter(ServiceProviderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceProvider save(ServiceProvider serviceProvider) {
        return toDomain(jpaRepository.save(toEntity(serviceProvider)));
    }

    @Override
    public Optional<ServiceProvider> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceProvider> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public Page<ServiceProvider> findAll(PlatformServiceProviderFilter filter, Pageable pageable) {
        Specification<ServiceProviderEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.category() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("category"), filter.category()));
            }

            if (filter.status() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("status"), filter.status()));
            }

            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("code")), like));
                    predicates.add(cb.like(cb.lower(root.get("name")), like));
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCodeLabel")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceProviderEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceProviderEntity toEntity(ServiceProvider serviceProvider) {
        ServiceProviderEntity entity = new ServiceProviderEntity();
        entity.setId(serviceProvider.id());
        entity.setCode(serviceProvider.code());
        entity.setName(serviceProvider.name());
        entity.setCategory(serviceProvider.category());
        entity.setServiceCustomerCodeLabel(serviceProvider.serviceCustomerCodeLabel());
        entity.setStatus(serviceProvider.status());
        return entity;
    }

    private ServiceProvider toDomain(ServiceProviderEntity entity) {
        return new ServiceProvider(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getCategory(),
                entity.getServiceCustomerCodeLabel(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
