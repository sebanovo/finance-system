package com.financesystem.finance_api.modules.tenant.transactions.application.mapper;

import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.fx.application.dto.TransactionFxDetailResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionMovementResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.Transaction;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionMovement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TransactionMapper {

    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    public TransactionMapper(AccountRepository accountRepository, ObjectMapper objectMapper) {
        this.accountRepository = accountRepository;
        this.objectMapper = objectMapper;
    }

    public TransactionResponse toResponse(Transaction transaction, List<TransactionMovement> movements) {
        Optional<AccountOwnerView> sourceAccount = transaction.sourceAccountId() == null
                ? Optional.empty()
                : accountRepository.findViewById(transaction.sourceAccountId());

        Optional<AccountOwnerView> targetAccount = transaction.targetAccountId() == null
                ? Optional.empty()
                : accountRepository.findViewById(transaction.targetAccountId());

        return new TransactionResponse(
                transaction.id(),
                transaction.type().name(),
                transaction.status().name(),
                transaction.channel().name(),
                transaction.amount(),
                transaction.currency(),
                transaction.sourceAccountId(),
                sourceAccount.map(AccountOwnerView::accountNumber).orElse(null),
                sourceAccount.map(view -> buildAccountLabel(view.accountName().name(), view.customAlias())).orElse(null),
                transaction.targetAccountId(),
                targetAccount.map(AccountOwnerView::accountNumber).orElse(null),
                targetAccount.map(view -> buildAccountLabel(view.accountName().name(), view.customAlias())).orElse(null),
                transaction.externalReference(),
                transaction.idempotencyKey(),
                transaction.description(),
                transaction.failureReason(),
                transaction.requestedByUserId(),
                transaction.approvedByUserId(),
                transaction.processedAt(),
                transaction.createdAt(),
                transaction.updatedAt(),
                parseFxDetail(transaction.metadata()),
                movements.stream().map(this::toMovementResponse).toList()
        );
    }

    public TransactionMovementResponse toMovementResponse(TransactionMovement movement) {
        Optional<AccountOwnerView> account = accountRepository.findViewById(movement.accountId());

        return new TransactionMovementResponse(
                movement.id(),
                movement.accountId(),
                account.map(AccountOwnerView::accountNumber).orElse(null),
                account.map(view -> buildAccountLabel(view.accountName().name(), view.customAlias())).orElse(null),
                movement.movementType().name(),
                movement.amount(),
                movement.currency(),
                movement.balanceBefore(),
                movement.balanceAfter(),
                movement.description(),
                movement.createdAt()
        );
    }

    private String buildAccountLabel(String accountName, String customAlias) {
        if (customAlias != null && !customAlias.isBlank()) {
            return customAlias.trim();
        }

        if (accountName == null) {
            return null;
        }

        return switch (accountName) {
            case "MAIN_WALLET" -> "Wallet principal";
            case "SAVINGS_ACCOUNT" -> "Cuenta de ahorro";
            case "CHECKING_ACCOUNT" -> "Cuenta corriente";
            case "CREDIT_CARD_ACCOUNT" -> "Tarjeta de crédito";
            case "PREPAID_CARD_ACCOUNT" -> "Tarjeta prepago";
            case "LOAN_ACCOUNT" -> "Cuenta de préstamo";
            case "BUSINESS_ACCOUNT" -> "Cuenta empresarial";
            case "SECONDARY_ACCOUNT" -> "Cuenta secundaria";
            default -> accountName;
        };
    }

    private TransactionFxDetailResponse parseFxDetail(String metadata) {
        if (metadata == null || metadata.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(metadata, TransactionFxDetailResponse.class);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }
}
