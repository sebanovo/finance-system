package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.mapper.TransactionMapper;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionMovementRepository;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListAccountTransactionsUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionMovementRepository transactionMovementRepository;
    private final TransactionMapper transactionMapper;

    public ListAccountTransactionsUseCase(
            TransactionRepository transactionRepository,
            TransactionMovementRepository transactionMovementRepository,
            TransactionMapper transactionMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMovementRepository = transactionMovementRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> execute(UUID accountId) {
        return transactionRepository.findAllByAccountId(accountId)
                .stream()
                .map(transaction -> transactionMapper.toResponse(transaction, transactionMovementRepository.findAllByTransactionId(transaction.id())))
                .toList();
    }
}
