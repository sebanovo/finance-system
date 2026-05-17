package com.financesystem.finance_api.modules.tenant.limits.application.usecase.delete;

import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitRuleResponse;
import com.financesystem.finance_api.modules.tenant.limits.application.mapper.LimitRuleMapper;
import com.financesystem.finance_api.modules.tenant.limits.domain.exception.LimitRuleNotFoundException;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRule;
import com.financesystem.finance_api.modules.tenant.limits.domain.repository.LimitRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteLimitRuleUseCase {

    private final LimitRuleRepository limitRuleRepository;
    private final LimitRuleMapper limitRuleMapper;

    public DeleteLimitRuleUseCase(
            LimitRuleRepository limitRuleRepository,
            LimitRuleMapper limitRuleMapper
    ) {
        this.limitRuleRepository = limitRuleRepository;
        this.limitRuleMapper = limitRuleMapper;
    }

    @Transactional
    public LimitRuleResponse execute(UUID id) {
        LimitRule existing = limitRuleRepository.findById(id)
                .orElseThrow(() -> new LimitRuleNotFoundException("Limit rule not found with id: " + id));

        LimitRule deactivated = new LimitRule(
                existing.id(),
                existing.code(),
                existing.name(),
                existing.description(),
                existing.limitType(),
                existing.scopeType(),
                existing.period(),
                existing.transactionType(),
                existing.accountType(),
                existing.currency(),
                existing.minAmount(),
                existing.maxAmount(),
                existing.maxCount(),
                false,
                existing.requireReviewExceed(),
                existing.createdByUserId(),
                existing.updatedByUserId(),
                existing.createdAt(),
                existing.updatedAt()
        );

        return limitRuleMapper.toResponse(limitRuleRepository.save(deactivated));
    }
}
