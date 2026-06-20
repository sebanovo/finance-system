package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CancelServiceBillRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class CancelServiceBillUseCase {

    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public CancelServiceBillUseCase(
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    @Transactional
    public ServiceBillResponse execute(UUID id, CancelServiceBillRequest request) {
        ServiceBill current = serviceBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));

        if (current.status() != ServiceBillStatus.PENDING) {
            throw new BusinessException("Service bill is not pending");
        }

        ServiceBill updated = serviceBillRepository.save(new ServiceBill(
                current.id(),
                current.providerId(),
                current.serviceCustomerId(),
                current.serviceCustomerCode(),
                current.customerName(),
                current.billingPeriod(),
                current.amount(),
                current.currency(),
                current.dueDate(),
                ServiceBillStatus.CANCELLED,
                current.paidByTenantId(),
                current.paidByTenantSlug(),
                current.paidByUserId(),
                current.paidByAccountId(),
                current.paidByAccountNumber(),
                current.paidTransactionId(),
                current.paidAt(),
                current.createdBySuperadminId(),
                current.createdAt(),
                current.updatedAt()
        ));

        var provider = serviceProviderRepository.findById(current.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_BILL_CANCELLED,
                "SERVICE_BILL",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "reason", StringUtils.hasText(request.reason()) ? request.reason().trim() : null,
                        "cancelledBy", currentSuperadminId()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated, provider);
    }

    private UUID currentSuperadminId() {
        String email = securityContextFacade.getCurrentEmail();
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return platformSuperadminRepository.findByEmail(email.trim().toLowerCase())
                .map(com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin::id)
                .orElse(null);
    }
}
