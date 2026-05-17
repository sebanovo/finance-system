package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateWithdrawalTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.TransactionProcessorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMyWithdrawalTransactionUseCase {

    private final TransactionProcessorService transactionProcessorService;

    public CreateMyWithdrawalTransactionUseCase(TransactionProcessorService transactionProcessorService) {
        this.transactionProcessorService = transactionProcessorService;
    }

    @Transactional
    public TransactionResponse execute(CreateWithdrawalTransactionRequest request) {
        return transactionProcessorService.createWithdrawal(request);
    }
}
