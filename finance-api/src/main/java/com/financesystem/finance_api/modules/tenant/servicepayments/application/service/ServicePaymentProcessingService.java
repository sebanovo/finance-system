package com.financesystem.finance_api.modules.tenant.servicepayments.application.service;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.*;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentProcessingRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollment;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.repository.TenantServiceEnrollmentRepository;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.PaymentProcessingService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.Instant;
import java.util.UUID;

@Service
public class ServicePaymentProcessingService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository;
    private final AccountRepository accountRepository;
    private final PaymentProcessingService paymentProcessingService;
    private final ServicePaymentFailureAuditService servicePaymentFailureAuditService;
    private final TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final JdbcTemplate jdbcTemplate;

    public ServicePaymentProcessingService(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceBillRepository serviceBillRepository,
            ServiceBillPaymentRepository serviceBillPaymentRepository,
            TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository,
            AccountRepository accountRepository,
            PaymentProcessingService paymentProcessingService,
            ServicePaymentFailureAuditService servicePaymentFailureAuditService,
            TenantServicePaymentsMapper tenantServicePaymentsMapper,
            AuditTrailService auditTrailService,
            JdbcTemplate jdbcTemplate
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.serviceBillPaymentRepository = serviceBillPaymentRepository;
        this.tenantServiceEnrollmentRepository = tenantServiceEnrollmentRepository;
        this.accountRepository = accountRepository;
        this.paymentProcessingService = paymentProcessingService;
        this.servicePaymentFailureAuditService = servicePaymentFailureAuditService;
        this.tenantServicePaymentsMapper = tenantServicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public ServicePaymentResponse process(ServicePaymentProcessingRequest request) {
        UUID actorUserId = require(request.actorUserId(), "Actor user is required");
        UUID accountOwnerUserId = require(request.accountOwnerUserId(), "Account owner user is required");
        String tenantSlug = normalizeTenantSlug(request.tenantSlug());
        String idempotencyKey = normalize(request.idempotencyKey());
        String serviceCustomerCode = normalize(request.serviceCustomerCode());

        UUID tenantId = resolveTenantId(tenantSlug);

        ServiceBillPayment existing = serviceBillPaymentRepository.findByIdempotencyKey(tenantId, accountOwnerUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            if (!existing.billId().equals(request.billId())) {
                throw new BusinessException("Idempotency key has already been used for another payment");
            }
            return mapPayment(existing);
        }

        try {
            if (request.enrollmentId() != null) {
                TenantServiceEnrollment enrollment = tenantServiceEnrollmentRepository.findById(request.enrollmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Service enrollment not found"));
                if (!enrollment.userId().equals(accountOwnerUserId)) {
                    throw new BusinessException("Service enrollment does not belong to the target user");
                }
                if (enrollment.status() != TenantServiceEnrollmentStatus.ACTIVE) {
                    throw new BusinessException("Service enrollment is inactive");
                }
            }

            ServiceBill bill = serviceBillRepository.findByIdForUpdate(request.billId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));
            if (bill.status() != ServiceBillStatus.PENDING) {
                throw new BusinessException("Service bill is not pending");
            }

            ServiceProvider provider = serviceProviderRepository.findById(request.providerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));
            if (provider.status() != ServiceProviderStatus.ACTIVE) {
                throw new BusinessException("Service provider is inactive");
            }

            if (!bill.providerId().equals(provider.id()) || !bill.serviceCustomerCode().equals(serviceCustomerCode)) {
                throw new BusinessException("Bill provider or service customer code does not match");
            }

            ServiceCustomer serviceCustomer = serviceCustomerRepository.findByProviderAndCode(provider.id(), bill.serviceCustomerCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));
            if (serviceCustomer.status() != ServiceCustomerStatus.ACTIVE) {
                throw new BusinessException("Service customer is inactive");
            }

            Account account = accountRepository.findByAccountNumber(normalize(request.sourceAccountNumber()))
                    .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
            if (!account.userId().equals(accountOwnerUserId)) {
                throw new BusinessException("Source account does not belong to the target user");
            }
            if (!account.active() || account.status() != AccountStatus.ACTIVE) {
                throw new BusinessException("Source account must be active");
            }
            if (!account.currency().name().equalsIgnoreCase(bill.currency())) {
                throw new BusinessException("Source account currency does not match bill currency");
            }
            if (account.availableBalance().compareTo(bill.amount()) < 0) {
                throw new BusinessException("Insufficient available balance");
            }

            auditTrailService.recordTenantEvent(
                    request.createdAuditEventType(),
                    "SERVICE_PAYMENT",
                    bill.id().toString(),
                    PlatformAuditPayloads.details(
                            "tenantSlug", tenantSlug,
                            "billId", bill.id(),
                            "providerId", provider.id(),
                            "serviceCustomerCode", bill.serviceCustomerCode(),
                            "amount", bill.amount(),
                            "currency", bill.currency(),
                            "sourceAccountNumber", account.accountNumber(),
                            "actorUserId", actorUserId,
                            "accountOwnerUserId", accountOwnerUserId,
                            "idempotencyKey", idempotencyKey,
                            "mode", request.paymentMode()
                    )
            );

            CreatePaymentTransactionRequest paymentRequest = new CreatePaymentTransactionRequest(
                    account.id(),
                    bill.amount(),
                    com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode.valueOf(bill.currency()),
                    request.transactionChannel(),
                    bill.serviceCustomerCode() + " - " + bill.customerName(),
                    "Service payment for " + provider.code() + " / " + bill.billingPeriod(),
                    idempotencyKey
            );

            TransactionResponse transaction = paymentProcessingService.createPayment(paymentRequest, accountOwnerUserId);
            String receiptNumber = nextReceiptNumber();
            Instant now = Instant.now();

            ServiceBillPayment savedPayment = serviceBillPaymentRepository.save(new ServiceBillPayment(
                    null,
                    bill.id(),
                    provider.id(),
                    tenantId,
                    tenantSlug,
                    accountOwnerUserId,
                    account.id(),
                    account.accountNumber(),
                    transaction.id(),
                    bill.amount(),
                    bill.currency(),
                    receiptNumber,
                    idempotencyKey,
                    ServiceBillPaymentStatus.PAID,
                    now,
                    now
            ));

            ServiceBill updatedBill = serviceBillRepository.save(new ServiceBill(
                    bill.id(),
                    bill.providerId(),
                    bill.serviceCustomerId(),
                    bill.serviceCustomerCode(),
                    bill.customerName(),
                    bill.billingPeriod(),
                    bill.amount(),
                    bill.currency(),
                    bill.dueDate(),
                    ServiceBillStatus.PAID,
                    tenantId,
                    tenantSlug,
                    accountOwnerUserId,
                    account.id(),
                    account.accountNumber(),
                    transaction.id(),
                    now,
                    bill.createdBySuperadminId(),
                    bill.createdAt(),
                    bill.updatedAt()
            ));

            auditTrailService.recordTenantEvent(
                    AuditEventTypes.SERVICE_PAYMENT_COMPLETED,
                    "SERVICE_PAYMENT",
                    savedPayment.id().toString(),
                    PlatformAuditPayloads.details(
                            "tenantSlug", tenantSlug,
                            "billId", bill.id(),
                            "providerId", provider.id(),
                            "serviceCustomerCode", bill.serviceCustomerCode(),
                            "amount", bill.amount(),
                            "currency", bill.currency(),
                            "sourceAccountNumber", account.accountNumber(),
                            "transactionId", transaction.id(),
                            "receiptNumber", receiptNumber,
                            "actorUserId", actorUserId,
                            "accountOwnerUserId", accountOwnerUserId,
                            "mode", request.paymentMode()
                    ),
                    bill,
                    updatedBill
            );

            return tenantServicePaymentsMapper.toPaymentResponse(savedPayment, updatedBill, provider);
        } catch (RuntimeException exception) {
            servicePaymentFailureAuditService.recordFailure(
                    request,
                    tenantSlug,
                    actorUserId,
                    accountOwnerUserId,
                    exception
            );
            throw exception;
        }
    }

    private ServicePaymentResponse mapPayment(ServiceBillPayment payment) {
        ServiceBill bill = serviceBillRepository.findById(payment.billId())
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));
        ServiceProvider provider = serviceProviderRepository.findById(payment.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));
        return tenantServicePaymentsMapper.toPaymentResponse(payment, bill, provider);
    }

    private UUID resolveTenantId(String tenantSlug) {
        UUID tenantId = jdbcTemplate.query(
                "SELECT id FROM public.platform_tenants WHERE slug = ? AND active = true",
                rs -> rs.next() ? (UUID) rs.getObject("id") : null,
                tenantSlug
        );

        if (tenantId == null) {
            throw new ResourceNotFoundException("Tenant not found");
        }

        return tenantId;
    }

    private String nextReceiptNumber() {
        Long sequenceValue = jdbcTemplate.queryForObject("SELECT nextval('public.service_payment_receipt_seq')", Long.class);
        if (sequenceValue == null) {
            throw new BusinessException("Unable to generate receipt number");
        }

        return "SP-" + java.time.LocalDate.now().toString().replace("-", "") + "-" + String.format("%06d", sequenceValue);
    }

    private UUID require(UUID value, String message) {
        if (value == null) {
            throw new BusinessException(message);
        }
        return value;
    }

    private String normalizeTenantSlug(String value) {
        String normalized = normalize(value);
        return normalized;
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Required value is missing");
        }
        return value.trim();
    }
}
