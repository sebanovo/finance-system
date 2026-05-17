package com.financesystem.finance_api.modules.tenant.accounting.infrastructure.adapter;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.fx.application.dto.TransactionFxDetailResponse;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.AccountingPeriodRepository;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.JournalEntryRepository;
import com.financesystem.finance_api.modules.tenant.transactions.application.port.AccountingPostingPort;
import com.financesystem.finance_api.modules.tenant.transactions.application.port.TransactionAccountingMovementRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.port.TransactionAccountingRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class AccountingPostingAdapter implements AccountingPostingPort {

    private final AccountingPeriodRepository accountingPeriodRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    public AccountingPostingAdapter(
            AccountingPeriodRepository accountingPeriodRepository,
            JournalEntryRepository journalEntryRepository,
            AccountRepository accountRepository,
            ObjectMapper objectMapper
    ) {
        this.accountingPeriodRepository = accountingPeriodRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.accountRepository = accountRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void postTransaction(TransactionAccountingRequest request) {
        if (request == null || request.transactionId() == null) {
            throw new BusinessException("Accounting request is required");
        }

        String entryNumber = buildEntryNumber(request.transactionId());
        if (journalEntryRepository.findAll().stream().anyMatch(entry -> entry.entryNumber().equals(entryNumber))) {
            return;
        }

        AccountingPeriod period = resolveCurrentPeriod(request.processedAt());
        UUID periodId = period != null ? period.id() : null;

        List<TransactionAccountingMovementRequest> movements = request.movements() == null
                ? List.of()
                : request.movements().stream()
                        .sorted(Comparator.comparing(TransactionAccountingMovementRequest::accountId, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList();

        TransactionFxDetailResponse fxDetail = parseFxDetail(request.metadata());

        List<JournalLine> lines = new ArrayList<>();
        int lineNo = 1;
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (TransactionAccountingMovementRequest movement : movements) {
            JournalLineType lineType = parseLineType(movement.movementType());
            BigDecimal amount = safeAmount(movement.amount());
            BigDecimal postingAmount = toPostingAmount(amount, movement.currency(), request.currency(), fxDetail);

            if (lineType == JournalLineType.DEBIT) {
                totalDebits = totalDebits.add(postingAmount);
            } else {
                totalCredits = totalCredits.add(postingAmount);
            }

            AccountOwnerView account = movement.accountId() != null
                    ? accountRepository.findViewById(movement.accountId()).orElse(null)
                    : null;
            String accountCode = account != null ? account.accountNumber() : (movement.accountId() != null ? movement.accountId().toString() : "UNKNOWN");
            String accountName = account != null ? buildAccountLabel(account) : (movement.description() != null ? movement.description() : "Unknown");

            lines.add(new JournalLine(
                    null,
                    null,
                    lineNo++,
                    accountCode,
                    accountName,
                    lineType,
                    amount,
                    normalizeCurrency(movement.currency(), request.currency()),
                    movement.description(),
                    Instant.now()
            ));
        }

        if (fxDetail != null
                && fxDetail.applied()
                && !fxDetail.feeIncludedInRate()
                && fxDetail.feeAmount() != null
                && fxDetail.feeAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal feePostingAmount = toPostingAmount(fxDetail.feeAmount(), fxDetail.feeCurrency().name(), request.currency(), fxDetail);
            totalCredits = totalCredits.add(feePostingAmount);
            lines.add(new JournalLine(
                    null,
                    null,
                    lineNo++,
                    "SYSTEM:FX_FEE",
                    "FX fee",
                    JournalLineType.CREDIT,
                    safeAmount(fxDetail.feeAmount()),
                    normalizeCurrencyCode(fxDetail.feeCurrency(), request.currency()),
                    buildFxFeeDescription(request.transactionType(), fxDetail),
                    Instant.now()
            ));
        }

        if (lines.isEmpty()) {
            return;
        }

        if (totalDebits.compareTo(totalCredits) != 0) {
            addBalancingLine(request, lines, totalDebits, totalCredits);
            if (totalDebits.compareTo(totalCredits) > 0) {
                totalCredits = totalDebits;
            } else {
                totalDebits = totalCredits;
            }
        }

        if (totalDebits.compareTo(totalCredits) != 0) {
            throw new BusinessException("Accounting entry must be balanced before posting");
        }

        if (request.transactionType() == null || request.transactionType().isBlank()) {
            throw new BusinessException("Transaction type is required for accounting posting");
        }

        JournalEntry journalEntry = new JournalEntry(
                null,
                entryNumber,
                request.transactionId(),
                periodId,
                JournalEntryType.valueOf(request.transactionType().toUpperCase(Locale.ROOT)),
                JournalEntryStatus.POSTED,
                request.description(),
                request.externalReference(),
                totalDebits,
                totalCredits,
                request.processedAt() != null ? request.processedAt() : Instant.now(),
                null,
                null,
                lines
        );

        journalEntryRepository.save(journalEntry);
    }

    private AccountingPeriod resolveCurrentPeriod(Instant postedAt) {
        LocalDate date = postedAt != null ? LocalDate.ofInstant(postedAt, java.time.ZoneOffset.UTC) : LocalDate.now();
        return accountingPeriodRepository.findAll().stream()
                .filter(period -> !date.isBefore(period.startDate()) && !date.isAfter(period.endDate()))
                .findFirst()
                .orElse(null);
    }

    private String buildEntryNumber(UUID transactionId) {
        return "JE-" + transactionId.toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
    }

    private JournalLineType parseLineType(String movementType) {
        if (movementType == null || movementType.isBlank()) {
            return JournalLineType.DEBIT;
        }

        return switch (movementType.trim().toUpperCase(Locale.ROOT)) {
            case "DEBIT" -> JournalLineType.DEBIT;
            case "CREDIT" -> JournalLineType.CREDIT;
            default -> JournalLineType.DEBIT;
        };
    }

    private String normalizeCurrency(String candidate, String fallback) {
        String value = candidate != null && !candidate.isBlank() ? candidate : fallback;
        return value == null ? "BOB" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeCurrencyCode(CurrencyCode candidate, String fallback) {
        return candidate != null ? candidate.name() : normalizeCurrency((String) null, fallback);
    }

    private String buildAccountLabel(AccountOwnerView account) {
        if (account.customAlias() != null && !account.customAlias().isBlank()) {
            return account.customAlias().trim();
        }

        return account.accountName() != null ? account.accountName().name() : account.accountNumber();
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void addBalancingLine(
            TransactionAccountingRequest request,
            List<JournalLine> lines,
            BigDecimal totalDebits,
            BigDecimal totalCredits
    ) {
        if (totalDebits.compareTo(totalCredits) > 0) {
            lines.add(new JournalLine(
                    null,
                    null,
                    lines.size() + 1,
                    "SYSTEM:EXTERNAL_CLEARING",
                    "External clearing",
                    JournalLineType.CREDIT,
                    totalDebits.subtract(totalCredits),
                    normalizeCurrency(null, request.currency()),
                    buildBalancingDescription(request.transactionType(), "CREDIT"),
                    Instant.now()
            ));
            return;
        }

        if (totalCredits.compareTo(totalDebits) > 0) {
            lines.add(new JournalLine(
                    null,
                    null,
                    lines.size() + 1,
                    "SYSTEM:EXTERNAL_CLEARING",
                    "External clearing",
                    JournalLineType.DEBIT,
                    totalCredits.subtract(totalDebits),
                    normalizeCurrency(null, request.currency()),
                    buildBalancingDescription(request.transactionType(), "DEBIT"),
                    Instant.now()
            ));
        }
    }

    private String buildBalancingDescription(String transactionType, String balancingSide) {
        String type = transactionType == null || transactionType.isBlank()
                ? "transaction"
                : transactionType.trim().toLowerCase(Locale.ROOT);
        return "Balancing line for " + type + " (" + balancingSide + ")";
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

    private BigDecimal toPostingAmount(
            BigDecimal amount,
            String movementCurrency,
            String baseCurrency,
            TransactionFxDetailResponse fxDetail
    ) {
        BigDecimal safeAmount = safeAmount(amount);
        String normalizedMovementCurrency = normalizeCurrency(movementCurrency, baseCurrency);
        String normalizedBaseCurrency = normalizeCurrency(baseCurrency, baseCurrency);

        if (fxDetail == null || !fxDetail.applied()) {
            return safeAmount;
        }

        if (normalizedMovementCurrency.equals(normalizedBaseCurrency)) {
            return safeAmount;
        }

        if (normalizedMovementCurrency.equals(normalizeCurrencyCode(fxDetail.targetCurrency(), baseCurrency))) {
            BigDecimal rate = fxDetail.feeIncludedInRate() && fxDetail.effectiveExchangeRate() != null
                    ? fxDetail.effectiveExchangeRate()
                    : fxDetail.exchangeRate();
            return safeAmount.divide(rate, 4, java.math.RoundingMode.HALF_UP);
        }

        return safeAmount;
    }

    private String buildFxFeeDescription(String transactionType, TransactionFxDetailResponse fxDetail) {
        String type = transactionType == null || transactionType.isBlank()
                ? "transaction"
                : transactionType.trim().toLowerCase(Locale.ROOT);

        if (fxDetail == null || fxDetail.feeAmount() == null || fxDetail.feeAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "FX fee for " + type;
        }

        return "FX fee for " + type + " (" + fxDetail.feeAmount() + " " + fxDetail.feeCurrency().name() + ")";
    }
}
