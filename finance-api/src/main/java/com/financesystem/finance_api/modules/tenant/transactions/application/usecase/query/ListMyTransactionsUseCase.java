package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.mapper.TransactionMapper;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionMovementRepository;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListMyTransactionsUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionMovementRepository transactionMovementRepository;
    private final TransactionMapper transactionMapper;
    private final SecurityContextFacade securityContextFacade;

    public ListMyTransactionsUseCase(
            TransactionRepository transactionRepository,
            TransactionMovementRepository transactionMovementRepository,
            TransactionMapper transactionMapper,
            SecurityContextFacade securityContextFacade
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMovementRepository = transactionMovementRepository;
        this.transactionMapper = transactionMapper;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> execute() {
        UUID userId = resolveCurrentUserId();
        return transactionRepository.findAllByRequestedByUserId(userId)
                .stream()
                .map(transaction -> transactionMapper.toResponse(transaction, transactionMovementRepository.findAllByTransactionId(transaction.id())))
                .toList();
    }

    private UUID resolveCurrentUserId() {
        String subject = securityContextFacade.getCurrentSubject();
        if (subject == null || subject.isBlank()) {
            throw new BusinessException("Authenticated user is required");
        }

        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Authenticated subject is not a valid user id");
        }
    }
}
