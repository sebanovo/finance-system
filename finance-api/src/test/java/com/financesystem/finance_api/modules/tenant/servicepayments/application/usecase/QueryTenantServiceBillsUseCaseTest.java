package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderSummaryResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.*;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.QueryTenantServiceBillsRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.QueryServiceBillsResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QueryTenantServiceBillsUseCaseTest {

    @AfterEach
    void clearTenantContext() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldQueryPendingBillsForActiveProviderAndCustomer() {
        TenantContextHolder.set(new TenantContext("tenant_financruz", "tenant_financruz", false));

        ServiceProviderRepository providerRepository = mock(ServiceProviderRepository.class);
        ServiceCustomerRepository customerRepository = mock(ServiceCustomerRepository.class);
        ServiceBillRepository billRepository = mock(ServiceBillRepository.class);
        TenantServicePaymentsMapper mapper = new TenantServicePaymentsMapper();
        AuditTrailService auditTrailService = mock(AuditTrailService.class);

        QueryTenantServiceBillsUseCase useCase = new QueryTenantServiceBillsUseCase(
                providerRepository,
                customerRepository,
                billRepository,
                mapper,
                auditTrailService
        );

        UUID providerId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        ServiceProvider provider = new ServiceProvider(
                providerId,
                "ELECTRICITY_CRE",
                "CRE",
                ServiceProviderCategory.ELECTRICITY,
                "Código de suministro",
                ServiceProviderStatus.ACTIVE,
                Instant.now(),
                Instant.now()
        );
        ServiceCustomer customer = new ServiceCustomer(
                customerId,
                providerId,
                "100001",
                "Carlos Mendoza",
                ServiceCustomerStatus.ACTIVE,
                Instant.now(),
                Instant.now()
        );
        ServiceBill bill = new ServiceBill(
                UUID.randomUUID(),
                providerId,
                customerId,
                "100001",
                "Carlos Mendoza",
                "2026-06",
                new BigDecimal("180.50"),
                "BOB",
                LocalDate.of(2026, 6, 30),
                ServiceBillStatus.PENDING,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(customerRepository.findByProviderAndCode(providerId, "100001")).thenReturn(Optional.of(customer));
        when(billRepository.findAllByProviderIdAndServiceCustomerCodeAndStatus(providerId, "100001", ServiceBillStatus.PENDING))
                .thenReturn(List.of(bill));

        QueryServiceBillsResponse response = useCase.execute(new QueryTenantServiceBillsRequest(providerId, "100001"));

        assertEquals(providerId, response.provider().id());
        assertEquals("100001", response.serviceCustomerCode());
        assertEquals(1, response.bills().size());
        verify(auditTrailService).recordTenantEvent(
                eq(AuditEventTypes.SERVICE_BILL_QUERIED),
                eq("SERVICE_BILL"),
                eq(customerId.toString()),
                any()
        );
    }
}
