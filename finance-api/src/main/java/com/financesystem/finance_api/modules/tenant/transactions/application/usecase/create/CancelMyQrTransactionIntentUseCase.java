package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.QrTransactionIntentResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.TransactionProcessorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CancelMyQrTransactionIntentUseCase {

    private final TransactionProcessorService transactionProcessorService;

    public CancelMyQrTransactionIntentUseCase(TransactionProcessorService transactionProcessorService) {
        this.transactionProcessorService = transactionProcessorService;
    }

    @Transactional
    public QrTransactionIntentResponse execute(UUID id) {
        return transactionProcessorService.cancelMyQrIntent(id);
    }
}
