package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.mapper.TransactionMapper;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionMovementRepository;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListMyAccountTransactionsUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMovementRepository transactionMovementRepository;
    private final TransactionMapper transactionMapper;
    private final SecurityContextFacade securityContextFacade;

    public ListMyAccountTransactionsUseCase(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            TransactionMovementRepository transactionMovementRepository,
            TransactionMapper transactionMapper,
            SecurityContextFacade securityContextFacade
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMovementRepository = transactionMovementRepository;
        this.transactionMapper = transactionMapper;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> execute(UUID accountId) {
        UUID currentUserId = resolveCurrentUserId();
        Account account = accountRepository.findById(accountId)
                .filter(existing -> currentUserId.equals(existing.userId()))
                .orElseThrow(() -> new BusinessException("Account not found with id: " + accountId));

        return transactionRepository.findAllByAccountId(account.id())
                .stream()
                .filter(transaction -> currentUserId.equals(transaction.requestedByUserId()) || isOwnerTransaction(transaction, account.id()))
                .map(transaction -> transactionMapper.toResponse(transaction, transactionMovementRepository.findAllByTransactionId(transaction.id())))
                .toList();
    }

    private boolean isOwnerTransaction(com.financesystem.finance_api.modules.tenant.transactions.domain.model.Transaction transaction, UUID accountId) {
        return accountId.equals(transaction.sourceAccountId()) || accountId.equals(transaction.targetAccountId());
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
