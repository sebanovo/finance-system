package com.financesystem.finance_api.modules.tenant.fx.infrastructure.persistence;

import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeCalculationMode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeType;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.OperationFee;
import com.financesystem.finance_api.modules.tenant.fx.domain.repository.OperationFeeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OperationFeeRepositoryAdapter implements OperationFeeRepository {

    private final OperationFeeJpaRepository jpaRepository;

    public OperationFeeRepositoryAdapter(OperationFeeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public OperationFee save(OperationFee operationFee) {
        return toDomain(jpaRepository.save(toEntity(operationFee)));
    }

    @Override
    public Optional<OperationFee> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<OperationFee> findByOperationCode(FxOperationCode operationCode) {
        return jpaRepository.findByOperationCode(operationCode.name()).map(this::toDomain);
    }

    @Override
    public List<OperationFee> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private OperationFeeEntity toEntity(OperationFee fee) {
        OperationFeeEntity entity = new OperationFeeEntity();
        entity.setId(fee.id());
        entity.setOperationCode(fee.operationCode().name());
        entity.setFeeType(fee.feeType().name());
        entity.setFeeValue(fee.feeValue());
        entity.setCalculationMode(fee.calculationMode().name());
        entity.setActive(fee.active());
        entity.setDescription(fee.description());
        entity.setCreatedAt(fee.createdAt());
        entity.setUpdatedAt(fee.updatedAt());
        return entity;
    }

    private OperationFee toDomain(OperationFeeEntity entity) {
        return new OperationFee(
                entity.getId(),
                FxOperationCode.valueOf(entity.getOperationCode()),
                FeeType.valueOf(entity.getFeeType()),
                entity.getFeeValue(),
                FeeCalculationMode.valueOf(entity.getCalculationMode()),
                entity.isActive(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
