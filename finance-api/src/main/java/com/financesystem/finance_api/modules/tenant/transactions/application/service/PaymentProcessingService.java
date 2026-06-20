package com.financesystem.finance_api.modules.tenant.transactions.application.service;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentProcessingService {

    private final TransactionProcessorService transactionProcessorService;

    public PaymentProcessingService(TransactionProcessorService transactionProcessorService) {
        this.transactionProcessorService = transactionProcessorService;
    }

    public TransactionResponse createPayment(CreatePaymentTransactionRequest request, UUID requestedByUserId) {
        return transactionProcessorService.createPayment(request, requestedByUserId);
    }
}
