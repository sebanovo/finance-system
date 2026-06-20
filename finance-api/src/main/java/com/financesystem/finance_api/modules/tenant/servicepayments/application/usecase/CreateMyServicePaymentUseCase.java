package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.CreateServicePaymentRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentProcessingRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.service.ServicePaymentProcessingService;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateMyServicePaymentUseCase {

    private final ServicePaymentProcessingService servicePaymentProcessingService;
    private final SecurityContextFacade securityContextFacade;

    public CreateMyServicePaymentUseCase(
            ServicePaymentProcessingService servicePaymentProcessingService,
            SecurityContextFacade securityContextFacade
    ) {
        this.servicePaymentProcessingService = servicePaymentProcessingService;
        this.securityContextFacade = securityContextFacade;
    }

    public ServicePaymentResponse execute(CreateServicePaymentRequest request) {
        UUID userId = currentUserId();
        return servicePaymentProcessingService.process(new ServicePaymentProcessingRequest(
                userId,
                userId,
                currentTenantSlug(),
                request.providerId(),
                request.serviceCustomerCode(),
                request.billId(),
                request.enrollmentId(),
                request.sourceAccountNumber(),
                request.idempotencyKey(),
                TransactionChannel.API,
                AuditEventTypes.SERVICE_PAYMENT_CREATED,
                "SELF_SERVICE"
        ));
    }

    private UUID currentUserId() {
        String subject = securityContextFacade.getCurrentSubject();
        if (subject == null || subject.isBlank()) {
            throw new BusinessException("Authenticated user is required");
        }
        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Authenticated user subject is invalid");
        }
    }

    private String currentTenantSlug() {
        String tenantSlug = securityContextFacade.getCurrentTenantSlug();
        if (tenantSlug == null || tenantSlug.isBlank()) {
            throw new BusinessException("Tenant is required");
        }
        return tenantSlug.trim();
    }
}
