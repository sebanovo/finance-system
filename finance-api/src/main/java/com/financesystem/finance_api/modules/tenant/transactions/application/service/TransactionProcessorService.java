package com.financesystem.finance_api.modules.tenant.transactions.application.service;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.governance.notifications.application.dto.NotificationPublishRequest;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationCategory;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPriority;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationType;
import com.financesystem.finance_api.modules.governance.notifications.domain.port.NotificationPublisherPort;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.fx.application.dto.TransactionFxDetailResponse;
import com.financesystem.finance_api.modules.tenant.fx.application.service.CurrencyExchangeService;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxQuote;
import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitEvaluationRequest;
import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitEvaluationResponse;
import com.financesystem.finance_api.modules.tenant.limits.application.service.LimitPolicyService;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.ConfirmQrTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateAdjustmentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateFeeTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateHoldTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateQrTransactionIntentRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.QrTransactionIntentResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.port.AccountingPostingPort;
import com.financesystem.finance_api.modules.tenant.transactions.application.port.TransactionAccountingMovementRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.port.TransactionAccountingRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateRefundTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateReversalTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateDepositTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateTransferTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateReleaseTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateWithdrawalTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.mapper.TransactionMapper;
import com.financesystem.finance_api.modules.tenant.transactions.audit.TenantTransactionAuditPayloads;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.*;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionMovementRepository;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionRepository;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.QrTransactionIntentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionProcessorService {

    private static final Logger log = LoggerFactory.getLogger(TransactionProcessorService.class);

    private static final EnumSet<AccountType> TRANSACTIONAL_ACCOUNT_TYPES = EnumSet.of(
            AccountType.WALLET,
            AccountType.SAVINGS,
            AccountType.CHECKING,
            AccountType.PREPAID_CARD
    );
    private static final EnumSet<AccountType> TRANSFER_SOURCE_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final EnumSet<AccountType> TRANSFER_TARGET_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final EnumSet<AccountType> DEPOSIT_TARGET_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final EnumSet<AccountType> WITHDRAWAL_SOURCE_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final EnumSet<AccountType> HOLD_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final EnumSet<AccountType> RELEASE_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final EnumSet<AccountType> FEE_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final EnumSet<AccountType> ADJUSTMENT_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final EnumSet<AccountType> QR_SOURCE_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final EnumSet<AccountType> QR_TARGET_ACCOUNT_TYPES = TRANSACTIONAL_ACCOUNT_TYPES;
    private static final Duration QR_INTENT_TTL = Duration.ofMinutes(15);

    private final TransactionRepository transactionRepository;
    private final TransactionMovementRepository transactionMovementRepository;
    private final QrTransactionIntentRepository qrTransactionIntentRepository;
    private final AccountRepository accountRepository;
    private final CurrencyExchangeService currencyExchangeService;
    private final LimitPolicyService limitPolicyService;
    private final AccountingPostingPort accountingPostingPort;
    private final TransactionMapper transactionMapper;
    private final AuditTrailService auditTrailService;
    private final NotificationPublisherPort notificationPublisherPort;
    private final SecurityContextFacade securityContextFacade;
    private final ObjectMapper objectMapper;

    public TransactionProcessorService(
            TransactionRepository transactionRepository,
            TransactionMovementRepository transactionMovementRepository,
            QrTransactionIntentRepository qrTransactionIntentRepository,
            AccountRepository accountRepository,
            CurrencyExchangeService currencyExchangeService,
            LimitPolicyService limitPolicyService,
            AccountingPostingPort accountingPostingPort,
            TransactionMapper transactionMapper,
            AuditTrailService auditTrailService,
            NotificationPublisherPort notificationPublisherPort,
            SecurityContextFacade securityContextFacade,
            ObjectMapper objectMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMovementRepository = transactionMovementRepository;
        this.qrTransactionIntentRepository = qrTransactionIntentRepository;
        this.accountRepository = accountRepository;
        this.currencyExchangeService = currencyExchangeService;
        this.limitPolicyService = limitPolicyService;
        this.accountingPostingPort = accountingPostingPort;
        this.transactionMapper = transactionMapper;
        this.auditTrailService = auditTrailService;
        this.notificationPublisherPort = notificationPublisherPort;
        this.securityContextFacade = securityContextFacade;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TransactionResponse createTransfer(CreateTransferTransactionRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Transfer",
                    buildTransferFingerprint(
                            request.sourceAccountId(),
                            request.targetAccountId(),
                            request.amount(),
                            request.currency(),
                            request.description()
                    ),
                    buildTransferFingerprint(
                            existing.sourceAccountId(),
                            existing.targetAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency()),
                            existing.description()
                    )
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        if (request.sourceAccountId().equals(request.targetAccountId())) {
            throw new BusinessException("Source and target accounts must be different");
        }

        Account sourceAccount = getAccountOrThrow(request.sourceAccountId(), "Source");
        Account targetAccount = getAccountOrThrow(request.targetAccountId(), "Target");

        validateTransferAccounts(sourceAccount, targetAccount, request.currency());
        String normalizedCurrency = request.currency().name();

        BigDecimal amount = safeMoney(request.amount());
        FxQuote fxQuote = currencyExchangeService.calculate(
                FxOperationCode.TRANSFER,
                amount,
                request.currency(),
                targetAccount.currency()
        );
        TransactionFxDetailResponse fxDetail = currencyExchangeService.toResponse(fxQuote);
        String fxMetadata = toFxMetadataJson(fxDetail);
        if (sourceAccount.availableBalance().compareTo(amount) < 0) {
            throw new BusinessException(buildInsufficientBalanceMessage(
                    "Source account",
                    amount,
                    sourceAccount.availableBalance(),
                    normalizedCurrency
            ));
        }

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                sourceAccount.id(),
                targetAccount.id(),
                sourceAccount.accountType(),
                targetAccount.accountType(),
                sourceAccount.availableBalance(),
                targetAccount.availableBalance(),
                TransactionType.TRANSFER,
                request.currency(),
                amount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "Transfer",
                    amount,
                    normalizedCurrency,
                    limitEvaluation.reason()
            ));
        }

        Transaction transaction = new Transaction(
                null,
                TransactionType.TRANSFER,
                TransactionStatus.PROCESSING,
                TransactionChannel.API,
                amount,
                normalizedCurrency,
                sourceAccount.id(),
                targetAccount.id(),
                null,
                idempotencyKey,
                normalizeNullable(request.description()),
                null,
                fxMetadata,
                null,
                null,
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (limitEvaluation.requiresReview()) {
            throw new BusinessException(buildReviewRequiredMessage(limitEvaluation.reason()));
        }

        BigDecimal sourceBefore = safeMoney(sourceAccount.availableBalance());
        BigDecimal targetBefore = safeMoney(targetAccount.availableBalance());
        BigDecimal sourceAfter = sourceBefore.subtract(amount);
        BigDecimal targetCreditAmount = fxQuote.targetAmountNet();
        BigDecimal targetAfter = targetBefore.add(targetCreditAmount);

        sourceAccount = new Account(
                sourceAccount.id(),
                sourceAccount.userId(),
                sourceAccount.accountNumber(),
                sourceAccount.accountName(),
                sourceAccount.customAlias(),
                sourceAccount.accountType(),
                sourceAccount.currency(),
                sourceAfter,
                safeMoney(sourceAccount.heldBalance()),
                sourceAccount.status(),
                sourceAccount.statusReason(),
                sourceAccount.active(),
                sourceAccount.primary(),
                sourceAccount.openedAt(),
                sourceAccount.closedAt(),
                sourceAccount.createdAt(),
                sourceAccount.updatedAt()
        );

        targetAccount = new Account(
                targetAccount.id(),
                targetAccount.userId(),
                targetAccount.accountNumber(),
                targetAccount.accountName(),
                targetAccount.customAlias(),
                targetAccount.accountType(),
                targetAccount.currency(),
                targetAfter,
                safeMoney(targetAccount.heldBalance()),
                targetAccount.status(),
                targetAccount.statusReason(),
                targetAccount.active(),
                targetAccount.primary(),
                targetAccount.openedAt(),
                targetAccount.closedAt(),
                targetAccount.createdAt(),
                targetAccount.updatedAt()
        );

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        List<TransactionMovement> movements = transactionMovementRepository.saveAll(List.of(
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        sourceAccount.id(),
                        TransactionMovementType.DEBIT,
                        amount,
                        normalizedCurrency,
                        sourceBefore,
                        sourceAfter,
                        "Transfer to " + targetAccount.accountNumber(),
                        null
                ),
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        targetAccount.id(),
                        TransactionMovementType.CREDIT,
                        targetCreditAmount,
                        targetAccount.currency().name(),
                        targetBefore,
                        targetAfter,
                        buildTransferCreditDescription(sourceAccount.accountNumber(), fxDetail),
                        null
                )
        ));

        Transaction completed = new Transaction(
                savedTransaction.id(),
                savedTransaction.type(),
                TransactionStatus.COMPLETED,
                savedTransaction.channel(),
                savedTransaction.amount(),
                savedTransaction.currency(),
                savedTransaction.sourceAccountId(),
                savedTransaction.targetAccountId(),
                savedTransaction.externalReference(),
                savedTransaction.idempotencyKey(),
                savedTransaction.description(),
                null,
                savedTransaction.metadata(),
                savedTransaction.parentTransactionId(),
                savedTransaction.reversedTransactionId(),
                savedTransaction.requestedByUserId(),
                savedTransaction.approvedByUserId(),
                Instant.now(),
                savedTransaction.createdAt(),
                savedTransaction.updatedAt()
        );

        Transaction persistedCompleted = transactionRepository.save(completed);

        limitPolicyService.registerUsage(limitRequest, limitEvaluation.checks().stream()
                .filter(check -> check.matched() && check.allowed())
                .map(check -> check.ruleId())
                .toList());

        accountingPostingPort.postTransaction(new TransactionAccountingRequest(
                persistedCompleted.id(),
                persistedCompleted.type().name(),
                persistedCompleted.status().name(),
                persistedCompleted.currency(),
                persistedCompleted.amount(),
                persistedCompleted.sourceAccountId(),
                persistedCompleted.targetAccountId(),
                persistedCompleted.externalReference(),
                persistedCompleted.description(),
                persistedCompleted.processedAt(),
                persistedCompleted.metadata(),
                movements.stream()
                        .map(movement -> new TransactionAccountingMovementRequest(
                                movement.accountId(),
                                movement.movementType().name(),
                                movement.amount(),
                                movement.currency(),
                                movement.balanceBefore(),
                                movement.balanceAfter(),
                                movement.description()
                        ))
                        .toList()
        ));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "TRANSFER",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "sourceAccount", sourceAccount.accountNumber(),
                        "targetAccount", targetAccount.accountNumber(),
                        "movementCount", movements.size()
                ),
                TenantTransactionAuditPayloads.transactionState(savedTransaction),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public TransactionResponse createDeposit(CreateDepositTransactionRequest request) {
        return createDeposit(request, FxOperationCode.DEPOSIT);
    }

    public TransactionResponse createDeposit(CreateDepositTransactionRequest request, FxOperationCode feeOperationCode) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Deposit",
                    buildDepositFingerprint(
                            request.targetAccountId(),
                            request.amount(),
                            request.currency(),
                            resolveChannel(request.method(), TransactionChannel.CASHBOX),
                            request.externalReference(),
                            request.description()
                    ),
                    buildDepositFingerprint(
                            existing.targetAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency()),
                            existing.channel(),
                            existing.externalReference(),
                            existing.description()
                    )
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        Account targetAccount = getAccountOrThrow(request.targetAccountId(), "Target");

        validateTransactionalAccountType(targetAccount, "QR target account", QR_TARGET_ACCOUNT_TYPES);
        validateAccountCurrencyMatches(targetAccount, request.currency(), "QR intent currency must match target account currency");
        String normalizedCurrency = request.currency().name();
        BigDecimal amount = safeMoney(request.amount());
        FxQuote fxQuote = currencyExchangeService.calculate(
                feeOperationCode,
                amount,
                request.currency(),
                targetAccount.currency()
        );
        TransactionFxDetailResponse fxDetail = currencyExchangeService.toResponse(fxQuote);
        String fxMetadata = toFxMetadataJson(fxDetail);

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                null,
                targetAccount.id(),
                null,
                targetAccount.accountType(),
                null,
                targetAccount.availableBalance(),
                TransactionType.DEPOSIT,
                request.currency(),
                amount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "Deposit",
                    amount,
                    normalizedCurrency,
                    limitEvaluation.reason()
            ));
        }

        Transaction transaction = new Transaction(
                null,
                TransactionType.DEPOSIT,
                TransactionStatus.PROCESSING,
                resolveChannel(request.method(), TransactionChannel.CASHBOX),
                amount,
                normalizedCurrency,
                null,
                targetAccount.id(),
                normalizeNullable(request.externalReference()),
                idempotencyKey,
                normalizeNullable(request.description()),
                null,
                fxMetadata,
                null,
                null,
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (limitEvaluation.requiresReview()) {
            throw new BusinessException(buildReviewRequiredMessage(limitEvaluation.reason()));
        }

        BigDecimal targetBefore = safeMoney(targetAccount.availableBalance());
        BigDecimal creditedAmount = fxQuote.targetAmountNet();
        BigDecimal targetAfter = targetBefore.add(creditedAmount);

        targetAccount = new Account(
                targetAccount.id(),
                targetAccount.userId(),
                targetAccount.accountNumber(),
                targetAccount.accountName(),
                targetAccount.customAlias(),
                targetAccount.accountType(),
                targetAccount.currency(),
                targetAfter,
                safeMoney(targetAccount.heldBalance()),
                targetAccount.status(),
                targetAccount.statusReason(),
                targetAccount.active(),
                targetAccount.primary(),
                targetAccount.openedAt(),
                targetAccount.closedAt(),
                targetAccount.createdAt(),
                targetAccount.updatedAt()
        );

        accountRepository.save(targetAccount);

        List<TransactionMovement> movements = transactionMovementRepository.saveAll(List.of(
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        targetAccount.id(),
                        TransactionMovementType.CREDIT,
                        creditedAmount,
                        targetAccount.currency().name(),
                        targetBefore,
                        targetAfter,
                        buildDepositDescription(targetAccount.accountNumber(), fxDetail),
                        null
                )
        ));

        Transaction completed = markCompleted(savedTransaction);
        Transaction persistedCompleted = transactionRepository.save(completed);

        limitPolicyService.registerUsage(limitRequest, limitEvaluation.checks().stream()
                .filter(check -> check.matched() && check.allowed())
                .map(check -> check.ruleId())
                .toList());

        accountingPostingPort.postTransaction(new TransactionAccountingRequest(
                persistedCompleted.id(),
                persistedCompleted.type().name(),
                persistedCompleted.status().name(),
                persistedCompleted.currency(),
                persistedCompleted.amount(),
                persistedCompleted.sourceAccountId(),
                persistedCompleted.targetAccountId(),
                persistedCompleted.externalReference(),
                persistedCompleted.description(),
                persistedCompleted.processedAt(),
                persistedCompleted.metadata(),
                movements.stream()
                        .map(movement -> new TransactionAccountingMovementRequest(
                                movement.accountId(),
                                movement.movementType().name(),
                                movement.amount(),
                                movement.currency(),
                                movement.balanceBefore(),
                                movement.balanceAfter(),
                                movement.description()
                        ))
                        .toList()
        ));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "DEPOSIT",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "account", targetAccount.accountNumber(),
                        "movement", "CREDIT"
                ),
                TenantTransactionAuditPayloads.transactionState(savedTransaction),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public TransactionResponse createWithdrawal(CreateWithdrawalTransactionRequest request) {
        return createWithdrawal(request, FxOperationCode.WITHDRAWAL);
    }

    public TransactionResponse createWithdrawal(CreateWithdrawalTransactionRequest request, FxOperationCode feeOperationCode) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Withdrawal",
                    buildWithdrawalFingerprint(
                            request.sourceAccountId(),
                            request.amount(),
                            request.currency(),
                            resolveChannel(request.method(), TransactionChannel.CASHBOX),
                            request.description()
                    ),
                    buildWithdrawalFingerprint(
                            existing.sourceAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency()),
                            existing.channel(),
                            existing.description()
                    )
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        Account sourceAccount = getAccountOrThrow(request.sourceAccountId(), "Source");

        validateWithdrawalAccount(sourceAccount, request.currency());
        String normalizedCurrency = request.currency().name();
        BigDecimal amount = safeMoney(request.amount());
        FxQuote fxQuote = currencyExchangeService.calculate(
                feeOperationCode,
                amount,
                request.currency(),
                sourceAccount.currency()
        );
        TransactionFxDetailResponse fxDetail = currencyExchangeService.toResponse(fxQuote);
        String fxMetadata = toFxMetadataJson(fxDetail);

        BigDecimal debitedAmount = fxQuote.targetAmountNet();
        if (sourceAccount.availableBalance().compareTo(debitedAmount) < 0) {
            throw new BusinessException(buildInsufficientBalanceMessage(
                    "Source account",
                    debitedAmount,
                    sourceAccount.availableBalance(),
                    sourceAccount.currency().name()
            ));
        }

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                sourceAccount.id(),
                null,
                sourceAccount.accountType(),
                null,
                sourceAccount.availableBalance(),
                null,
                TransactionType.WITHDRAWAL,
                request.currency(),
                debitedAmount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "Withdrawal",
                    amount,
                    normalizedCurrency,
                    limitEvaluation.reason()
            ));
        }

        Transaction transaction = new Transaction(
                null,
                TransactionType.WITHDRAWAL,
                TransactionStatus.PROCESSING,
                resolveChannel(request.method(), TransactionChannel.CASHBOX),
                amount,
                normalizedCurrency,
                sourceAccount.id(),
                null,
                null,
                idempotencyKey,
                normalizeNullable(request.description()),
                null,
                fxMetadata,
                null,
                null,
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (limitEvaluation.requiresReview()) {
            throw new BusinessException(buildReviewRequiredMessage(limitEvaluation.reason()));
        }

        BigDecimal sourceBefore = safeMoney(sourceAccount.availableBalance());
        BigDecimal sourceAfter = sourceBefore.subtract(debitedAmount);

        sourceAccount = new Account(
                sourceAccount.id(),
                sourceAccount.userId(),
                sourceAccount.accountNumber(),
                sourceAccount.accountName(),
                sourceAccount.customAlias(),
                sourceAccount.accountType(),
                sourceAccount.currency(),
                sourceAfter,
                safeMoney(sourceAccount.heldBalance()),
                sourceAccount.status(),
                sourceAccount.statusReason(),
                sourceAccount.active(),
                sourceAccount.primary(),
                sourceAccount.openedAt(),
                sourceAccount.closedAt(),
                sourceAccount.createdAt(),
                sourceAccount.updatedAt()
        );

        accountRepository.save(sourceAccount);

        List<TransactionMovement> movements = transactionMovementRepository.saveAll(List.of(
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        sourceAccount.id(),
                        TransactionMovementType.DEBIT,
                        debitedAmount,
                        sourceAccount.currency().name(),
                        sourceBefore,
                        sourceAfter,
                        buildWithdrawalDescription(sourceAccount.accountNumber(), fxDetail),
                        null
                )
        ));

        Transaction completed = markCompleted(savedTransaction);
        Transaction persistedCompleted = transactionRepository.save(completed);

        limitPolicyService.registerUsage(limitRequest, limitEvaluation.checks().stream()
                .filter(check -> check.matched() && check.allowed())
                .map(check -> check.ruleId())
                .toList());

        accountingPostingPort.postTransaction(new TransactionAccountingRequest(
                persistedCompleted.id(),
                persistedCompleted.type().name(),
                persistedCompleted.status().name(),
                persistedCompleted.currency(),
                persistedCompleted.amount(),
                persistedCompleted.sourceAccountId(),
                persistedCompleted.targetAccountId(),
                persistedCompleted.externalReference(),
                persistedCompleted.description(),
                persistedCompleted.processedAt(),
                persistedCompleted.metadata(),
                movements.stream()
                        .map(movement -> new TransactionAccountingMovementRequest(
                                movement.accountId(),
                                movement.movementType().name(),
                                movement.amount(),
                                movement.currency(),
                                movement.balanceBefore(),
                                movement.balanceAfter(),
                                movement.description()
                        ))
                        .toList()
        ));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "WITHDRAWAL",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "account", sourceAccount.accountNumber(),
                        "movement", "DEBIT"
                ),
                TenantTransactionAuditPayloads.transactionState(savedTransaction),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public TransactionResponse createPayment(CreatePaymentTransactionRequest request) {
        return createPayment(request, resolveRequestedByUserId());
    }

    @Transactional
    public TransactionResponse createPayment(CreatePaymentTransactionRequest request, UUID requestedByUserId) {
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Payment",
                    buildPaymentFingerprint(
                            request.sourceAccountId(),
                            request.amount(),
                            request.currency(),
                            resolveChannel(request.method(), TransactionChannel.API),
                            request.externalReference(),
                            request.description()
                    ),
                    buildPaymentFingerprint(
                            existing.sourceAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency()),
                            existing.channel(),
                            existing.externalReference(),
                            existing.description()
                    )
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        Account sourceAccount = getAccountOrThrow(request.sourceAccountId(), "Source");

        validatePaymentAccount(sourceAccount, request.currency());
        String normalizedCurrency = request.currency().name();
        BigDecimal amount = safeMoney(request.amount());
        FxQuote fxQuote = currencyExchangeService.calculate(
                FxOperationCode.PAYMENT,
                amount,
                request.currency(),
                sourceAccount.currency()
        );
        TransactionFxDetailResponse fxDetail = currencyExchangeService.toResponse(fxQuote);
        String fxMetadata = toFxMetadataJson(fxDetail);
        BigDecimal debitedAmount = fxQuote.targetAmountNet();

        if (sourceAccount.availableBalance().compareTo(debitedAmount) < 0) {
            throw new BusinessException(buildInsufficientBalanceMessage(
                    "Source account",
                    debitedAmount,
                    sourceAccount.availableBalance(),
                    sourceAccount.currency().name()
            ));
        }

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                sourceAccount.id(),
                null,
                sourceAccount.accountType(),
                null,
                sourceAccount.availableBalance(),
                null,
                TransactionType.PAYMENT,
                request.currency(),
                debitedAmount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "Payment",
                    amount,
                    normalizedCurrency,
                    limitEvaluation.reason()
            ));
        }

        Transaction transaction = new Transaction(
                null,
                TransactionType.PAYMENT,
                TransactionStatus.PROCESSING,
                resolveChannel(request.method(), TransactionChannel.API),
                amount,
                normalizedCurrency,
                sourceAccount.id(),
                null,
                normalizeNullable(request.externalReference()),
                idempotencyKey,
                normalizeNullable(request.description()),
                null,
                fxMetadata,
                null,
                null,
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (limitEvaluation.requiresReview()) {
            throw new BusinessException(buildReviewRequiredMessage(limitEvaluation.reason()));
        }

        BigDecimal sourceBefore = safeMoney(sourceAccount.availableBalance());
        BigDecimal sourceAfter = sourceBefore.subtract(debitedAmount);

        sourceAccount = new Account(
                sourceAccount.id(),
                sourceAccount.userId(),
                sourceAccount.accountNumber(),
                sourceAccount.accountName(),
                sourceAccount.customAlias(),
                sourceAccount.accountType(),
                sourceAccount.currency(),
                sourceAfter,
                safeMoney(sourceAccount.heldBalance()),
                sourceAccount.status(),
                sourceAccount.statusReason(),
                sourceAccount.active(),
                sourceAccount.primary(),
                sourceAccount.openedAt(),
                sourceAccount.closedAt(),
                sourceAccount.createdAt(),
                sourceAccount.updatedAt()
        );

        accountRepository.save(sourceAccount);

        List<TransactionMovement> movements = transactionMovementRepository.saveAll(List.of(
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        sourceAccount.id(),
                        TransactionMovementType.DEBIT,
                        debitedAmount,
                        sourceAccount.currency().name(),
                        sourceBefore,
                        sourceAfter,
                        buildPaymentDescription(request.externalReference(), sourceAccount.accountNumber(), fxDetail),
                        null
                )
        ));

        Transaction completed = markCompleted(savedTransaction);
        Transaction persistedCompleted = transactionRepository.save(completed);

        limitPolicyService.registerUsage(limitRequest, limitEvaluation.checks().stream()
                .filter(check -> check.matched() && check.allowed())
                .map(check -> check.ruleId())
                .toList());

        accountingPostingPort.postTransaction(buildAccountingRequest(persistedCompleted, movements));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "PAYMENT",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "account", sourceAccount.accountNumber(),
                        "movement", "DEBIT"
                ),
                TenantTransactionAuditPayloads.transactionState(savedTransaction),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public TransactionResponse createHold(CreateHoldTransactionRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Hold",
                    buildHoldFingerprint(
                            request.accountId(),
                            request.amount(),
                            request.currency(),
                            request.description()
                    ),
                    buildHoldFingerprint(
                            existing.sourceAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency()),
                            existing.description()
                    )
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        Account account = getAccountOrThrow(request.accountId(), "Account");

        validateTransactionalAccountType(account, "hold account", HOLD_ACCOUNT_TYPES);
        validateAccountCurrencyMatches(account, request.currency(), "Hold currency must match account currency");
        String normalizedCurrency = request.currency().name();
        BigDecimal amount = safeMoney(request.amount());

        if (account.availableBalance().compareTo(amount) < 0) {
            throw new BusinessException(buildInsufficientBalanceMessage(
                    "Account",
                    amount,
                    account.availableBalance(),
                    account.currency().name()
            ));
        }

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                account.id(),
                account.id(),
                account.accountType(),
                account.accountType(),
                account.availableBalance(),
                account.heldBalance(),
                TransactionType.HOLD,
                request.currency(),
                amount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "Hold",
                    amount,
                    normalizedCurrency,
                    limitEvaluation.reason()
            ));
        }

        Transaction transaction = new Transaction(
                null,
                TransactionType.HOLD,
                TransactionStatus.PROCESSING,
                TransactionChannel.API,
                amount,
                normalizedCurrency,
                account.id(),
                account.id(),
                null,
                idempotencyKey,
                normalizeNullable(request.description()),
                null,
                "{\"operation\":\"hold\"}",
                null,
                null,
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (limitEvaluation.requiresReview()) {
            throw new BusinessException(buildReviewRequiredMessage(limitEvaluation.reason()));
        }

        BigDecimal availableBefore = safeMoney(account.availableBalance());
        BigDecimal heldBefore = safeMoney(account.heldBalance());
        BigDecimal availableAfter = availableBefore.subtract(amount);
        BigDecimal heldAfter = heldBefore.add(amount);

        accountRepository.save(rebuildAccount(account, availableAfter, heldAfter));

        List<TransactionMovement> movements = transactionMovementRepository.saveAll(List.of(
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        account.id(),
                        TransactionMovementType.DEBIT,
                        amount,
                        normalizedCurrency,
                        availableBefore,
                        availableAfter,
                        buildHoldDescription(account.accountNumber()),
                        null
                ),
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        account.id(),
                        TransactionMovementType.CREDIT,
                        amount,
                        normalizedCurrency,
                        heldBefore,
                        heldAfter,
                        buildHoldReserveDescription(account.accountNumber()),
                        null
                )
        ));

        Transaction completed = markCompleted(savedTransaction);
        Transaction persistedCompleted = transactionRepository.save(completed);

        limitPolicyService.registerUsage(limitRequest, limitEvaluation.checks().stream()
                .filter(check -> check.matched() && check.allowed())
                .map(check -> check.ruleId())
                .toList());

        accountingPostingPort.postTransaction(buildAccountingRequest(persistedCompleted, movements));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "HOLD",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "account", account.accountNumber(),
                        "movement", "HOLD"
                ),
                TenantTransactionAuditPayloads.transactionState(savedTransaction),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public TransactionResponse createRelease(CreateReleaseTransactionRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Release",
                    buildReleaseFingerprint(
                            request.accountId(),
                            request.amount(),
                            request.currency(),
                            request.description()
                    ),
                    buildReleaseFingerprint(
                            existing.sourceAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency()),
                            existing.description()
                    )
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        Account account = getAccountOrThrow(request.accountId(), "Account");

        validateTransactionalAccountType(account, "release account", RELEASE_ACCOUNT_TYPES);
        validateAccountCurrencyMatches(account, request.currency(), "Release currency must match account currency");
        String normalizedCurrency = request.currency().name();
        BigDecimal amount = safeMoney(request.amount());

        if (account.heldBalance().compareTo(amount) < 0) {
            throw new BusinessException(buildInsufficientBalanceMessage(
                    "Held balance",
                    amount,
                    account.heldBalance(),
                    account.currency().name()
            ));
        }

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                account.id(),
                account.id(),
                account.accountType(),
                account.accountType(),
                account.availableBalance(),
                account.heldBalance(),
                TransactionType.RELEASE,
                request.currency(),
                amount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "Release",
                    amount,
                    normalizedCurrency,
                    limitEvaluation.reason()
            ));
        }

        Transaction transaction = new Transaction(
                null,
                TransactionType.RELEASE,
                TransactionStatus.PROCESSING,
                TransactionChannel.API,
                amount,
                normalizedCurrency,
                account.id(),
                account.id(),
                null,
                idempotencyKey,
                normalizeNullable(request.description()),
                null,
                "{\"operation\":\"release\"}",
                null,
                null,
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (limitEvaluation.requiresReview()) {
            throw new BusinessException(buildReviewRequiredMessage(limitEvaluation.reason()));
        }

        BigDecimal availableBefore = safeMoney(account.availableBalance());
        BigDecimal heldBefore = safeMoney(account.heldBalance());
        BigDecimal availableAfter = availableBefore.add(amount);
        BigDecimal heldAfter = heldBefore.subtract(amount);

        accountRepository.save(rebuildAccount(account, availableAfter, heldAfter));

        List<TransactionMovement> movements = transactionMovementRepository.saveAll(List.of(
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        account.id(),
                        TransactionMovementType.CREDIT,
                        amount,
                        normalizedCurrency,
                        availableBefore,
                        availableAfter,
                        buildReleaseDescription(account.accountNumber()),
                        null
                ),
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        account.id(),
                        TransactionMovementType.DEBIT,
                        amount,
                        normalizedCurrency,
                        heldBefore,
                        heldAfter,
                        buildReleaseReserveDescription(account.accountNumber()),
                        null
                )
        ));

        Transaction completed = markCompleted(savedTransaction);
        Transaction persistedCompleted = transactionRepository.save(completed);

        limitPolicyService.registerUsage(limitRequest, limitEvaluation.checks().stream()
                .filter(check -> check.matched() && check.allowed())
                .map(check -> check.ruleId())
                .toList());

        accountingPostingPort.postTransaction(buildAccountingRequest(persistedCompleted, movements));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "RELEASE",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "account", account.accountNumber(),
                        "movement", "RELEASE"
                ),
                TenantTransactionAuditPayloads.transactionState(savedTransaction),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public TransactionResponse createFee(CreateFeeTransactionRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Fee",
                    buildFeeFingerprint(
                            request.accountId(),
                            request.amount(),
                            request.currency(),
                            request.externalReference(),
                            request.description()
                    ),
                    buildFeeFingerprint(
                            existing.sourceAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency()),
                            existing.externalReference(),
                            existing.description()
                    )
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        Account account = getAccountOrThrow(request.accountId(), "Account");

        validateTransactionalAccountType(account, "fee account", FEE_ACCOUNT_TYPES);
        validateAccountCurrencyMatches(account, request.currency(), "Fee currency must match account currency");
        String normalizedCurrency = request.currency().name();
        BigDecimal amount = safeMoney(request.amount());

        if (account.availableBalance().compareTo(amount) < 0) {
            throw new BusinessException(buildInsufficientBalanceMessage(
                    "Account",
                    amount,
                    account.availableBalance(),
                    account.currency().name()
            ));
        }

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                account.id(),
                null,
                account.accountType(),
                null,
                account.availableBalance(),
                null,
                TransactionType.FEE,
                request.currency(),
                amount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "Fee",
                    amount,
                    normalizedCurrency,
                    limitEvaluation.reason()
            ));
        }

        Transaction transaction = new Transaction(
                null,
                TransactionType.FEE,
                TransactionStatus.PROCESSING,
                TransactionChannel.ADMIN,
                amount,
                normalizedCurrency,
                account.id(),
                null,
                normalizeNullable(request.externalReference()),
                idempotencyKey,
                normalizeNullable(request.description()),
                null,
                "{\"operation\":\"fee\"}",
                null,
                null,
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (limitEvaluation.requiresReview()) {
            throw new BusinessException(buildReviewRequiredMessage(limitEvaluation.reason()));
        }

        BigDecimal balanceBefore = safeMoney(account.availableBalance());
        BigDecimal balanceAfter = balanceBefore.subtract(amount);
        accountRepository.save(rebuildAccount(account, balanceAfter));

        List<TransactionMovement> movements = transactionMovementRepository.saveAll(List.of(
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        account.id(),
                        TransactionMovementType.DEBIT,
                        amount,
                        normalizedCurrency,
                        balanceBefore,
                        balanceAfter,
                        buildFeeDescription(account.accountNumber()),
                        null
                )
        ));

        Transaction completed = markCompleted(savedTransaction);
        Transaction persistedCompleted = transactionRepository.save(completed);

        limitPolicyService.registerUsage(limitRequest, limitEvaluation.checks().stream()
                .filter(check -> check.matched() && check.allowed())
                .map(check -> check.ruleId())
                .toList());

        accountingPostingPort.postTransaction(buildAccountingRequest(persistedCompleted, movements));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                Map.of(
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "account", account.accountNumber(),
                        "movement", "DEBIT"
                )
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public TransactionResponse createAdjustment(CreateAdjustmentTransactionRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Adjustment",
                    buildAdjustmentFingerprint(
                            request.accountId(),
                            request.amount(),
                            request.currency(),
                            request.direction(),
                            request.externalReference(),
                            request.reason()
                    ),
                    buildAdjustmentFingerprint(
                            existing.sourceAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency()),
                            existing.description() != null && existing.description().toLowerCase(Locale.ROOT).contains("credit")
                                    ? AdjustmentDirection.CREDIT
                                    : AdjustmentDirection.DEBIT,
                            existing.externalReference(),
                            existing.description()
                    )
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        Account account = getAccountOrThrow(request.accountId(), "Account");

        validateTransactionalAccountType(account, "adjustment account", ADJUSTMENT_ACCOUNT_TYPES);
        validateAccountCurrencyMatches(account, request.currency(), "Adjustment currency must match account currency");
        String normalizedCurrency = request.currency().name();
        BigDecimal amount = safeMoney(request.amount());
        boolean credit = resolveDirection(request.direction());

        if (!credit && account.availableBalance().compareTo(amount) < 0) {
            throw new BusinessException(buildInsufficientBalanceMessage(
                    "Account",
                    amount,
                    account.availableBalance(),
                    account.currency().name()
            ));
        }

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                account.id(),
                null,
                account.accountType(),
                null,
                account.availableBalance(),
                null,
                TransactionType.ADJUSTMENT,
                request.currency(),
                amount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "Adjustment",
                    amount,
                    normalizedCurrency,
                    limitEvaluation.reason()
            ));
        }

        Transaction transaction = new Transaction(
                null,
                TransactionType.ADJUSTMENT,
                TransactionStatus.PROCESSING,
                TransactionChannel.ADMIN,
                amount,
                normalizedCurrency,
                account.id(),
                null,
                normalizeNullable(request.externalReference()),
                idempotencyKey,
                normalizeNullable(request.reason()),
                null,
                "{\"direction\":\"" + (credit ? "CREDIT" : "DEBIT") + "\"}",
                null,
                null,
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (limitEvaluation.requiresReview()) {
            throw new BusinessException(buildReviewRequiredMessage(limitEvaluation.reason()));
        }

        BigDecimal balanceBefore = safeMoney(account.availableBalance());
        BigDecimal balanceAfter = credit ? balanceBefore.add(amount) : balanceBefore.subtract(amount);
        accountRepository.save(rebuildAccount(account, balanceAfter));

        TransactionMovementType movementType = credit ? TransactionMovementType.CREDIT : TransactionMovementType.DEBIT;
        List<TransactionMovement> movements = transactionMovementRepository.saveAll(List.of(
                new TransactionMovement(
                        null,
                        savedTransaction.id(),
                        account.id(),
                        movementType,
                        amount,
                        normalizedCurrency,
                        balanceBefore,
                        balanceAfter,
                        buildAdjustmentDescription(account.accountNumber(), request.reason(), credit),
                        null
                )
        ));

        Transaction completed = markCompleted(savedTransaction);
        Transaction persistedCompleted = transactionRepository.save(completed);

        limitPolicyService.registerUsage(limitRequest, limitEvaluation.checks().stream()
                .filter(check -> check.matched() && check.allowed())
                .map(check -> check.ruleId())
                .toList());

        accountingPostingPort.postTransaction(buildAccountingRequest(persistedCompleted, movements));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "ADJUSTMENT",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "account", account.accountNumber(),
                        "movement", movementType.name()
                ),
                TenantTransactionAuditPayloads.transactionState(savedTransaction),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public QrTransactionIntentResponse createQrIntent(CreateQrTransactionIntentRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());
        Instant now = Instant.now();

        QrTransactionIntent existing = qrTransactionIntentRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "QR intent",
                    buildQrIntentFingerprint(
                            request.targetAccountId(),
                            request.amount(),
                            request.currency(),
                            request.externalReference(),
                            request.description()
                    ),
                    buildQrIntentFingerprint(
                            existing.targetAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency()),
                            existing.externalReference(),
                            existing.description()
                    )
            );
            return toQrTransactionIntentResponse(existing);
        }

        Account targetAccount = getAccountOrThrow(request.targetAccountId(), "Target");

        validateDepositAccount(targetAccount, request.currency());
        String normalizedCurrency = request.currency().name();
        BigDecimal amount = safeMoney(request.amount());

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                null,
                targetAccount.id(),
                null,
                targetAccount.accountType(),
                null,
                targetAccount.availableBalance(),
                TransactionType.PAYMENT,
                request.currency(),
                amount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "QR intent",
                    amount,
                    normalizedCurrency,
                    limitEvaluation.reason()
            ));
        }

        QrTransactionIntent intent = new QrTransactionIntent(
                null,
                QrTransactionIntentStatus.PENDING,
                TransactionChannel.QR,
                amount,
                normalizedCurrency,
                targetAccount.id(),
                normalizeNullable(request.externalReference()),
                normalizeNullable(request.description()),
                idempotencyKey,
                null,
                requestedByUserId,
                null,
                now.plus(QR_INTENT_TTL),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        QrTransactionIntent savedIntent = qrTransactionIntentRepository.save(intent);
        QrTransactionIntent savedWithPayload = qrTransactionIntentRepository.save(new QrTransactionIntent(
                savedIntent.id(),
                savedIntent.status(),
                savedIntent.channel(),
                savedIntent.amount(),
                savedIntent.currency(),
                savedIntent.targetAccountId(),
                savedIntent.externalReference(),
                savedIntent.description(),
                savedIntent.idempotencyKey(),
                savedIntent.confirmedTransactionId(),
                savedIntent.requestedByUserId(),
                savedIntent.confirmedAt(),
                savedIntent.expiresAt(),
                savedIntent.cancelledAt(),
                savedIntent.cancelledByUserId(),
                savedIntent.payerAccountId(),
                savedIntent.paidAmount(),
                savedIntent.paidCurrency(),
                buildQrPayload(savedIntent.id()),
                savedIntent.qrSignature(),
                savedIntent.createdAt(),
                savedIntent.updatedAt()
        ));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_CREATED,
                "QR_TRANSACTION_INTENT",
                savedWithPayload.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "CREATE_QR_INTENT",
                        "type", TransactionType.PAYMENT.name(),
                        "status", savedWithPayload.status().name(),
                        "channel", savedWithPayload.channel().name(),
                        "account", targetAccount.accountNumber()
                ),
                null,
                TenantTransactionAuditPayloads.intentState(savedWithPayload)
        );

        return toQrTransactionIntentResponse(savedWithPayload);
    }

    @Transactional
    public QrTransactionIntentResponse createMyQrIntent(CreateQrTransactionIntentRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        Account targetAccount = getAccountOrThrow(request.targetAccountId(), "Target");
        if (targetAccount.userId() == null || !targetAccount.userId().equals(requestedByUserId)) {
            throw new BusinessException("Target account does not belong to the authenticated user");
        }

        return createQrIntent(request);
    }

    @Transactional
    public TransactionResponse reverseTransaction(UUID transactionId, CreateReversalTransactionRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Reversal",
                    buildReversalFingerprint(transactionId, request.reason()),
                    buildReversalFingerprint(existing.parentTransactionId(), existing.description())
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        Transaction original = getTransactionOrThrow(transactionId);

        if (original.status() != TransactionStatus.COMPLETED) {
            throw new BusinessException("Only completed transactions can be reversed");
        }

        List<TransactionMovement> originalMovements = transactionMovementRepository.findAllByTransactionId(original.id());
        if (originalMovements.isEmpty()) {
            throw new BusinessException("Original transaction has no movements to reverse");
        }

        Transaction reversal = new Transaction(
                null,
                TransactionType.REVERSAL,
                TransactionStatus.PROCESSING,
                TransactionChannel.ADMIN,
                safeMoney(original.amount()),
                original.currency(),
                original.sourceAccountId(),
                original.targetAccountId(),
                original.externalReference(),
                idempotencyKey,
                normalizeNullable(request.reason()),
                null,
                null,
                original.id(),
                original.id(),
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedReversal = transactionRepository.save(reversal);

        List<TransactionMovement> reversalMovements = applyOppositeMovements(savedReversal.id(), originalMovements);
        persistMovementAccountChanges(originalMovements, reversalMovements);

        Transaction completed = markCompleted(savedReversal);
        Transaction persistedCompleted = transactionRepository.save(completed);
        transactionRepository.save(new Transaction(
                original.id(),
                original.type(),
                TransactionStatus.REVERSED,
                original.channel(),
                original.amount(),
                original.currency(),
                original.sourceAccountId(),
                original.targetAccountId(),
                original.externalReference(),
                original.idempotencyKey(),
                original.description(),
                original.failureReason(),
                original.metadata(),
                original.parentTransactionId(),
                persistedCompleted.id(),
                original.requestedByUserId(),
                original.approvedByUserId(),
                original.processedAt(),
                original.createdAt(),
                original.updatedAt()
        ));

        accountingPostingPort.postTransaction(buildAccountingRequest(persistedCompleted, reversalMovements));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "REVERSAL",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "originalTransactionId", original.id().toString()
                ),
                TenantTransactionAuditPayloads.transactionState(savedReversal),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, reversalMovements);

        return transactionMapper.toResponse(persistedCompleted, reversalMovements);
    }

    @Transactional
    public TransactionResponse refundTransaction(UUID transactionId, CreateRefundTransactionRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);
        if (existing != null) {
            replayOrConflict(
                    "Refund",
                    buildRefundRequestFingerprint(transactionId, request.amount(), request.reason()),
                    buildRefundRequestFingerprint(existing.parentTransactionId(), existing.amount(), existing.description())
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        Transaction original = getTransactionOrThrow(transactionId);

        if (original.status() == TransactionStatus.REVERSED || original.status() == TransactionStatus.CANCELLED) {
            throw new BusinessException("Refund cannot be applied to a reversed or cancelled transaction");
        }

        if (original.type() == TransactionType.TRANSFER) {
            throw new BusinessException("Transfers must be reversed, not refunded");
        }

        List<Transaction> children = transactionRepository.findAllByParentTransactionId(original.id());
        if (!children.isEmpty()) {
            throw new BusinessException("Refunds must be complete and can only be applied once");
        }

        List<TransactionMovement> originalMovements = transactionMovementRepository.findAllByTransactionId(original.id());
        if (originalMovements.isEmpty()) {
            throw new BusinessException("Original transaction has no movements to refund");
        }

        String normalizedCurrency = original.currency().trim().toUpperCase(Locale.ROOT);
        BigDecimal requestedAmount = safeMoney(request.amount());
        BigDecimal originalAmount = safeMoney(original.amount());
        if (requestedAmount.compareTo(originalAmount) != 0) {
            throw new BusinessException("Refund amount must match the full original transaction amount");
        }

        Transaction refund = new Transaction(
                null,
                TransactionType.REFUND,
                TransactionStatus.PROCESSING,
                TransactionChannel.ADMIN,
                requestedAmount,
                normalizedCurrency,
                original.sourceAccountId(),
                original.targetAccountId(),
                original.externalReference(),
                idempotencyKey,
                normalizeNullable(request.reason()),
                null,
                original.metadata(),
                original.id(),
                original.id(),
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedRefund = transactionRepository.save(refund);
        List<TransactionMovement> movements = applyOppositeMovements(savedRefund.id(), originalMovements);
        persistMovementAccountChanges(originalMovements, movements);

        Transaction completed = markCompleted(savedRefund);
        Transaction persistedCompleted = transactionRepository.save(completed);

        transactionRepository.save(new Transaction(
                original.id(),
                original.type(),
                TransactionStatus.REVERSED,
                original.channel(),
                original.amount(),
                original.currency(),
                original.sourceAccountId(),
                original.targetAccountId(),
                original.externalReference(),
                original.idempotencyKey(),
                original.description(),
                original.failureReason(),
                original.metadata(),
                original.parentTransactionId(),
                persistedCompleted.id(),
                original.requestedByUserId(),
                original.approvedByUserId(),
                original.processedAt(),
                original.createdAt(),
                original.updatedAt()
        ));

        accountingPostingPort.postTransaction(buildAccountingRequest(persistedCompleted, movements));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "REFUND",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "originalTransactionId", original.id().toString(),
                        "refundAmount", requestedAmount.toPlainString()
                ),
                TenantTransactionAuditPayloads.transactionState(savedRefund),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public TransactionResponse confirmQrIntent(UUID transactionId, ConfirmQrTransactionRequest request) {
        UUID requestedByUserId = resolveRequestedByUserId();
        String idempotencyKey = normalizeText(request.idempotencyKey());
        Instant now = Instant.now();

        Transaction existing = transactionRepository
                .findByRequestedByUserIdAndIdempotencyKey(requestedByUserId, idempotencyKey)
                .orElse(null);

        QrTransactionIntent intent = qrTransactionIntentRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("QR intent not found with id: " + transactionId));

        if (intent.channel() != TransactionChannel.QR) {
            throw new BusinessException("QR intent is not a QR payment intent");
        }

        if (intent.status() != QrTransactionIntentStatus.PENDING) {
            throw new BusinessException("QR intent is not pending confirmation");
        }

        if (intent.expiresAt() != null && intent.expiresAt().isBefore(now)) {
            QrTransactionIntent expiredIntent = new QrTransactionIntent(
                    intent.id(),
                    QrTransactionIntentStatus.EXPIRED,
                    intent.channel(),
                    intent.amount(),
                    intent.currency(),
                    intent.targetAccountId(),
                    intent.externalReference(),
                    intent.description(),
                    intent.idempotencyKey(),
                    intent.confirmedTransactionId(),
                    intent.requestedByUserId(),
                    intent.confirmedAt(),
                    intent.expiresAt(),
                    intent.cancelledAt(),
                    intent.cancelledByUserId(),
                    intent.payerAccountId(),
                    intent.paidAmount(),
                    intent.paidCurrency(),
                    intent.qrPayload(),
                    intent.qrSignature(),
                    intent.createdAt(),
                    intent.updatedAt()
            );
            qrTransactionIntentRepository.save(expiredIntent);
            throw new BusinessException("QR intent has expired");
        }

        if (intent.targetAccountId() == null) {
            throw new BusinessException("QR intent does not have a target account");
        }

        Account sourceAccount = getAccountOrThrow(request.sourceAccountId(), "Source");
        Account targetAccount = getAccountOrThrow(intent.targetAccountId(), "Target");

        if (sourceAccount.userId() == null || !sourceAccount.userId().equals(requestedByUserId)) {
            throw new BusinessException("Source account does not belong to the authenticated user");
        }

        if (sourceAccount.id().equals(targetAccount.id())) {
            throw new BusinessException("Source and target accounts must be different");
        }

        if (sourceAccount.status() == AccountStatus.CLOSED || targetAccount.status() == AccountStatus.CLOSED) {
            throw new BusinessException("Closed accounts cannot participate in QR payments");
        }

        if (!sourceAccount.active() || !targetAccount.active()) {
            throw new BusinessException("Both accounts must be active for QR payments");
        }

        CurrencyCode intentCurrency = parseCurrencyCode(intent.currency(), "QR currency is invalid");
        validateSupportedCurrency(sourceAccount.currency(), "Source account currency is invalid");
        validateTransactionalAccountType(sourceAccount, "QR source account", QR_SOURCE_ACCOUNT_TYPES);
        validateTransactionalAccountType(targetAccount, "QR target account", QR_TARGET_ACCOUNT_TYPES);
        validateAccountCurrencyMatches(targetAccount, intentCurrency, "QR intent currency must match target account currency");

        if (existing != null) {
            replayOrConflict(
                    "QR confirmation",
                    buildQrConfirmFingerprint(
                            request.sourceAccountId(),
                            intent.targetAccountId(),
                            intent.amount(),
                            intentCurrency
                    ),
                    buildQrConfirmFingerprint(
                            existing.sourceAccountId(),
                            existing.targetAccountId(),
                            existing.amount(),
                            CurrencyCode.valueOf(existing.currency())
                    )
            );
            return transactionMapper.toResponse(existing, transactionMovementRepository.findAllByTransactionId(existing.id()));
        }

        BigDecimal targetAmount = safeMoney(intent.amount());
        FxQuote fxQuote = currencyExchangeService.calculateForTargetAmount(
                FxOperationCode.PAYMENT,
                targetAmount,
                sourceAccount.currency(),
                targetAccount.currency()
        );
        TransactionFxDetailResponse fxDetail = currencyExchangeService.toResponse(fxQuote);
        String fxMetadata = toFxMetadataJson(fxDetail);
        BigDecimal sourceAmount = fxQuote.sourceAmount();

        if (sourceAccount.availableBalance().compareTo(sourceAmount) < 0) {
            throw new BusinessException(buildInsufficientBalanceMessage(
                    "Source account",
                    sourceAmount,
                    sourceAccount.availableBalance(),
                    sourceAccount.currency().name()
            ));
        }

        LimitEvaluationRequest limitRequest = new LimitEvaluationRequest(
                requestedByUserId,
                sourceAccount.id(),
                targetAccount.id(),
                sourceAccount.accountType(),
                targetAccount.accountType(),
                sourceAccount.availableBalance(),
                targetAccount.availableBalance(),
                TransactionType.PAYMENT,
                sourceAccount.currency(),
                sourceAmount
        );

        LimitEvaluationResponse limitEvaluation = limitPolicyService.evaluate(limitRequest);
        if (!limitEvaluation.allowed() && !limitEvaluation.requiresReview()) {
            throw new BusinessException(buildLimitDeniedMessage(
                    "QR confirmation",
                    sourceAmount,
                    sourceAccount.currency().name(),
                    limitEvaluation.reason()
            ));
        }

        if (limitEvaluation.requiresReview()) {
            throw new BusinessException(buildReviewRequiredMessage(limitEvaluation.reason()));
        }

        BigDecimal sourceBefore = safeMoney(sourceAccount.availableBalance());
        BigDecimal targetBefore = safeMoney(targetAccount.availableBalance());
        BigDecimal sourceAfter = sourceBefore.subtract(sourceAmount);
        BigDecimal targetCreditAmount = fxQuote.targetAmountNet();
        BigDecimal targetAfter = targetBefore.add(targetCreditAmount);

        accountRepository.save(rebuildAccount(sourceAccount, sourceAfter));
        accountRepository.save(rebuildAccount(targetAccount, targetAfter));

        Transaction confirmed = new Transaction(
                null,
                TransactionType.PAYMENT,
                TransactionStatus.COMPLETED,
                TransactionChannel.QR,
                sourceAmount,
                sourceAccount.currency().name(),
                sourceAccount.id(),
                targetAccount.id(),
                intent.externalReference(),
                idempotencyKey,
                intent.description(),
                null,
                fxMetadata,
                null,
                null,
                requestedByUserId,
                null,
                null,
                null,
                null
        );

        Transaction savedConfirmed = transactionRepository.save(confirmed);

        List<TransactionMovement> movements = transactionMovementRepository.saveAll(List.of(
                new TransactionMovement(
                        null,
                        savedConfirmed.id(),
                        sourceAccount.id(),
                        TransactionMovementType.DEBIT,
                        sourceAmount,
                        sourceAccount.currency().name(),
                        sourceBefore,
                        sourceAfter,
                        buildPaymentDescription(intent.externalReference(), sourceAccount.accountNumber(), fxDetail),
                        null
                ),
                new TransactionMovement(
                        null,
                        savedConfirmed.id(),
                        targetAccount.id(),
                        TransactionMovementType.CREDIT,
                        targetCreditAmount,
                        targetAccount.currency().name(),
                        targetBefore,
                        targetAfter,
                        "QR payment to " + targetAccount.accountNumber(),
                        null
                )
        ));

        Transaction completed = markCompleted(savedConfirmed);
        Transaction persistedCompleted = transactionRepository.save(completed);

        QrTransactionIntent updatedIntent = new QrTransactionIntent(
                intent.id(),
                QrTransactionIntentStatus.CONFIRMED,
                intent.channel(),
                intent.amount(),
                intent.currency(),
                intent.targetAccountId(),
                intent.externalReference(),
                intent.description(),
                intent.idempotencyKey(),
                persistedCompleted.id(),
                intent.requestedByUserId(),
                now,
                intent.expiresAt(),
                intent.cancelledAt(),
                intent.cancelledByUserId(),
                sourceAccount.id(),
                sourceAmount,
                sourceAccount.currency().name(),
                intent.qrPayload(),
                intent.qrSignature(),
                intent.createdAt(),
                now
        );
        qrTransactionIntentRepository.save(updatedIntent);

        limitPolicyService.registerUsage(limitRequest, limitEvaluation.checks().stream()
                .filter(check -> check.matched() && check.allowed())
                .map(check -> check.ruleId())
                .toList());

        accountingPostingPort.postTransaction(buildAccountingRequest(persistedCompleted, movements));

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "QR_TRANSACTION_INTENT",
                intent.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "CONFIRM_QR_INTENT",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "channel", persistedCompleted.channel().name(),
                        "intentId", intent.id().toString(),
                        "sourceAccount", sourceAccount.accountNumber(),
                        "targetAccount", targetAccount.accountNumber()
                ),
                TenantTransactionAuditPayloads.intentState(intent),
                TenantTransactionAuditPayloads.intentState(updatedIntent)
        );

        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_COMPLETED,
                "TRANSACTION",
                persistedCompleted.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "CONFIRM_QR_TRANSACTION",
                        "type", persistedCompleted.type().name(),
                        "status", persistedCompleted.status().name(),
                        "channel", persistedCompleted.channel().name(),
                        "intentId", intent.id().toString(),
                        "sourceAccount", sourceAccount.accountNumber(),
                        "targetAccount", targetAccount.accountNumber()
                ),
                TenantTransactionAuditPayloads.transactionState(savedConfirmed),
                TenantTransactionAuditPayloads.transactionState(persistedCompleted)
        );

        publishTransactionNotifications(persistedCompleted, movements);

        return transactionMapper.toResponse(persistedCompleted, movements);
    }

    @Transactional
    public QrTransactionIntentResponse getMyQrIntent(UUID transactionId) {
        UUID requestedByUserId = resolveRequestedByUserId();
        QrTransactionIntent intent = getQrIntentOrThrow(transactionId);
        ensureQrIntentBelongsToUser(intent, requestedByUserId);
        QrTransactionIntent refreshed = refreshQrIntentStatusIfExpired(intent);
        return toQrTransactionIntentResponse(refreshed);
    }

    @Transactional
    public QrTransactionIntentResponse cancelMyQrIntent(UUID transactionId) {
        UUID requestedByUserId = resolveRequestedByUserId();
        QrTransactionIntent intent = getQrIntentOrThrow(transactionId);
        ensureQrIntentBelongsToUser(intent, requestedByUserId);

        QrTransactionIntent current = refreshQrIntentStatusIfExpired(intent);
        if (current.status() != QrTransactionIntentStatus.PENDING) {
            throw new BusinessException("QR intent cannot be cancelled in its current state");
        }

        Instant now = Instant.now();
        QrTransactionIntent cancelled = new QrTransactionIntent(
                current.id(),
                QrTransactionIntentStatus.CANCELLED,
                current.channel(),
                current.amount(),
                current.currency(),
                current.targetAccountId(),
                current.externalReference(),
                current.description(),
                current.idempotencyKey(),
                current.confirmedTransactionId(),
                current.requestedByUserId(),
                current.confirmedAt(),
                current.expiresAt(),
                now,
                requestedByUserId,
                current.payerAccountId(),
                current.paidAmount(),
                current.paidCurrency(),
                current.qrPayload(),
                current.qrSignature(),
                current.createdAt(),
                now
        );

        QrTransactionIntent saved = qrTransactionIntentRepository.save(cancelled);
        auditTrailService.recordTenantEvent(
                AuditEventTypes.TRANSACTION_UPDATED,
                "QR_TRANSACTION_INTENT",
                saved.id().toString(),
                TenantTransactionAuditPayloads.details(
                        "operation", "CANCEL_QR_INTENT",
                        "status", saved.status().name(),
                        "channel", saved.channel().name()
                ),
                TenantTransactionAuditPayloads.intentState(intent),
                TenantTransactionAuditPayloads.intentState(saved)
        );

        return toQrTransactionIntentResponse(saved);
    }

    private void validateTransferAccounts(Account sourceAccount, Account targetAccount, CurrencyCode currency) {
        if (sourceAccount.status() == AccountStatus.CLOSED || targetAccount.status() == AccountStatus.CLOSED) {
            throw new BusinessException("Closed accounts cannot participate in transfers");
        }

        if (!sourceAccount.active() || !targetAccount.active()) {
            throw new BusinessException("Both accounts must be active");
        }

        validateTransactionalAccountType(sourceAccount, "transfer source account", TRANSFER_SOURCE_ACCOUNT_TYPES);
        validateTransactionalAccountType(targetAccount, "transfer target account", TRANSFER_TARGET_ACCOUNT_TYPES);

        if (sourceAccount.currency() != currency) {
            throw new BusinessException("Transfer currency must match source account currency");
        }
    }

    private void validateDepositAccount(Account targetAccount, CurrencyCode currency) {
        if (targetAccount.status() == AccountStatus.CLOSED) {
            throw new BusinessException("Closed accounts cannot receive deposits");
        }

        if (!targetAccount.active()) {
            throw new BusinessException("Target account must be active");
        }

        validateTransactionalAccountType(targetAccount, "deposit target account", DEPOSIT_TARGET_ACCOUNT_TYPES);
        validateSupportedCurrency(currency, "Deposit currency is invalid");
    }

    private void validateWithdrawalAccount(Account sourceAccount, CurrencyCode currency) {
        if (sourceAccount.status() == AccountStatus.CLOSED) {
            throw new BusinessException("Closed accounts cannot process withdrawals");
        }

        if (!sourceAccount.active()) {
            throw new BusinessException("Source account must be active");
        }

        validateTransactionalAccountType(sourceAccount, "withdrawal source account", WITHDRAWAL_SOURCE_ACCOUNT_TYPES);
        validateSupportedCurrency(currency, "Withdrawal currency is invalid");
    }

    private void validatePaymentAccount(Account sourceAccount, CurrencyCode currency) {
        if (sourceAccount.status() == AccountStatus.CLOSED) {
            throw new BusinessException("Closed accounts cannot process payments");
        }

        if (!sourceAccount.active()) {
            throw new BusinessException("Source account must be active");
        }

        validateTransactionalAccountType(sourceAccount, "payment source account", TRANSFER_SOURCE_ACCOUNT_TYPES);
        validateSupportedCurrency(currency, "Payment currency is invalid");
    }

    private void validateAccountCurrencyMatches(Account account, CurrencyCode currency, String message) {
        validateSupportedCurrency(currency, message);

        if (account.currency() != currency) {
            throw new BusinessException(message + ": " + currency.name());
        }
    }

    private CurrencyCode parseCurrencyCode(String currency, String message) {
        if (currency == null || currency.isBlank()) {
            throw new BusinessException(message);
        }

        try {
            return CurrencyCode.valueOf(currency.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(message + ": " + currency);
        }
    }

    private void validateSupportedCurrency(CurrencyCode currency, String message) {
        if (currency == null) {
            throw new BusinessException(message);
        }
    }

    private void validateTransactionalAccountType(Account account, String operationLabel, EnumSet<AccountType> allowedTypes) {
        if (account == null) {
            throw new BusinessException("Account is required for " + operationLabel);
        }

        if (allowedTypes == null || !allowedTypes.contains(account.accountType())) {
            throw new BusinessException(
                    "Account type " + account.accountType().name() + " is not allowed for " + operationLabel
                            + ". Use a transactional account type such as " + supportedTransactionalTypes()
            );
        }
    }

    private TransactionAccountingRequest buildAccountingRequest(Transaction transaction, List<TransactionMovement> movements) {
        return new TransactionAccountingRequest(
                transaction.id(),
                transaction.type().name(),
                transaction.status().name(),
                transaction.currency(),
                transaction.amount(),
                transaction.sourceAccountId(),
                transaction.targetAccountId(),
                transaction.externalReference(),
                transaction.description(),
                transaction.processedAt(),
                transaction.metadata(),
                movements.stream()
                        .map(movement -> new TransactionAccountingMovementRequest(
                                movement.accountId(),
                                movement.movementType().name(),
                                movement.amount(),
                                movement.currency(),
                                movement.balanceBefore(),
                                movement.balanceAfter(),
                                movement.description()
                        ))
                        .toList()
        );
    }

    private String buildDepositDescription(String accountNumber, TransactionFxDetailResponse fxDetail) {
        if (fxDetail != null && fxDetail.applied()) {
            return "Deposit to " + accountNumber + " (" + fxDetail.sourceAmount() + " " + fxDetail.sourceCurrency()
                    + " -> " + fxDetail.targetAmountNet() + " " + fxDetail.targetCurrency() + ")";
        }

        return "Deposit to " + accountNumber;
    }

    private String buildWithdrawalDescription(String accountNumber, TransactionFxDetailResponse fxDetail) {
        if (fxDetail != null && fxDetail.applied()) {
            return "Withdrawal from " + accountNumber + " (" + fxDetail.sourceAmount() + " " + fxDetail.sourceCurrency()
                    + " -> " + fxDetail.targetAmountNet() + " " + fxDetail.targetCurrency() + ")";
        }

        return "Withdrawal from " + accountNumber;
    }

    private String buildTransferCreditDescription(String sourceAccountNumber, TransactionFxDetailResponse fxDetail) {
        if (fxDetail != null && fxDetail.applied()) {
            return "Transfer from " + sourceAccountNumber + " (" + fxDetail.sourceAmount() + " " + fxDetail.sourceCurrency()
                    + " -> " + fxDetail.targetAmountNet() + " " + fxDetail.targetCurrency() + ")";
        }

        return "Transfer from " + sourceAccountNumber;
    }

    private String buildPaymentDescription(String externalReference, String accountNumber, TransactionFxDetailResponse fxDetail) {
        if (externalReference != null && !externalReference.isBlank()) {
            if (fxDetail != null && fxDetail.applied()) {
                return "Payment to " + externalReference.trim() + " (" + fxDetail.sourceAmount() + " " + fxDetail.sourceCurrency()
                        + " -> " + fxDetail.targetAmountNet() + " " + fxDetail.targetCurrency() + ")";
            }

            return "Payment to " + externalReference.trim();
        }

        if (fxDetail != null && fxDetail.applied()) {
            return "Payment from " + accountNumber + " (" + fxDetail.sourceAmount() + " " + fxDetail.sourceCurrency()
                    + " -> " + fxDetail.targetAmountNet() + " " + fxDetail.targetCurrency() + ")";
        }

        return "Payment from " + accountNumber;
    }

    private String toFxMetadataJson(TransactionFxDetailResponse fxDetail) {
        if (fxDetail == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(fxDetail);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("Unable to serialize FX detail");
        }
    }

    private String buildRefundDescription(Transaction original, String reason) {
        String base = original.externalReference() != null && !original.externalReference().isBlank()
                ? original.externalReference().trim()
                : original.id().toString();

        if (reason != null && !reason.isBlank()) {
            return "Refund for " + base + " - " + reason.trim();
        }

        return "Refund for " + base;
    }

    private String buildHoldDescription(String accountNumber) {
        return "Hold from " + accountNumber;
    }

    private String buildHoldReserveDescription(String accountNumber) {
        return "Reserve hold on " + accountNumber;
    }

    private String buildReleaseDescription(String accountNumber) {
        return "Release to " + accountNumber;
    }

    private String buildReleaseReserveDescription(String accountNumber) {
        return "Release reserved hold on " + accountNumber;
    }

    private String buildFeeDescription(String accountNumber) {
        return "Fee charged to " + accountNumber;
    }

    private String buildAdjustmentDescription(String accountNumber, String reason, boolean credit) {
        String action = credit ? "Adjustment credit to " : "Adjustment debit from ";
        if (reason != null && !reason.isBlank()) {
            return action + accountNumber + " - " + reason.trim();
        }

        return action + accountNumber;
    }

    private String buildReviewRequiredMessage(String reason) {
        if (reason == null || reason.isBlank()) {
            return "Operation requires manual review and the review workflow is not implemented";
        }

        return "Operation requires manual review: " + reason;
    }

    private String buildLimitDeniedMessage(String operationName, BigDecimal amount, String currency, String reason) {
        String fallback = operationName + " is not allowed by the current limits for "
                + safeMoney(amount).toPlainString() + " " + currency;

        if (reason == null || reason.isBlank()) {
            return fallback;
        }

        return reason + " (" + fallback + ")";
    }

    private void replayOrConflict(String operationName, String expectedFingerprint, String existingFingerprint) {
        if (!expectedFingerprint.equals(existingFingerprint)) {
            throw new BusinessException(operationName + " idempotency key was already used for a different request. Generate a new idempotencyKey.");
        }
    }

    private String buildTransferFingerprint(
            UUID sourceAccountId,
            UUID targetAccountId,
            BigDecimal amount,
            CurrencyCode currency,
            String description
    ) {
        return fingerprint("TRANSFER", sourceAccountId, targetAccountId, amount, currency, description);
    }

    private String buildDepositFingerprint(
            UUID targetAccountId,
            BigDecimal amount,
            CurrencyCode currency,
            TransactionChannel channel,
            String externalReference,
            String description
    ) {
        return fingerprint("DEPOSIT", targetAccountId, amount, currency, channel, externalReference, description);
    }

    private String buildWithdrawalFingerprint(
            UUID sourceAccountId,
            BigDecimal amount,
            CurrencyCode currency,
            TransactionChannel channel,
            String description
    ) {
        return fingerprint("WITHDRAWAL", sourceAccountId, amount, currency, channel, description);
    }

    private String buildPaymentFingerprint(
            UUID sourceAccountId,
            BigDecimal amount,
            CurrencyCode currency,
            TransactionChannel channel,
            String externalReference,
            String description
    ) {
        return fingerprint("PAYMENT", sourceAccountId, amount, currency, channel, externalReference, description);
    }

    private String buildHoldFingerprint(UUID accountId, BigDecimal amount, CurrencyCode currency, String description) {
        return fingerprint("HOLD", accountId, amount, currency, description);
    }

    private String buildReleaseFingerprint(UUID accountId, BigDecimal amount, CurrencyCode currency, String description) {
        return fingerprint("RELEASE", accountId, amount, currency, description);
    }

    private String buildFeeFingerprint(
            UUID accountId,
            BigDecimal amount,
            CurrencyCode currency,
            String externalReference,
            String description
    ) {
        return fingerprint("FEE", accountId, amount, currency, externalReference, description);
    }

    private String buildAdjustmentFingerprint(
            UUID accountId,
            BigDecimal amount,
            CurrencyCode currency,
            AdjustmentDirection direction,
            String externalReference,
            String reason
    ) {
        return fingerprint("ADJUSTMENT", accountId, amount, currency, direction, externalReference, reason);
    }

    private String buildQrIntentFingerprint(
            UUID targetAccountId,
            BigDecimal amount,
            CurrencyCode currency,
            String externalReference,
            String description
    ) {
        return fingerprint("QR_INTENT", targetAccountId, amount, currency, externalReference, description);
    }

    private String buildReversalFingerprint(UUID originalTransactionId, String reason) {
        return fingerprint("REVERSAL", originalTransactionId, reason);
    }

    private String buildRefundRequestFingerprint(UUID originalTransactionId, BigDecimal amount, String reason) {
        return fingerprint("REFUND", originalTransactionId, amount, reason);
    }

    private String buildQrConfirmFingerprint(UUID sourceAccountId, UUID targetAccountId, BigDecimal sourceAmount, CurrencyCode sourceCurrency) {
        return fingerprint("QR_CONFIRM", sourceAccountId, targetAccountId, sourceAmount, sourceCurrency);
    }

    private String fingerprint(Object... parts) {
        return Arrays.stream(parts)
                .map(this::normalizeFingerprintPart)
                .collect(Collectors.joining("|"));
    }

    private String normalizeFingerprintPart(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof BigDecimal bigDecimal) {
            return safeMoney(bigDecimal).stripTrailingZeros().toPlainString();
        }

        if (value instanceof CurrencyCode currencyCode) {
            return currencyCode.name();
        }

        if (value instanceof TransactionChannel channel) {
            return channel.name();
        }

        if (value instanceof AdjustmentDirection direction) {
            return direction.name();
        }

        if (value instanceof UUID uuid) {
            return uuid.toString();
        }

        if (value instanceof Enum<?> enumValue) {
            return enumValue.name();
        }

        if (value instanceof String text) {
            String normalized = normalizeNullable(text);
            return normalized == null ? "" : normalized;
        }

        return value.toString();
    }

    private AdjustmentDirection parseAdjustmentDirectionFromMetadata(String metadata) {
        if (metadata != null && metadata.toUpperCase(Locale.ROOT).contains("\"DIRECTION\":\"CREDIT\"")) {
            return AdjustmentDirection.CREDIT;
        }

        return AdjustmentDirection.DEBIT;
    }

    private boolean resolveDirection(AdjustmentDirection direction) {
        if (direction == null) {
            throw new BusinessException("Adjustment direction is required");
        }

        return direction == AdjustmentDirection.CREDIT;
    }

    private String supportedTransactionalTypes() {
        return TRANSACTIONAL_ACCOUNT_TYPES.stream()
                .map(AccountType::name)
                .collect(Collectors.joining(", "));
    }

    private List<TransactionMovement> applyOppositeMovements(UUID transactionId, List<TransactionMovement> originalMovements) {
        return originalMovements.stream()
                .map(originalMovement -> {
                    Account account = getAccountOrThrow(originalMovement.accountId(), "Account");

                    BigDecimal before = safeMoney(account.availableBalance());
                    BigDecimal amount = safeMoney(originalMovement.amount());
                    TransactionMovementType oppositeType = originalMovement.movementType() == TransactionMovementType.DEBIT
                            ? TransactionMovementType.CREDIT
                            : TransactionMovementType.DEBIT;

                    BigDecimal after = oppositeType == TransactionMovementType.CREDIT
                            ? before.add(amount)
                            : before.subtract(amount);

                    if (oppositeType == TransactionMovementType.DEBIT && before.compareTo(amount) < 0) {
                        throw new BusinessException(buildInsufficientBalanceMessage(
                                "Account",
                                amount,
                                before,
                                account.currency().name()
                        ));
                    }

                    accountRepository.save(rebuildAccount(account, after));

                    return new TransactionMovement(
                            null,
                            transactionId,
                            account.id(),
                            oppositeType,
                            amount,
                            originalMovement.currency(),
                            before,
                            after,
                            "Reversal of " + originalMovement.description(),
                            null
                    );
                })
                .toList();
    }

    private void persistMovementAccountChanges(List<TransactionMovement> originalMovements, List<TransactionMovement> newMovements) {
        if (originalMovements.size() != newMovements.size()) {
            return;
        }

        for (int index = 0; index < originalMovements.size(); index++) {
            TransactionMovement movement = newMovements.get(index);
            Account account = getAccountOrThrow(movement.accountId(), "Account");
            accountRepository.save(rebuildAccount(account, movement.balanceAfter()));
        }
    }

    private Account rebuildAccount(Account account, BigDecimal newAvailableBalance) {
        return new Account(
                account.id(),
                account.userId(),
                account.accountNumber(),
                account.accountName(),
                account.customAlias(),
                account.accountType(),
                account.currency(),
                safeMoney(newAvailableBalance),
                safeMoney(account.heldBalance()),
                account.status(),
                account.statusReason(),
                account.active(),
                account.primary(),
                account.openedAt(),
                account.closedAt(),
                account.createdAt(),
                account.updatedAt()
        );
    }

    private Account rebuildAccount(Account account, BigDecimal newAvailableBalance, BigDecimal newHeldBalance) {
        return new Account(
                account.id(),
                account.userId(),
                account.accountNumber(),
                account.accountName(),
                account.customAlias(),
                account.accountType(),
                account.currency(),
                safeMoney(newAvailableBalance),
                safeMoney(newHeldBalance),
                account.status(),
                account.statusReason(),
                account.active(),
                account.primary(),
                account.openedAt(),
                account.closedAt(),
                account.createdAt(),
                account.updatedAt()
        );
    }

    private TransactionChannel resolveChannel(TransactionChannel method, TransactionChannel defaultChannel) {
        if (method == null) {
            return defaultChannel;
        }
        return method;
    }

    private QrTransactionIntentResponse toQrTransactionIntentResponse(QrTransactionIntent intent) {
        return new QrTransactionIntentResponse(
                intent.id(),
                intent.status().name(),
                intent.channel().name(),
                intent.amount(),
                intent.currency(),
                intent.targetAccountId(),
                intent.externalReference(),
                intent.description(),
                intent.idempotencyKey(),
                intent.confirmedTransactionId(),
                intent.expiresAt(),
                intent.confirmedAt(),
                intent.cancelledAt(),
                intent.cancelledByUserId(),
                intent.payerAccountId(),
                intent.paidAmount(),
                intent.paidCurrency(),
                intent.qrPayload(),
                intent.qrSignature(),
                intent.createdAt(),
                intent.updatedAt()
        );
    }

    private Transaction markCompleted(Transaction transaction) {
        return new Transaction(
                transaction.id(),
                transaction.type(),
                TransactionStatus.COMPLETED,
                transaction.channel(),
                transaction.amount(),
                transaction.currency(),
                transaction.sourceAccountId(),
                transaction.targetAccountId(),
                transaction.externalReference(),
                transaction.idempotencyKey(),
                transaction.description(),
                null,
                transaction.metadata(),
                transaction.parentTransactionId(),
                transaction.reversedTransactionId(),
                transaction.requestedByUserId(),
                transaction.approvedByUserId(),
                Instant.now(),
                transaction.createdAt(),
                transaction.updatedAt()
        );
    }

    private void publishTransactionNotifications(Transaction transaction, List<TransactionMovement> movements) {
        if (transaction == null || transaction.id() == null || movements == null || movements.isEmpty()) {
            return;
        }

        try {
            Map<UUID, List<TransactionMovement>> movementsByUser = movements.stream()
                    .map(movement -> Map.entry(movement, getAccountOrThrow(movement.accountId(), "Account")))
                    .filter(entry -> entry.getValue() != null && entry.getValue().userId() != null)
                    .collect(Collectors.groupingBy(
                            entry -> entry.getValue().userId(),
                            Collectors.mapping(Map.Entry::getKey, Collectors.toList())
                    ));

            for (Map.Entry<UUID, List<TransactionMovement>> entry : movementsByUser.entrySet()) {
                NotificationPublishRequest request = buildTransactionNotificationRequest(
                        transaction,
                        entry.getKey(),
                        entry.getValue()
                );
                if (request != null) {
                    notificationPublisherPort.publish(request);
                }
            }
        } catch (Exception exception) {
            log.warn("Unable to publish notifications for transaction {}: {}", transaction.id(), exception.getMessage());
        }
    }

    private NotificationPublishRequest buildTransactionNotificationRequest(
            Transaction transaction,
            UUID userId,
            List<TransactionMovement> userMovements
    ) {
        if (transaction == null || userId == null || userMovements == null || userMovements.isEmpty()) {
            return null;
        }

        List<Account> accounts = userMovements.stream()
                .map(movement -> getAccountOrThrow(movement.accountId(), "Account"))
                .toList();

        List<String> accountNumbers = accounts.stream()
                .map(Account::accountNumber)
                .distinct()
                .toList();

        NotificationType type = resolveNotificationType(transaction, userMovements);
        String title = resolveNotificationTitle(transaction, userMovements, accountNumbers);
        String body = resolveNotificationBody(transaction, userMovements, accountNumbers);

        ObjectNode data = objectMapper.createObjectNode();
        data.put("transactionId", transaction.id().toString());
        data.put("transactionType", transaction.type().name());
        data.put("status", transaction.status().name());
        data.put("channel", transaction.channel().name());
        data.put("amount", safeMoney(transaction.amount()).toPlainString());
        data.put("currency", transaction.currency());
        if (transaction.sourceAccountId() != null) {
            data.put("sourceAccountId", transaction.sourceAccountId().toString());
        }
        if (transaction.targetAccountId() != null) {
            data.put("targetAccountId", transaction.targetAccountId().toString());
        }
        data.putPOJO("accountNumbers", accountNumbers);
        data.putPOJO("movementTypes", userMovements.stream()
                .map(movement -> movement.movementType().name())
                .distinct()
                .toList());
        if (transaction.externalReference() != null) {
            data.put("externalReference", transaction.externalReference());
        }
        if (transaction.processedAt() != null) {
            data.put("processedAt", transaction.processedAt().toString());
        }

        return new NotificationPublishRequest(
                userId,
                type,
                NotificationCategory.TRANSACTIONS,
                resolveNotificationPriority(transaction.type()),
                title,
                body,
                data,
                null,
                buildTransactionActionUrl(transaction),
                null
        );
    }

    private NotificationType resolveNotificationType(Transaction transaction, List<TransactionMovement> movements) {
        TransactionType transactionType = transaction == null ? null : transaction.type();
        TransactionChannel transactionChannel = transaction == null ? null : transaction.channel();
        boolean hasDebit = movements.stream().anyMatch(movement -> movement.movementType() == TransactionMovementType.DEBIT);
        boolean hasCredit = movements.stream().anyMatch(movement -> movement.movementType() == TransactionMovementType.CREDIT);

        if (transactionType == null) {
            return NotificationType.SYSTEM_NOTICE;
        }

        return switch (transactionType) {
            case TRANSFER -> hasDebit && hasCredit ? NotificationType.TRANSFER_RECEIVED : NotificationType.TRANSFER_SENT;
            case DEPOSIT -> NotificationType.DEPOSIT_CONFIRMED;
            case WITHDRAWAL -> NotificationType.WITHDRAWAL_CONFIRMED;
            case PAYMENT -> transactionChannel == TransactionChannel.QR
                    ? NotificationType.QR_CONFIRMED
                    : NotificationType.PAYMENT_SUCCESS;
            case REVERSAL -> NotificationType.REVERSAL_COMPLETED;
            case REFUND -> NotificationType.REFUND_COMPLETED;
            case HOLD, RELEASE, FEE, ADJUSTMENT, SETTLEMENT -> NotificationType.SYSTEM_NOTICE;
        };
    }

    private NotificationPriority resolveNotificationPriority(TransactionType transactionType) {
        if (transactionType == null) {
            return NotificationPriority.NORMAL;
        }

        return switch (transactionType) {
            case PAYMENT, TRANSFER, REFUND, REVERSAL -> NotificationPriority.HIGH;
            case DEPOSIT, WITHDRAWAL -> NotificationPriority.NORMAL;
            case HOLD, RELEASE, FEE, ADJUSTMENT, SETTLEMENT -> NotificationPriority.LOW;
        };
    }

    private String resolveNotificationTitle(Transaction transaction, List<TransactionMovement> movements, List<String> accountNumbers) {
        TransactionType transactionType = transaction == null ? null : transaction.type();
        TransactionChannel transactionChannel = transaction == null ? null : transaction.channel();
        boolean hasDebit = movements.stream().anyMatch(movement -> movement.movementType() == TransactionMovementType.DEBIT);
        boolean hasCredit = movements.stream().anyMatch(movement -> movement.movementType() == TransactionMovementType.CREDIT);

        if (transactionType == null) {
            return "Transaction update";
        }

        return switch (transactionType) {
            case TRANSFER -> hasDebit && hasCredit ? "Transfer completed" : (hasCredit ? "Transfer received" : "Transfer sent");
            case DEPOSIT -> "Deposit confirmed";
            case WITHDRAWAL -> "Withdrawal confirmed";
            case PAYMENT -> transactionChannel == TransactionChannel.QR
                    ? "QR payment confirmed"
                    : (hasCredit ? "Payment received" : "Payment sent");
            case REVERSAL -> "Transaction reversed";
            case REFUND -> "Refund completed";
            case HOLD -> "Hold placed";
            case RELEASE -> "Hold released";
            case FEE -> "Fee charged";
            case ADJUSTMENT -> "Account adjusted";
            case SETTLEMENT -> "Settlement recorded";
        };
    }

    private String resolveNotificationBody(Transaction transaction, List<TransactionMovement> userMovements, List<String> accountNumbers) {
        String accountText = accountNumbers.isEmpty()
                ? "your account"
                : String.join(", ", accountNumbers);

        String amountText = safeMoney(transaction.amount()).toPlainString() + " " + transaction.currency();
        boolean hasDebit = userMovements.stream().anyMatch(movement -> movement.movementType() == TransactionMovementType.DEBIT);
        boolean hasCredit = userMovements.stream().anyMatch(movement -> movement.movementType() == TransactionMovementType.CREDIT);

        if (transaction.type() == TransactionType.TRANSFER && hasDebit && hasCredit) {
            return "Transfer between your accounts completed for " + amountText + ".";
        }

        return switch (transaction.type()) {
            case TRANSFER -> hasCredit
                    ? "You received " + amountText + " in " + accountText + "."
                    : "You sent " + amountText + " from " + accountText + ".";
            case DEPOSIT -> "You received " + amountText + " in " + accountText + ".";
            case WITHDRAWAL -> "You withdrew " + amountText + " from " + accountText + ".";
            case PAYMENT -> hasCredit
                    ? "You received a payment of " + amountText + " in " + accountText + "."
                    : "You paid " + amountText + " from " + accountText + ".";
            case REVERSAL -> "A transaction of " + amountText + " was reversed for " + accountText + ".";
            case REFUND -> "A refund of " + amountText + " was completed for " + accountText + ".";
            case HOLD -> "A hold of " + amountText + " was placed on " + accountText + ".";
            case RELEASE -> "A hold of " + amountText + " was released on " + accountText + ".";
            case FEE -> "A fee of " + amountText + " was charged to " + accountText + ".";
            case ADJUSTMENT -> "An adjustment of " + amountText + " was applied to " + accountText + ".";
            case SETTLEMENT -> "A settlement of " + amountText + " was recorded for " + accountText + ".";
        };
    }

    private String buildTransactionActionUrl(Transaction transaction) {
        if (transaction == null || transaction.id() == null) {
            return null;
        }

        return "/api/me/transactions/" + transaction.id();
    }

    private Map<String, Object> transactionAuditDetails(String operation, Transaction transaction, Object... extras) {
        Object[] base = new Object[] {
                "operation", operation,
                "type", transaction.type().name(),
                "status", transaction.status().name(),
                "channel", transaction.channel().name(),
                "amount", transaction.amount(),
                "currency", transaction.currency(),
                "sourceAccountId", transaction.sourceAccountId(),
                "targetAccountId", transaction.targetAccountId(),
                "externalReference", transaction.externalReference(),
                "idempotencyKey", transaction.idempotencyKey(),
                "description", transaction.description(),
                "failureReason", transaction.failureReason(),
                "requestedByUserId", transaction.requestedByUserId(),
                "approvedByUserId", transaction.approvedByUserId(),
                "processedAt", transaction.processedAt()
        };

        Object[] payload = new Object[base.length + extras.length];
        System.arraycopy(base, 0, payload, 0, base.length);
        System.arraycopy(extras, 0, payload, base.length, extras.length);
        return TenantTransactionAuditPayloads.details(payload);
    }

    private Map<String, Object> intentAuditDetails(String operation, QrTransactionIntent intent, Object... extras) {
        Object[] base = new Object[] {
                "operation", operation,
                "status", intent.status(),
                "channel", intent.channel(),
                "amount", intent.amount(),
                "currency", intent.currency(),
                "targetAccountId", intent.targetAccountId(),
                "externalReference", intent.externalReference(),
                "idempotencyKey", intent.idempotencyKey(),
                "confirmedTransactionId", intent.confirmedTransactionId(),
                "requestedByUserId", intent.requestedByUserId(),
                "confirmedAt", intent.confirmedAt()
        };

        Object[] payload = new Object[base.length + extras.length];
        System.arraycopy(base, 0, payload, 0, base.length);
        System.arraycopy(extras, 0, payload, base.length, extras.length);
        return TenantTransactionAuditPayloads.details(payload);
    }

    private UUID resolveRequestedByUserId() {
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

    private Account getAccountOrThrow(UUID accountId, String label) {
        if (accountId == null) {
            throw new BusinessException(label + " account id is required");
        }

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(label + " account not found with id: " + accountId));
    }

    private Transaction getTransactionOrThrow(UUID transactionId) {
        if (transactionId == null) {
            throw new BusinessException("Transaction id is required");
        }

        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
    }

    private QrTransactionIntent getQrIntentOrThrow(UUID intentId) {
        if (intentId == null) {
            throw new BusinessException("QR intent id is required");
        }

        return qrTransactionIntentRepository.findById(intentId)
                .orElseThrow(() -> new ResourceNotFoundException("QR intent not found with id: " + intentId));
    }

    private void ensureQrIntentBelongsToUser(QrTransactionIntent intent, UUID userId) {
        if (intent.requestedByUserId() == null || !intent.requestedByUserId().equals(userId)) {
            throw new ResourceNotFoundException("QR intent not found with id: " + intent.id());
        }
    }

    private QrTransactionIntent refreshQrIntentStatusIfExpired(QrTransactionIntent intent) {
        if (intent.status() != QrTransactionIntentStatus.PENDING) {
            return intent;
        }

        Instant expiresAt = intent.expiresAt();
        if (expiresAt == null || !expiresAt.isBefore(Instant.now())) {
            return intent;
        }

        QrTransactionIntent expired = new QrTransactionIntent(
                intent.id(),
                QrTransactionIntentStatus.EXPIRED,
                intent.channel(),
                intent.amount(),
                intent.currency(),
                intent.targetAccountId(),
                intent.externalReference(),
                intent.description(),
                intent.idempotencyKey(),
                intent.confirmedTransactionId(),
                intent.requestedByUserId(),
                intent.confirmedAt(),
                intent.expiresAt(),
                intent.cancelledAt(),
                intent.cancelledByUserId(),
                intent.payerAccountId(),
                intent.paidAmount(),
                intent.paidCurrency(),
                intent.qrPayload(),
                intent.qrSignature(),
                intent.createdAt(),
                Instant.now()
        );
        return qrTransactionIntentRepository.save(expired);
    }

    private String buildQrPayload(UUID intentId) {
        return "finance://pay?intentId=" + intentId;
    }

    private String buildInsufficientBalanceMessage(
            String subject,
            BigDecimal required,
            BigDecimal available,
            String currency
    ) {
        return subject + " has insufficient balance. Required " + safeMoney(required).toPlainString()
                + " " + currency + ", available " + safeMoney(available).toPlainString() + " " + currency;
    }

    private BigDecimal safeMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeText(String value) {
        if (value == null) {
            throw new BusinessException("Idempotency key is required");
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new BusinessException("Idempotency key is required");
        }

        return trimmed;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
