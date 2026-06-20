package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.UpdateServiceProviderRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UpdateServiceProviderUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public UpdateServiceProviderUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public ServiceProviderResponse execute(UUID id, UpdateServiceProviderRequest request) {
        ServiceProvider current = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Service provider not found"));

        String name = StringUtils.hasText(request.name()) ? request.name().trim() : current.name();
        String label = StringUtils.hasText(request.serviceCustomerCodeLabel())
                ? request.serviceCustomerCodeLabel().trim()
                : current.serviceCustomerCodeLabel();
        var category = request.category() != null ? request.category() : current.category();

        ServiceProvider updated = serviceProviderRepository.save(new ServiceProvider(
                current.id(),
                current.code(),
                name,
                category,
                label,
                current.status(),
                current.createdAt(),
                current.updatedAt()
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_PROVIDER_UPDATED,
                "SERVICE_PROVIDER",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "id", updated.id(),
                        "code", updated.code(),
                        "name", updated.name(),
                        "category", updated.category(),
                        "serviceCustomerCodeLabel", updated.serviceCustomerCodeLabel(),
                        "status", updated.status()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated);
    }
}
