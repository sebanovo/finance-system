package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.mapper.TransactionMapper;
import com.financesystem.finance_api.modules.tenant.transactions.domain.exception.TransactionNotFoundException;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionMovementRepository;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetTransactionByIdUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionMovementRepository transactionMovementRepository;
    private final TransactionMapper transactionMapper;

    public GetTransactionByIdUseCase(
            TransactionRepository transactionRepository,
            TransactionMovementRepository transactionMovementRepository,
            TransactionMapper transactionMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMovementRepository = transactionMovementRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional(readOnly = true)
    public TransactionResponse execute(UUID id) {
        return transactionRepository.findById(id)
                .map(transaction -> transactionMapper.toResponse(transaction, transactionMovementRepository.findAllByTransactionId(transaction.id())))
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
    }
}
