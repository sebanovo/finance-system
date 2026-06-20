package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServiceEnrollmentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.CreateServiceEnrollmentRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollment;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.repository.TenantServiceEnrollmentRepository;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class CreateMyServiceEnrollmentUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository;
    private final TenantUserRepository tenantUserRepository;
    private final com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;

    public CreateMyServiceEnrollmentUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository,
            TenantUserRepository tenantUserRepository,
            com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper tenantServicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.tenantServiceEnrollmentRepository = tenantServiceEnrollmentRepository;
        this.tenantUserRepository = tenantUserRepository;
        this.tenantServicePaymentsMapper = tenantServicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional
    public ServiceEnrollmentResponse execute(CreateServiceEnrollmentRequest request) {
        UUID userId = currentUserId();
        ServiceProvider provider = serviceProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        if (provider.status() != ServiceProviderStatus.ACTIVE) {
            throw new BusinessException("Service provider is inactive");
        }

        String customerCode = normalizeCode(request.serviceCustomerCode());
        var serviceCustomer = serviceCustomerRepository.findByProviderAndCode(provider.id(), customerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));

        if (serviceCustomer.status() != ServiceCustomerStatus.ACTIVE) {
            throw new BusinessException("Service customer is inactive");
        }

        if (tenantServiceEnrollmentRepository.findByUserIdAndProviderIdAndServiceCustomerCode(userId, provider.id(), customerCode).isPresent()) {
            throw new BusinessException("Service enrollment already exists");
        }

        TenantServiceEnrollment created = tenantServiceEnrollmentRepository.save(new TenantServiceEnrollment(
                null,
                userId,
                provider.id(),
                provider.code(),
                provider.name(),
                provider.category().name(),
                customerCode,
                serviceCustomer.customerName(),
                StringUtils.hasText(request.alias()) ? request.alias().trim() : null,
                TenantServiceEnrollmentStatus.ACTIVE,
                null,
                null
        ));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.SERVICE_ENROLLMENT_CREATED,
                "SERVICE_ENROLLMENT",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "tenantSlug", TenantContextHolder.getRequired().tenantSlug(),
                        "providerId", provider.id(),
                        "providerCode", provider.code(),
                        "serviceCustomerCode", created.serviceCustomerCode(),
                        "alias", created.alias(),
                        "status", created.status()
                ),
                null,
                created
        );

        return tenantServicePaymentsMapper.toEnrollmentResponse(created);
    }

    private UUID currentUserId() {
        String subject = securityContextFacade.getCurrentSubject();
        if (!StringUtils.hasText(subject)) {
            throw new BusinessException("Authenticated user is required");
        }

        try {
            UUID userId = UUID.fromString(subject.trim());
            if (tenantUserRepository.findById(userId).isEmpty()) {
                throw new ResourceNotFoundException("Tenant user not found");
            }
            return userId;
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Authenticated user subject is invalid");
        }
    }

    private String normalizeCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Service customer code must not be blank");
        }
        return value.trim();
    }
}
