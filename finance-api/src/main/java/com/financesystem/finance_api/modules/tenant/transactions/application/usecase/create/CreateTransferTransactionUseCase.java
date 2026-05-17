package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateTransferTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.TransactionProcessorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTransferTransactionUseCase {

    private final TransactionProcessorService transactionProcessorService;

    public CreateTransferTransactionUseCase(TransactionProcessorService transactionProcessorService) {
        this.transactionProcessorService = transactionProcessorService;
    }

    @Transactional
    public TransactionResponse execute(CreateTransferTransactionRequest request) {
        return transactionProcessorService.createTransfer(request);
    }
}
