package com.financesystem.finance_api.modules.tenant.transactions.domain.repository;

import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionMovement;

import java.util.List;
import java.util.UUID;

public interface TransactionMovementRepository {

    TransactionMovement save(TransactionMovement transactionMovement);

    List<TransactionMovement> saveAll(List<TransactionMovement> transactionMovements);

    List<TransactionMovement> findAllByTransactionId(UUID transactionId);
}
