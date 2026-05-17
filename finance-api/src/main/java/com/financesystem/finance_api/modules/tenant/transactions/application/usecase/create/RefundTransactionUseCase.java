package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateRefundTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.TransactionProcessorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RefundTransactionUseCase {

    private final TransactionProcessorService transactionProcessorService;

    public RefundTransactionUseCase(TransactionProcessorService transactionProcessorService) {
        this.transactionProcessorService = transactionProcessorService;
    }

    @Transactional
    public TransactionResponse execute(UUID transactionId, CreateRefundTransactionRequest request) {
        return transactionProcessorService.refundTransaction(transactionId, request);
    }
}
