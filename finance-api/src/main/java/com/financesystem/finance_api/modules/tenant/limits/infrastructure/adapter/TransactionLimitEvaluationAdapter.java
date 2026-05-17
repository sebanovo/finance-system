package com.financesystem.finance_api.modules.tenant.limits.infrastructure.adapter;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.limits.application.dto.*;
import com.financesystem.finance_api.modules.tenant.limits.application.service.LimitPolicyService;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.*;
import com.financesystem.finance_api.modules.tenant.limits.domain.repository.LimitRuleRepository;
import com.financesystem.finance_api.modules.tenant.limits.domain.repository.LimitUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class TransactionLimitEvaluationAdapter implements LimitPolicyService {

    private static final DateTimeFormatter DAY_KEY_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter MONTH_KEY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final LimitRuleRepository limitRuleRepository;
    private final LimitUsageRepository limitUsageRepository;

    public TransactionLimitEvaluationAdapter(
            LimitRuleRepository limitRuleRepository,
            LimitUsageRepository limitUsageRepository
    ) {
        this.limitRuleRepository = limitRuleRepository;
        this.limitUsageRepository = limitUsageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public LimitEvaluationResponse evaluate(LimitEvaluationRequest request) {
        return evaluateInternal(request);
    }

    @Override
    @Transactional
    public void registerUsage(LimitEvaluationRequest request, List<UUID> ruleIds) {
        if (ruleIds == null || ruleIds.isEmpty()) {
            return;
        }

        for (UUID ruleId : new LinkedHashSet<>(ruleIds)) {
            LimitRule rule = limitRuleRepository.findById(ruleId).orElse(null);
            if (rule == null || !rule.active()) {
                continue;
            }

            String scopeKey = resolveScopeKey(rule, request);
            String periodKey = resolvePeriodKey(rule.period(), request);

            LimitUsage currentUsage = limitUsageRepository.findByRuleIdAndScopeKeyAndPeriodKey(ruleId, scopeKey, periodKey)
                    .orElse(new LimitUsage(null, ruleId, scopeKey, periodKey, 0, BigDecimal.ZERO, null, null, null));

            LimitUsage updated = new LimitUsage(
                    currentUsage.id(),
                    ruleId,
                    scopeKey,
                    periodKey,
                    currentUsage.transactionCount() + 1L,
                    safeAmount(currentUsage.totalAmount()).add(safeAmount(request.amount())),
                    java.time.Instant.now(),
                    currentUsage.createdAt(),
                    currentUsage.updatedAt()
            );

            limitUsageRepository.save(updated);
        }
    }

    private LimitEvaluationResponse evaluateInternal(LimitEvaluationRequest request) {
        List<LimitEvaluationRuleCheckResponse> checks = new ArrayList<>();
        boolean allowed = true;
        boolean requiresReview = false;
        String reason = "No limit rule matched";

        for (LimitRule rule : limitRuleRepository.findAllActive()) {
            if (!matches(rule, request)) {
                continue;
            }

            LimitUsage usage = findUsage(rule, request);
            LimitEvaluationRuleCheckResponse check = evaluateRule(rule, request, usage);
            checks.add(check);

            if (!check.allowed()) {
                allowed = false;
                if (check.requiresReview()) {
                    requiresReview = true;
                }
                if (StringUtils.hasText(check.reason())) {
                    reason = check.reason();
                }
            }
        }

        if (checks.isEmpty()) {
            return new LimitEvaluationResponse(true, false, reason,
                    request.transactionType(),
                    request.amount(),
                    request.currency(),
                    checks);
        }

        if (allowed) {
            reason = "Allowed";
        }

        return new LimitEvaluationResponse(allowed, requiresReview, reason,
                request.transactionType(),
                request.amount(),
                request.currency(),
                checks);
    }

    private LimitEvaluationRuleCheckResponse evaluateRule(
            LimitRule rule,
            LimitEvaluationRequest request,
            LimitUsage usage
    ) {
        BigDecimal amount = safeAmount(request.amount());
        BigDecimal currentAmount = usage != null ? safeAmount(usage.totalAmount()) : BigDecimal.ZERO;
        Long currentCount = usage != null ? usage.transactionCount() : 0L;
        BigDecimal maxAmount = rule.maxAmount();
        Long maxCount = rule.maxCount();
        boolean allowed = true;
        boolean requiresReview = false;
        String reason = "Allowed";
        BigDecimal remainingAmount = null;
        Long remainingCount = null;

        switch (rule.limitType()) {
            case PER_TRANSACTION_AMOUNT -> {
                if (maxAmount != null) {
                    allowed = amount.compareTo(maxAmount) <= 0;
                    remainingAmount = maxAmount.subtract(amount).max(BigDecimal.ZERO);
                    if (!allowed) {
                        reason = "Amount exceeds the per transaction limit";
                    }
                }
            }
            case DAILY_AMOUNT, MONTHLY_AMOUNT -> {
                if (maxAmount != null) {
                    BigDecimal projected = currentAmount.add(amount);
                    allowed = projected.compareTo(maxAmount) <= 0;
                    remainingAmount = maxAmount.subtract(projected).max(BigDecimal.ZERO);
                    if (!allowed) {
                        reason = "Amount exceeds the configured aggregated limit";
                    }
                }
            }
            case DAILY_COUNT, MONTHLY_COUNT -> {
                if (maxCount != null) {
                    long projected = currentCount + 1L;
                    allowed = projected <= maxCount;
                    remainingCount = Math.max(maxCount - projected, 0L);
                    if (!allowed) {
                        reason = "Transaction count exceeds the configured limit";
                    }
                }
            }
            case MIN_AMOUNT -> {
                if (rule.minAmount() != null) {
                    allowed = amount.compareTo(rule.minAmount()) >= 0;
                    if (!allowed) {
                        reason = "Amount is below the minimum allowed limit";
                    }
                }
            }
            case MAX_BALANCE -> {
                BigDecimal referenceBalance = resolveReferenceBalance(rule, request);
                if (maxAmount != null && referenceBalance != null) {
                    BigDecimal projected = referenceBalance.add(amount);
                    allowed = projected.compareTo(maxAmount) <= 0;
                    remainingAmount = maxAmount.subtract(projected).max(BigDecimal.ZERO);
                    if (!allowed) {
                        reason = "Projected balance exceeds the allowed maximum";
                    }
                }
            }
        }

        if (!allowed && rule.requireReviewExceed()) {
            requiresReview = true;
        }

        return new LimitEvaluationRuleCheckResponse(
                rule.id(),
                rule.code(),
                rule.name(),
                rule.limitType(),
                rule.scopeType(),
                rule.period(),
                true,
                allowed,
                requiresReview,
                reason,
                currentAmount,
                currentCount,
                remainingAmount,
                remainingCount,
                maxAmount,
                maxCount
        );
    }

    private boolean matches(LimitRule rule, LimitEvaluationRequest request) {
        if (!rule.active()) {
            return false;
        }

        if (rule.currency() != null && request.currency() != rule.currency()) {
            return false;
        }

        if (rule.transactionType() != null && request.transactionType() != rule.transactionType()) {
            return false;
        }

        if (rule.accountType() != null) {
            boolean matchesSource = request.sourceAccountType() != null
                    && rule.accountType() == request.sourceAccountType();
            boolean matchesTarget = request.targetAccountType() != null
                    && rule.accountType() == request.targetAccountType();
            if (!matchesSource && !matchesTarget) {
                return false;
            }
        }

        return switch (rule.scopeType()) {
            case TENANT -> true;
            case TRANSACTION_TYPE -> request.transactionType() != null;
            case ACCOUNT_TYPE -> request.sourceAccountType() != null || request.targetAccountType() != null;
            case USER -> request.actorUserId() != null;
            case ACCOUNT -> request.sourceAccountId() != null || request.targetAccountId() != null;
        };
    }

    private LimitUsage findUsage(LimitRule rule, LimitEvaluationRequest request) {
        String scopeKey = resolveScopeKey(rule, request);
        String periodKey = resolvePeriodKey(rule.period(), request);

        return limitUsageRepository.findByRuleIdAndScopeKeyAndPeriodKey(rule.id(), scopeKey, periodKey).orElse(null);
    }

    private String resolveScopeKey(LimitRule rule, LimitEvaluationRequest request) {
        return switch (rule.scopeType()) {
            case TENANT -> "TENANT";
            case TRANSACTION_TYPE -> "TRANSACTION_TYPE:" + normalizeKey(request.transactionType() != null ? request.transactionType().name() : null);
            case ACCOUNT_TYPE -> "ACCOUNT_TYPE:" + normalizeKey(firstText(
                    request.sourceAccountType() != null ? request.sourceAccountType().name() : null,
                    request.targetAccountType() != null ? request.targetAccountType().name() : null
            ));
            case USER -> "USER:" + normalizeKey(request.actorUserId() != null ? request.actorUserId().toString() : null);
            case ACCOUNT -> "ACCOUNT:" + normalizeKey(firstText(
                    request.sourceAccountId() != null ? request.sourceAccountId().toString() : null,
                    request.targetAccountId() != null ? request.targetAccountId().toString() : null
            ));
        };
    }

    private String resolvePeriodKey(LimitPeriod period, LimitEvaluationRequest request) {
        return switch (period) {
            case TRANSACTION -> "TX";
            case DAILY -> "DAY:" + DAY_KEY_FORMAT.format(LocalDate.now());
            case MONTHLY -> "MONTH:" + MONTH_KEY_FORMAT.format(YearMonth.now());
        };
    }

    private BigDecimal resolveReferenceBalance(LimitRule rule, LimitEvaluationRequest request) {
        if (request.targetAvailableBalance() != null) {
            return safeAmount(request.targetAvailableBalance());
        }

        if (request.sourceAvailableBalance() != null) {
            return safeAmount(request.sourceAvailableBalance());
        }

        return null;
    }

    private String firstText(String first, String second) {
        if (StringUtils.hasText(first)) {
            return first;
        }
        return second;
    }

    private String normalizeKey(String value) {
        if (!StringUtils.hasText(value)) {
            return "UNSET";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private BigDecimal safeAmount(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(4, RoundingMode.HALF_UP);
    }
}
