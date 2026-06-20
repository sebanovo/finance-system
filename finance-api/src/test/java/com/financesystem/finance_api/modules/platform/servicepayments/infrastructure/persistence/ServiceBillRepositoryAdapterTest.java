package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceBillRepositoryAdapterTest {

    @Test
    void shouldDelegateLockedLookupToPessimisticQuery() {
        ServiceBillJpaRepository jpaRepository = mock(ServiceBillJpaRepository.class);
        ServiceBillRepositoryAdapter adapter = new ServiceBillRepositoryAdapter(jpaRepository);
        UUID billId = UUID.randomUUID();
        ServiceBillEntity entity = new ServiceBillEntity();
        ServiceBill expected = new ServiceBill(
                billId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "100001",
                "Cliente",
                "2026-06",
                java.math.BigDecimal.TEN,
                "BOB",
                java.time.LocalDate.now(),
                com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus.PENDING,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        entity.setId(billId);
        entity.setProviderId(expected.providerId());
        entity.setServiceCustomerId(expected.serviceCustomerId());
        entity.setServiceCustomerCode(expected.serviceCustomerCode());
        entity.setCustomerName(expected.customerName());
        entity.setBillingPeriod(expected.billingPeriod());
        entity.setAmount(expected.amount());
        entity.setCurrency(expected.currency());
        entity.setDueDate(expected.dueDate());
        entity.setStatus(expected.status());

        when(jpaRepository.findLockedById(billId)).thenReturn(Optional.of(entity));

        ServiceBill actual = adapter.findByIdForUpdate(billId).orElseThrow();

        assertSame(expected.id(), actual.id());
        verify(jpaRepository).findLockedById(billId);
    }
}
