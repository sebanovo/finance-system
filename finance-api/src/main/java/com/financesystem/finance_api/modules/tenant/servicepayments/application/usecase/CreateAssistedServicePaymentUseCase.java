package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.CreateAssistedServicePaymentRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentProcessingRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.service.ServicePaymentProcessingService;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import org.springframework.stereotype.Service;

/**
 * Legacy compatibility use case.
 *
 * The active contract for bank/cashbox payments is {@link CreateBankServicePaymentUseCase}.
 */
@Deprecated(forRemoval = false)
@Service
public class CreateAssistedServicePaymentUseCase {

    private final ServicePaymentProcessingService servicePaymentProcessingService;
    private final SecurityContextFacade securityContextFacade;

    public CreateAssistedServicePaymentUseCase(
            ServicePaymentProcessingService servicePaymentProcessingService,
            SecurityContextFacade securityContextFacade
    ) {
        this.servicePaymentProcessingService = servicePaymentProcessingService;
        this.securityContextFacade = securityContextFacade;
    }

    public ServicePaymentResponse execute(CreateAssistedServicePaymentRequest request) {
        if (request == null || request.userId() == null) {
            throw new BusinessException("Target user is required");
        }

        return servicePaymentProcessingService.process(new ServicePaymentProcessingRequest(
                currentUserId(),
                request.userId(),
                currentTenantSlug(),
                request.providerId(),
                request.serviceCustomerCode(),
                request.billId(),
                null,
                request.sourceAccountNumber(),
                request.idempotencyKey(),
                TransactionChannel.CASHBOX,
                AuditEventTypes.SERVICE_PAYMENT_ASSISTED_CREATED,
                "ASSISTED"
        ));
    }

    private java.util.UUID currentUserId() {
        String subject = securityContextFacade.getCurrentSubject();
        if (subject == null || subject.isBlank()) {
            throw new BusinessException("Authenticated user is required");
        }
        try {
            return java.util.UUID.fromString(subject.trim());
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
