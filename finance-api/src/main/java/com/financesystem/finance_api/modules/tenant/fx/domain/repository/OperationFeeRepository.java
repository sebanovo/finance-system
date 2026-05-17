package com.financesystem.finance_api.modules.tenant.fx.domain.repository;

import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.OperationFee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationFeeRepository {

    OperationFee save(OperationFee operationFee);

    Optional<OperationFee> findById(UUID id);

    Optional<OperationFee> findByOperationCode(FxOperationCode operationCode);

    List<OperationFee> findAll();

    void deleteById(UUID id);
}
