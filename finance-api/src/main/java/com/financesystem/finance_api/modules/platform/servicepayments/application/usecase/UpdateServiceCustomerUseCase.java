package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.UpdateServiceCustomerRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UpdateServiceCustomerUseCase {

    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public UpdateServiceCustomerUseCase(
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public ServiceCustomerResponse execute(UUID id, UpdateServiceCustomerRequest request) {
        ServiceCustomer current = serviceCustomerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));

        if (!StringUtils.hasText(request.customerName()) && request.status() == null) {
            throw new BusinessException("At least one field must be provided");
        }

        String customerName = StringUtils.hasText(request.customerName()) ? request.customerName().trim() : current.customerName();
        var status = request.status() != null ? request.status() : current.status();

        ServiceCustomer updated = serviceCustomerRepository.save(new ServiceCustomer(
                current.id(),
                current.providerId(),
                current.serviceCustomerCode(),
                customerName,
                status,
                current.createdAt(),
                current.updatedAt()
        ));

        var provider = serviceProviderRepository.findById(current.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_CUSTOMER_UPDATED,
                "SERVICE_CUSTOMER",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "customerName", updated.customerName(),
                        "status", updated.status()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated, provider);
    }
}
