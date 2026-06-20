package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CreateServiceProviderRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class CreateServiceProviderUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;

    public CreateServiceProviderUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional
    public ServiceProviderResponse execute(CreateServiceProviderRequest request) {
        String code = normalizeCode(request.code());
        if (serviceProviderRepository.existsByCode(code)) {
            throw new BusinessException("Service provider already exists");
        }

        String customerCodeLabel = StringUtils.hasText(request.serviceCustomerCodeLabel())
                ? request.serviceCustomerCodeLabel().trim()
                : "Código de cliente";

        ServiceProvider created = serviceProviderRepository.save(new ServiceProvider(
                null,
                code,
                request.name().trim(),
                request.category(),
                customerCodeLabel,
                ServiceProviderStatus.ACTIVE,
                null,
                null
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_PROVIDER_CREATED,
                "SERVICE_PROVIDER",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "code", created.code(),
                        "name", created.name(),
                        "category", created.category(),
                        "serviceCustomerCodeLabel", created.serviceCustomerCodeLabel(),
                        "status", created.status(),
                        "createdBy", securityContextFacade.getCurrentEmail()
                ),
                null,
                created
        );

        return servicePaymentsMapper.toResponse(created);
    }

    private String normalizeCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Service provider code must not be blank");
        }

        return value.trim().toUpperCase();
    }
}
