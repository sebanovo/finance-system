package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CreateServiceBillRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Service
public class CreateServiceBillUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public CreateServiceBillUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceBillRepository serviceBillRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    @Transactional
    public ServiceBillResponse execute(CreateServiceBillRequest request) {
        var provider = serviceProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        if (provider.status() != ServiceProviderStatus.ACTIVE) {
            throw new BusinessException("Service provider is inactive");
        }

        String code = normalizeCode(request.serviceCustomerCode());
        var serviceCustomer = serviceCustomerRepository.findByProviderAndCode(provider.id(), code)
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));

        if (serviceCustomer.status() != ServiceCustomerStatus.ACTIVE) {
            throw new BusinessException("Service customer is inactive");
        }

        String billingPeriod = request.billingPeriod().trim();
        if (serviceBillRepository.existsByProviderCodeAndBillingPeriod(provider.id(), code, billingPeriod)) {
            throw new BusinessException("Service bill already exists");
        }

        ServiceBill created = serviceBillRepository.save(new ServiceBill(
                null,
                provider.id(),
                serviceCustomer.id(),
                code,
                serviceCustomer.customerName(),
                billingPeriod,
                request.amount(),
                request.currency().name(),
                request.dueDate(),
                ServiceBillStatus.PENDING,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                currentSuperadminId(),
                null,
                null
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_BILL_CREATED,
                "SERVICE_BILL",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "providerId", provider.id(),
                        "providerCode", provider.code(),
                        "serviceCustomerCode", created.serviceCustomerCode(),
                        "billingPeriod", created.billingPeriod(),
                        "amount", created.amount(),
                        "currency", created.currency(),
                        "dueDate", created.dueDate(),
                        "status", created.status(),
                        "createdBy", currentSuperadminId()
                ),
                null,
                created
        );

        return servicePaymentsMapper.toResponse(created, provider);
    }

    private String normalizeCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Service customer code must not be blank");
        }

        return value.trim();
    }

    private java.util.UUID currentSuperadminId() {
        String email = securityContextFacade.getCurrentEmail();
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return platformSuperadminRepository.findByEmail(email.trim().toLowerCase())
                .map(com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin::id)
                .orElse(null);
    }
}
