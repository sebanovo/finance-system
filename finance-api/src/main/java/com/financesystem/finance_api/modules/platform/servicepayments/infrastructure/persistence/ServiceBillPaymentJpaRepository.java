package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceBillPaymentJpaRepository extends JpaRepository<ServiceBillPaymentEntity, UUID>, JpaSpecificationExecutor<ServiceBillPaymentEntity> {

    Optional<ServiceBillPaymentEntity> findByBillId(UUID billId);

    Optional<ServiceBillPaymentEntity> findByPaidByTenantIdAndPaidByUserIdAndIdempotencyKey(UUID paidByTenantId, UUID paidByUserId, String idempotencyKey);

    boolean existsByBillId(UUID billId);
}
