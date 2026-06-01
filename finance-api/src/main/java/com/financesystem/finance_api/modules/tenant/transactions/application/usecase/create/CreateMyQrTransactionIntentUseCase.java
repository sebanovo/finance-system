package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateQrTransactionIntentRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.QrTransactionIntentResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.TransactionProcessorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMyQrTransactionIntentUseCase {

    private final TransactionProcessorService transactionProcessorService;

    public CreateMyQrTransactionIntentUseCase(TransactionProcessorService transactionProcessorService) {
        this.transactionProcessorService = transactionProcessorService;
    }

    @Transactional
    public QrTransactionIntentResponse execute(CreateQrTransactionIntentRequest request) {
        return transactionProcessorService.createMyQrIntent(request);
    }
}
