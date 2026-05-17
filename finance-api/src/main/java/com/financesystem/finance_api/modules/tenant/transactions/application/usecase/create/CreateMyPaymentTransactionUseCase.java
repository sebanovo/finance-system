package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.TransactionProcessorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMyPaymentTransactionUseCase {

    private final TransactionProcessorService transactionProcessorService;

    public CreateMyPaymentTransactionUseCase(TransactionProcessorService transactionProcessorService) {
        this.transactionProcessorService = transactionProcessorService;
    }

    @Transactional
    public TransactionResponse execute(CreatePaymentTransactionRequest request) {
        return transactionProcessorService.createPayment(request);
    }
}
