package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.CreateBankServicePaymentRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentProcessingRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.service.ServicePaymentProcessingService;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateBankServicePaymentUseCase {

    private final ServicePaymentProcessingService servicePaymentProcessingService;
    private final SecurityContextFacade securityContextFacade;
    private final AccountRepository accountRepository;

    public CreateBankServicePaymentUseCase(
            ServicePaymentProcessingService servicePaymentProcessingService,
            SecurityContextFacade securityContextFacade,
            AccountRepository accountRepository
    ) {
        this.servicePaymentProcessingService = servicePaymentProcessingService;
        this.securityContextFacade = securityContextFacade;
        this.accountRepository = accountRepository;
    }

    public ServicePaymentResponse execute(CreateBankServicePaymentRequest request) {
        UUID actorUserId = currentUserId();
        Account account = accountRepository.findByAccountNumber(request.sourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));

        if (!account.active() || account.status() != AccountStatus.ACTIVE) {
            throw new BusinessException("Source account must be active");
        }

        UUID payerUserId = account.userId();
        if (payerUserId == null) {
            throw new BusinessException("Source account does not have an owner");
        }

        return servicePaymentProcessingService.process(new ServicePaymentProcessingRequest(
                actorUserId,
                payerUserId,
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
