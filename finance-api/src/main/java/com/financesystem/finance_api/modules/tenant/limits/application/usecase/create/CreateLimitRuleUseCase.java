package com.financesystem.finance_api.modules.tenant.limits.application.usecase.create;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.tenant.limits.application.dto.CreateLimitRuleRequest;
import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitRuleResponse;
import com.financesystem.finance_api.modules.tenant.limits.application.mapper.LimitRuleMapper;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.*;
import com.financesystem.finance_api.modules.tenant.limits.domain.repository.LimitRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class CreateLimitRuleUseCase {

    private final LimitRuleRepository limitRuleRepository;
    private final LimitRuleMapper limitRuleMapper;

    public CreateLimitRuleUseCase(
            LimitRuleRepository limitRuleRepository,
            LimitRuleMapper limitRuleMapper
    ) {
        this.limitRuleRepository = limitRuleRepository;
        this.limitRuleMapper = limitRuleMapper;
    }

    @Transactional
    public LimitRuleResponse execute(CreateLimitRuleRequest request) {
        String code = normalizeCode(request.code());

        limitRuleRepository.findByCode(code).ifPresent(existing -> {
            throw new BusinessException("Limit rule with code '" + code + "' already exists");
        });

        LimitRule limitRule = new LimitRule(
                null,
                code,
                normalizeText(request.name()),
                normalizeNullable(request.description()),
                request.limitType(),
                request.scopeType(),
                request.period(),
                request.transactionType(),
                request.accountType(),
                request.currency(),
                request.minAmount(),
                request.maxAmount(),
                normalizeMaxCount(request.maxCount(), request.limitType()),
                request.active(),
                request.requireReviewExceed(),
                null,
                null,
                null,
                null
        );

        validateRuleConfiguration(limitRule);
        validateScopeConfiguration(limitRule);
        validateThresholds(limitRule);

        return limitRuleMapper.toResponse(limitRuleRepository.save(limitRule));
    }

    private String normalizeCode(String value) {
        String normalized = normalizeText(value).toLowerCase(Locale.ROOT);

        if (normalized.isBlank()) {
            throw new BusinessException("Limit rule code is required");
        }

        return normalized;
    }

    private String normalizeText(String value) {
        if (value == null) {
            throw new BusinessException("Required field is missing");
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new BusinessException("Required field is missing");
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

    private Long normalizeMaxCount(Long maxCount, LimitRuleType limitType) {
        if (isCountBased(limitType) && maxCount == null) {
            throw new BusinessException("Field 'maxCount' is required for count-based limits");
        }

        return maxCount;
    }

    private boolean isCountBased(LimitRuleType limitType) {
        return limitType == LimitRuleType.DAILY_COUNT || limitType == LimitRuleType.MONTHLY_COUNT;
    }

    private void validateThresholds(LimitRule limitRule) {
        switch (limitRule.limitType()) {
            case PER_TRANSACTION_AMOUNT, DAILY_AMOUNT, MONTHLY_AMOUNT, MAX_BALANCE -> {
                if (limitRule.maxAmount() == null) {
                    throw new BusinessException("Field 'maxAmount' is required for amount-based limits");
                }
            }
            case MIN_AMOUNT -> {
                if (limitRule.minAmount() == null) {
                    throw new BusinessException("Field 'minAmount' is required for minimum amount limits");
                }
            }
            case DAILY_COUNT, MONTHLY_COUNT -> {
                if (limitRule.maxCount() == null) {
                    throw new BusinessException("Field 'maxCount' is required for count-based limits");
                }
            }
        }
    }

    private void validateRuleConfiguration(LimitRule limitRule) {
        if ((limitRule.limitType() == LimitRuleType.PER_TRANSACTION_AMOUNT
                || limitRule.limitType() == LimitRuleType.MIN_AMOUNT
                || limitRule.limitType() == LimitRuleType.MAX_BALANCE)
                && limitRule.period() != LimitPeriod.TRANSACTION) {
            throw new BusinessException("This limit type must use TRANSACTION period");
        }

        if (limitRule.limitType() == LimitRuleType.DAILY_AMOUNT || limitRule.limitType() == LimitRuleType.DAILY_COUNT) {
            if (limitRule.period() != LimitPeriod.DAILY) {
                throw new BusinessException("Daily limits must use DAILY period");
            }
        }

        if (limitRule.limitType() == LimitRuleType.MONTHLY_AMOUNT || limitRule.limitType() == LimitRuleType.MONTHLY_COUNT) {
            if (limitRule.period() != LimitPeriod.MONTHLY) {
                throw new BusinessException("Monthly limits must use MONTHLY period");
            }
        }
    }

    private void validateScopeConfiguration(LimitRule limitRule) {
        if (limitRule.scopeType() == LimitScopeType.TRANSACTION_TYPE) {
            if (limitRule.transactionType() == null) {
                throw new BusinessException("Field 'transactionType' is required for TRANSACTION_TYPE scope rules");
            }
        } else if (limitRule.transactionType() != null) {
            throw new BusinessException("Field 'transactionType' is only allowed for TRANSACTION_TYPE scope rules");
        }

        if (limitRule.scopeType() == LimitScopeType.ACCOUNT_TYPE) {
            if (limitRule.accountType() == null) {
                throw new BusinessException("Field 'accountType' is required for ACCOUNT_TYPE scope rules");
            }
        } else if (limitRule.accountType() != null) {
            throw new BusinessException("Field 'accountType' is only allowed for ACCOUNT_TYPE scope rules");
        }
    }
}
