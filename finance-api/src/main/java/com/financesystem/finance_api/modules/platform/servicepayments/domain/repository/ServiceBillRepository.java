package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceBillRepository {

    ServiceBill save(ServiceBill serviceBill);

    Optional<ServiceBill> findById(UUID id);

    Optional<ServiceBill> findByIdForUpdate(UUID id);

    Optional<ServiceBill> findByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);

    boolean existsByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);

    List<ServiceBill> findAllByProviderIdAndServiceCustomerCodeAndStatus(
            UUID providerId,
            String serviceCustomerCode,
            ServiceBillStatus status
    );

    Page<ServiceBill> findAll(PlatformServiceBillFilter filter, Pageable pageable);
}
