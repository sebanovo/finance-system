package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderSummaryResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.QueryServiceBillsRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.QueryServiceBillsResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollment;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.repository.TenantServiceEnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class QueryMyServiceBillsUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository;
    private final TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;

    public QueryMyServiceBillsUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceBillRepository serviceBillRepository,
            TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository,
            TenantServicePaymentsMapper tenantServicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.tenantServiceEnrollmentRepository = tenantServiceEnrollmentRepository;
        this.tenantServicePaymentsMapper = tenantServicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional(readOnly = true)
    public QueryServiceBillsResponse execute(QueryServiceBillsRequest request) {
        UUID userId = currentUserId();
        QueryContext context = resolveContext(userId, request);

        ServiceProvider provider = serviceProviderRepository.findById(context.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));
        if (provider.status() != ServiceProviderStatus.ACTIVE) {
            throw new BusinessException("Service provider is inactive");
        }

        var serviceCustomer = serviceCustomerRepository.findByProviderAndCode(provider.id(), context.serviceCustomerCode())
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));
        if (serviceCustomer.status() != ServiceCustomerStatus.ACTIVE) {
            throw new BusinessException("Service customer is inactive");
        }

        List<com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill> bills =
                serviceBillRepository.findAllByProviderIdAndServiceCustomerCodeAndStatus(
                        provider.id(),
                        context.serviceCustomerCode(),
                        ServiceBillStatus.PENDING
                );

        auditTrailService.recordTenantEvent(
                AuditEventTypes.SERVICE_BILL_QUERIED,
                "SERVICE_BILL",
                serviceCustomer.id().toString(),
                PlatformAuditPayloads.details(
                        "tenantSlug", TenantContextHolder.getRequired().tenantSlug(),
                        "providerId", provider.id(),
                        "serviceCustomerCode", context.serviceCustomerCode(),
                        "mode", context.mode()
                ),
                null,
                null
        );

        return tenantServicePaymentsMapper.toBillsResponse(
                provider,
                context.serviceCustomerCode(),
                serviceCustomer.customerName(),
                bills
        );
    }

    private QueryContext resolveContext(UUID userId, QueryServiceBillsRequest request) {
        boolean hasEnrollment = request.enrollmentId() != null;
        boolean hasManual = StringUtils.hasText(request.serviceCustomerCode()) && request.providerId() != null;

        if (hasEnrollment == hasManual) {
            throw new BusinessException("Send either enrollmentId or providerId + serviceCustomerCode");
        }

        if (hasEnrollment) {
            TenantServiceEnrollment enrollment = tenantServiceEnrollmentRepository.findById(request.enrollmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service enrollment not found"));

            if (!enrollment.userId().equals(userId)) {
                throw new BusinessException("Service enrollment does not belong to the current user");
            }

            if (enrollment.status() != TenantServiceEnrollmentStatus.ACTIVE) {
                throw new BusinessException("Service enrollment is inactive");
            }

            return new QueryContext(enrollment.providerId(), enrollment.serviceCustomerCode(), "ENROLLMENT");
        }

        return new QueryContext(request.providerId(), request.serviceCustomerCode().trim(), "MANUAL");
    }

    private UUID currentUserId() {
        String subject = securityContextFacade.getCurrentSubject();
        if (!StringUtils.hasText(subject)) {
            throw new BusinessException("Authenticated user is required");
        }

        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Authenticated user subject is invalid");
        }
    }

    private record QueryContext(UUID providerId, String serviceCustomerCode, String mode) {
    }
}
