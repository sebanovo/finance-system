package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CreateServiceCustomerRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CreateServiceCustomerUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public CreateServiceCustomerUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    @Transactional
    public ServiceCustomerResponse execute(CreateServiceCustomerRequest request) {
        var provider = serviceProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        if (provider.status() != com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus.ACTIVE) {
            throw new BusinessException("Service provider is inactive");
        }

        String code = normalizeCode(request.serviceCustomerCode());
        if (serviceCustomerRepository.existsByProviderAndCode(provider.id(), code)) {
            throw new BusinessException("Service customer already exists");
        }

        ServiceCustomer created = serviceCustomerRepository.save(new ServiceCustomer(
                null,
                provider.id(),
                code,
                request.customerName().trim(),
                ServiceCustomerStatus.ACTIVE,
                null,
                null
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_CUSTOMER_CREATED,
                "SERVICE_CUSTOMER",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "providerId", provider.id(),
                        "providerCode", provider.code(),
                        "serviceCustomerCode", created.serviceCustomerCode(),
                        "customerName", created.customerName(),
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
