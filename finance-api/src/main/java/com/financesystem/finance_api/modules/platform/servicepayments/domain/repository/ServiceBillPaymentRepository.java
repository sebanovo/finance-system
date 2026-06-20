package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceBillPaymentRepository {

    ServiceBillPayment save(ServiceBillPayment serviceBillPayment);

    Optional<ServiceBillPayment> findById(UUID id);

    Optional<ServiceBillPayment> findByBillId(UUID billId);

    Optional<ServiceBillPayment> findByIdempotencyKey(UUID paidByTenantId, UUID paidByUserId, String idempotencyKey);

    boolean existsByBillId(UUID billId);

    Page<ServiceBillPayment> findAll(PlatformServiceBillPaymentFilter filter, Pageable pageable);
}
