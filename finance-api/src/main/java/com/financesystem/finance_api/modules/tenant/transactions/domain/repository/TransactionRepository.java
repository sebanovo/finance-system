package com.financesystem.finance_api.modules.tenant.transactions.domain.repository;

import com.financesystem.finance_api.modules.tenant.transactions.domain.model.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    Optional<Transaction> findByRequestedByUserIdAndIdempotencyKey(UUID requestedByUserId, String idempotencyKey);

    List<Transaction> findAllByParentTransactionId(UUID parentTransactionId);

    List<Transaction> findAll();

    List<Transaction> findAllByAccountId(UUID accountId);

    List<Transaction> findAllByRequestedByUserId(UUID requestedByUserId);
}
