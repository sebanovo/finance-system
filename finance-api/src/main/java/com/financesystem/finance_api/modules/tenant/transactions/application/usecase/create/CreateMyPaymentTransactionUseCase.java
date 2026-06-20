package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.PaymentProcessingService;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateMyPaymentTransactionUseCase {

    private final PaymentProcessingService paymentProcessingService;
    private final SecurityContextFacade securityContextFacade;

    public CreateMyPaymentTransactionUseCase(
            PaymentProcessingService paymentProcessingService,
            SecurityContextFacade securityContextFacade
    ) {
        this.paymentProcessingService = paymentProcessingService;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional
    public TransactionResponse execute(CreatePaymentTransactionRequest request) {
        return paymentProcessingService.createPayment(request, currentUserId());
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
}
