package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServiceEnrollmentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollment;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.repository.TenantServiceEnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class DeleteMyServiceEnrollmentUseCase {

    private final TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository;
    private final TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;

    public DeleteMyServiceEnrollmentUseCase(
            TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository,
            TenantServicePaymentsMapper tenantServicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade
    ) {
        this.tenantServiceEnrollmentRepository = tenantServiceEnrollmentRepository;
        this.tenantServicePaymentsMapper = tenantServicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional
    public ServiceEnrollmentResponse execute(UUID enrollmentId) {
        UUID userId = currentUserId();
        TenantServiceEnrollment current = tenantServiceEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Service enrollment not found"));

        if (!current.userId().equals(userId)) {
            throw new BusinessException("Service enrollment does not belong to the current user");
        }

        if (current.status() == TenantServiceEnrollmentStatus.INACTIVE) {
            return tenantServicePaymentsMapper.toEnrollmentResponse(current);
        }

        TenantServiceEnrollment updated = tenantServiceEnrollmentRepository.save(new TenantServiceEnrollment(
                current.id(),
                current.userId(),
                current.providerId(),
                current.providerCode(),
                current.providerName(),
                current.providerCategory(),
                current.serviceCustomerCode(),
                current.serviceCustomerName(),
                current.alias(),
                TenantServiceEnrollmentStatus.INACTIVE,
                current.createdAt(),
                current.updatedAt()
        ));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.SERVICE_ENROLLMENT_DELETED,
                "SERVICE_ENROLLMENT",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "status", updated.status()
                ),
                current,
                updated
        );

        return tenantServicePaymentsMapper.toEnrollmentResponse(updated);
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
}
