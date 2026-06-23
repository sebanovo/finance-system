package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void shouldReturnBillsOrderedByOldestDueDateFirst() {
        ServiceBillJpaRepository jpaRepository = mock(ServiceBillJpaRepository.class);
        ServiceBillRepositoryAdapter adapter = new ServiceBillRepositoryAdapter(jpaRepository);
        UUID providerId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        ServiceBillEntity july = new ServiceBillEntity();
        july.setId(UUID.randomUUID());
        july.setProviderId(providerId);
        july.setServiceCustomerId(customerId);
        july.setServiceCustomerCode("100001");
        july.setCustomerName("Cliente");
        july.setBillingPeriod("2026-07");
        july.setAmount(BigDecimal.TEN);
        july.setCurrency("BOB");
        july.setDueDate(LocalDate.of(2026, 7, 10));
        july.setStatus(com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus.PENDING);
        ServiceBillEntity june = new ServiceBillEntity();
        june.setId(UUID.randomUUID());
        june.setProviderId(providerId);
        june.setServiceCustomerId(customerId);
        june.setServiceCustomerCode("100001");
        june.setCustomerName("Cliente");
        june.setBillingPeriod("2026-06");
        june.setAmount(BigDecimal.TEN);
        june.setCurrency("BOB");
        june.setDueDate(LocalDate.of(2026, 6, 10));
        june.setStatus(com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus.PENDING);

        when(jpaRepository.findAll(org.mockito.ArgumentMatchers.<Specification<ServiceBillEntity>>any()))
                .thenReturn(List.of(july, june));

        List<ServiceBill> actual = adapter.findAllByProviderIdAndServiceCustomerCodeAndStatus(
                providerId,
                "100001",
                com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus.PENDING
        );

        assertEquals("2026-06", actual.get(0).billingPeriod());
        assertEquals("2026-07", actual.get(1).billingPeriod());
    }
}
