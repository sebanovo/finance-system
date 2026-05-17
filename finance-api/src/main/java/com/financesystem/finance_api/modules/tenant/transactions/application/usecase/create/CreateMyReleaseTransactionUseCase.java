package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateReleaseTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.TransactionProcessorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMyReleaseTransactionUseCase {

    private final TransactionProcessorService transactionProcessorService;

    public CreateMyReleaseTransactionUseCase(TransactionProcessorService transactionProcessorService) {
        this.transactionProcessorService = transactionProcessorService;
    }

    @Transactional
    public TransactionResponse execute(CreateReleaseTransactionRequest request) {
        return transactionProcessorService.createRelease(request);
    }
}
