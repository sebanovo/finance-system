package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ChangeServiceProviderStatusRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangeServiceProviderStatusUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public ChangeServiceProviderStatusUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public ServiceProviderResponse execute(UUID id, ChangeServiceProviderStatusRequest request) {
        ServiceProvider current = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Service provider not found"));

        if (current.status() == request.status()) {
            return servicePaymentsMapper.toResponse(current);
        }

        ServiceProvider updated = serviceProviderRepository.save(new ServiceProvider(
                current.id(),
                current.code(),
                current.name(),
                current.category(),
                current.serviceCustomerCodeLabel(),
                request.status(),
                current.createdAt(),
                current.updatedAt()
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_PROVIDER_STATUS_CHANGED,
                "SERVICE_PROVIDER",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "status", updated.status()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated);
    }
}
