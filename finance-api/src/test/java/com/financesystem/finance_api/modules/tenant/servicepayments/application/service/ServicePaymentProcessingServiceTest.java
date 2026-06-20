package com.financesystem.finance_api.modules.tenant.servicepayments.application.service;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderSummaryResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.*;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.*;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentProcessingRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollment;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.repository.TenantServiceEnrollmentRepository;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionMovementResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionMovementResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.PaymentProcessingService;
import com.financesystem.finance_api.modules.tenant.fx.application.dto.TransactionFxDetailResponse;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ServicePaymentProcessingServiceTest {

    @Test
    void shouldProcessPaymentAndPersistBillPayment() {
        ServiceProviderRepository providerRepository = mock(ServiceProviderRepository.class);
        ServiceCustomerRepository customerRepository = mock(ServiceCustomerRepository.class);
        ServiceBillRepository billRepository = mock(ServiceBillRepository.class);
        ServiceBillPaymentRepository paymentRepository = mock(ServiceBillPaymentRepository.class);
        TenantServiceEnrollmentRepository enrollmentRepository = mock(TenantServiceEnrollmentRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        PaymentProcessingService paymentProcessingService = mock(PaymentProcessingService.class);
        ServicePaymentFailureAuditService failureAuditService = mock(ServicePaymentFailureAuditService.class);
        TenantServicePaymentsMapper mapper = mock(TenantServicePaymentsMapper.class);
        AuditTrailService auditTrailService = mock(AuditTrailService.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        ServicePaymentProcessingService service = new ServicePaymentProcessingService(
                providerRepository,
                customerRepository,
                billRepository,
                paymentRepository,
                enrollmentRepository,
                accountRepository,
                paymentProcessingService,
                failureAuditService,
                mapper,
                auditTrailService,
                jdbcTemplate
        );

        UUID tenantId = UUID.randomUUID();
        UUID actorUserId = UUID.randomUUID();
        UUID accountOwnerUserId = UUID.randomUUID();
        UUID billId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        Instant now = Instant.now();

        ServiceProvider provider = new ServiceProvider(
                providerId,
                "ELECTRICITY_CRE",
                "CRE",
                ServiceProviderCategory.ELECTRICITY,
                "Código",
                ServiceProviderStatus.ACTIVE,
                now,
                now
        );
        ServiceCustomer customer = new ServiceCustomer(
                customerId,
                providerId,
                "100001",
                "Carlos Mendoza",
                ServiceCustomerStatus.ACTIVE,
                now,
                now
        );
        ServiceBill bill = new ServiceBill(
                billId,
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
                now,
                now
        );
        Account account = new Account(
                accountId,
                accountOwnerUserId,
                "100000000123",
                AccountName.CHECKING_ACCOUNT,
                null,
                AccountType.CHECKING,
                CurrencyCode.BOB,
                new BigDecimal("500.00"),
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                null,
                true,
                true,
                now,
                null,
                now,
                now
        );
        TransactionResponse transactionResponse = new TransactionResponse(
                transactionId,
                "PAYMENT",
                "COMPLETED",
                "API",
                new BigDecimal("180.50"),
                "BOB",
                accountId,
                "100000000123",
                "Checking",
                null,
                null,
                null,
                null,
                "idempotency-1",
                "Service payment",
                null,
                actorUserId,
                null,
                now,
                now,
                now,
                null,
                List.of()
        );
        ServiceBillPayment savedPayment = new ServiceBillPayment(
                paymentId,
                billId,
                providerId,
                tenantId,
                "tenant_financruz",
                accountOwnerUserId,
                accountId,
                account.accountNumber(),
                transactionId,
                new BigDecimal("180.50"),
                "BOB",
                "SP-20260619-000001",
                "idempotency-1",
                ServiceBillPaymentStatus.PAID,
                now,
                now
        );
        ServiceBill updatedBill = new ServiceBill(
                billId,
                providerId,
                customerId,
                "100001",
                "Carlos Mendoza",
                "2026-06",
                new BigDecimal("180.50"),
                "BOB",
                LocalDate.of(2026, 6, 30),
                ServiceBillStatus.PAID,
                tenantId,
                "tenant_financruz",
                accountOwnerUserId,
                accountId,
                account.accountNumber(),
                transactionId,
                now,
                null,
                now,
                now
        );
        var expectedResponse = new com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse(
                paymentId,
                billId,
                transactionId,
                "SP-20260619-000001",
                new ServiceProviderSummaryResponse(providerId, "ELECTRICITY_CRE", "CRE", ServiceProviderCategory.ELECTRICITY),
                "100001",
                "Carlos Mendoza",
                "2026-06",
                new BigDecimal("180.50"),
                "BOB",
                "100000000123",
                ServiceBillStatus.PAID,
                now
        );

        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.ResultSetExtractor.class), eq("tenant_financruz")))
                .thenReturn(tenantId);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(1L);
        when(paymentRepository.findByIdempotencyKey(tenantId, accountOwnerUserId, "idempotency-1")).thenReturn(Optional.empty());
        when(billRepository.findByIdForUpdate(billId)).thenReturn(Optional.of(bill));
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(customerRepository.findByProviderAndCode(providerId, "100001")).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber("100000000123")).thenReturn(Optional.of(account));
        when(paymentProcessingService.createPayment(any(CreatePaymentTransactionRequest.class), eq(accountOwnerUserId))).thenReturn(transactionResponse);
        when(paymentRepository.save(any(ServiceBillPayment.class))).thenReturn(savedPayment);
        when(billRepository.save(any(ServiceBill.class))).thenReturn(updatedBill);
        when(mapper.toPaymentResponse(savedPayment, updatedBill, provider)).thenReturn(expectedResponse);

        var actual = service.process(new ServicePaymentProcessingRequest(
                actorUserId,
                accountOwnerUserId,
                "tenant_financruz",
                providerId,
                "100001",
                billId,
                null,
                "100000000123",
                "idempotency-1",
                com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel.API,
                AuditEventTypes.SERVICE_PAYMENT_CREATED,
                "SELF_SERVICE"
        ));

        assertSame(expectedResponse, actual);
        verify(paymentRepository).save(any(ServiceBillPayment.class));
        verify(billRepository).save(any(ServiceBill.class));
        verify(paymentProcessingService).createPayment(any(CreatePaymentTransactionRequest.class), eq(accountOwnerUserId));
        verify(paymentRepository).findByIdempotencyKey(tenantId, accountOwnerUserId, "idempotency-1");
    }

    @Test
    void shouldReuseExistingPaymentForSameIdempotencyKey() {
        ServiceProviderRepository providerRepository = mock(ServiceProviderRepository.class);
        ServiceCustomerRepository customerRepository = mock(ServiceCustomerRepository.class);
        ServiceBillRepository billRepository = mock(ServiceBillRepository.class);
        ServiceBillPaymentRepository paymentRepository = mock(ServiceBillPaymentRepository.class);
        TenantServiceEnrollmentRepository enrollmentRepository = mock(TenantServiceEnrollmentRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        PaymentProcessingService paymentProcessingService = mock(PaymentProcessingService.class);
        ServicePaymentFailureAuditService failureAuditService = mock(ServicePaymentFailureAuditService.class);
        TenantServicePaymentsMapper mapper = mock(TenantServicePaymentsMapper.class);
        AuditTrailService auditTrailService = mock(AuditTrailService.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        ServicePaymentProcessingService service = new ServicePaymentProcessingService(
                providerRepository,
                customerRepository,
                billRepository,
                paymentRepository,
                enrollmentRepository,
                accountRepository,
                paymentProcessingService,
                failureAuditService,
                mapper,
                auditTrailService,
                jdbcTemplate
        );

        UUID tenantId = UUID.randomUUID();
        UUID actorUserId = UUID.randomUUID();
        UUID billId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        Instant now = Instant.now();

        ServiceBillPayment existingPayment = new ServiceBillPayment(
                UUID.randomUUID(),
                billId,
                providerId,
                tenantId,
                "tenant_financruz",
                actorUserId,
                UUID.randomUUID(),
                "100000000123",
                UUID.randomUUID(),
                new BigDecimal("180.50"),
                "BOB",
                "SP-20260619-000001",
                "idempotency-1",
                ServiceBillPaymentStatus.PAID,
                now,
                now
        );
        ServiceBill bill = new ServiceBill(
                billId,
                providerId,
                UUID.randomUUID(),
                "100001",
                "Carlos Mendoza",
                "2026-06",
                new BigDecimal("180.50"),
                "BOB",
                LocalDate.of(2026, 6, 30),
                ServiceBillStatus.PAID,
                tenantId,
                "tenant_financruz",
                actorUserId,
                UUID.randomUUID(),
                "100000000123",
                existingPayment.paidTransactionId(),
                now,
                null,
                now,
                now
        );
        ServiceProvider provider = new ServiceProvider(
                providerId,
                "ELECTRICITY_CRE",
                "CRE",
                ServiceProviderCategory.ELECTRICITY,
                "Código",
                ServiceProviderStatus.ACTIVE,
                now,
                now
        );
        var expectedResponse = new com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse(
                existingPayment.id(),
                billId,
                existingPayment.paidTransactionId(),
                existingPayment.receiptNumber(),
                new ServiceProviderSummaryResponse(providerId, "ELECTRICITY_CRE", "CRE", ServiceProviderCategory.ELECTRICITY),
                "100001",
                "Carlos Mendoza",
                "2026-06",
                new BigDecimal("180.50"),
                "BOB",
                "100000000123",
                ServiceBillStatus.PAID,
                now
        );

        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.ResultSetExtractor.class), eq("tenant_financruz")))
                .thenReturn(tenantId);
        when(paymentRepository.findByIdempotencyKey(tenantId, actorUserId, "idempotency-1")).thenReturn(Optional.of(existingPayment));
        when(billRepository.findById(billId)).thenReturn(Optional.of(bill));
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(mapper.toPaymentResponse(existingPayment, bill, provider)).thenReturn(expectedResponse);

        var actual = service.process(new ServicePaymentProcessingRequest(
                actorUserId,
                actorUserId,
                "tenant_financruz",
                providerId,
                "100001",
                billId,
                null,
                "100000000123",
                "idempotency-1",
                com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel.API,
                AuditEventTypes.SERVICE_PAYMENT_CREATED,
                "SELF_SERVICE"
        ));

        assertSame(expectedResponse, actual);
        verify(paymentProcessingService, never()).createPayment(any(CreatePaymentTransactionRequest.class), any(UUID.class));
        verify(paymentRepository, never()).save(any(ServiceBillPayment.class));
        verify(billRepository, never()).findByIdForUpdate(any());
    }

    @Test
    void shouldRecordFailureAuditEvenWhenPaymentFailsBeforeDebit() {
        ServiceProviderRepository providerRepository = mock(ServiceProviderRepository.class);
        ServiceCustomerRepository customerRepository = mock(ServiceCustomerRepository.class);
        ServiceBillRepository billRepository = mock(ServiceBillRepository.class);
        ServiceBillPaymentRepository paymentRepository = mock(ServiceBillPaymentRepository.class);
        TenantServiceEnrollmentRepository enrollmentRepository = mock(TenantServiceEnrollmentRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        PaymentProcessingService paymentProcessingService = mock(PaymentProcessingService.class);
        ServicePaymentFailureAuditService failureAuditService = mock(ServicePaymentFailureAuditService.class);
        TenantServicePaymentsMapper mapper = mock(TenantServicePaymentsMapper.class);
        AuditTrailService auditTrailService = mock(AuditTrailService.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        ServicePaymentProcessingService service = new ServicePaymentProcessingService(
                providerRepository,
                customerRepository,
                billRepository,
                paymentRepository,
                enrollmentRepository,
                accountRepository,
                paymentProcessingService,
                failureAuditService,
                mapper,
                auditTrailService,
                jdbcTemplate
        );

        UUID tenantId = UUID.randomUUID();
        UUID actorUserId = UUID.randomUUID();
        UUID accountOwnerUserId = UUID.randomUUID();
        UUID billId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Instant now = Instant.now();

        ServiceProvider provider = new ServiceProvider(
                providerId,
                "ELECTRICITY_CRE",
                "CRE",
                ServiceProviderCategory.ELECTRICITY,
                "Código",
                ServiceProviderStatus.ACTIVE,
                now,
                now
        );
        ServiceCustomer customer = new ServiceCustomer(
                customerId,
                providerId,
                "100001",
                "Carlos Mendoza",
                ServiceCustomerStatus.ACTIVE,
                now,
                now
        );
        ServiceBill bill = new ServiceBill(
                billId,
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
                now,
                now
        );
        Account account = new Account(
                accountId,
                accountOwnerUserId,
                "100000000123",
                AccountName.CHECKING_ACCOUNT,
                null,
                AccountType.CHECKING,
                CurrencyCode.BOB,
                new BigDecimal("10.00"),
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                null,
                true,
                true,
                now,
                null,
                now,
                now
        );

        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.ResultSetExtractor.class), eq("tenant_financruz")))
                .thenReturn(tenantId);
        when(paymentRepository.findByIdempotencyKey(tenantId, accountOwnerUserId, "idempotency-1")).thenReturn(Optional.empty());
        when(billRepository.findByIdForUpdate(billId)).thenReturn(Optional.of(bill));
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(customerRepository.findByProviderAndCode(providerId, "100001")).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber("100000000123")).thenReturn(Optional.of(account));

        var exception = assertThrows(
                RuntimeException.class,
                () -> service.process(new ServicePaymentProcessingRequest(
                        actorUserId,
                        accountOwnerUserId,
                        "tenant_financruz",
                        providerId,
                        "100001",
                        billId,
                        null,
                        "100000000123",
                        "idempotency-1",
                        com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel.API,
                        AuditEventTypes.SERVICE_PAYMENT_CREATED,
                        "SELF_SERVICE"
                ))
        );

        assertEquals("Insufficient available balance", exception.getMessage());
        verify(failureAuditService).recordFailure(any(ServicePaymentProcessingRequest.class), eq("tenant_financruz"), eq(actorUserId), eq(accountOwnerUserId), any(RuntimeException.class));
        verify(paymentProcessingService, never()).createPayment(any(CreatePaymentTransactionRequest.class), any(UUID.class));
        verify(paymentRepository, never()).save(any(ServiceBillPayment.class));
    }
}
