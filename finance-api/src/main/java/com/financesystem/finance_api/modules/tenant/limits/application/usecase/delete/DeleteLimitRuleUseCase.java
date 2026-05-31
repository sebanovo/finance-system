package com.financesystem.finance_api.modules.tenant.limits.application.usecase.delete;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitRuleResponse;
import com.financesystem.finance_api.modules.tenant.limits.application.mapper.LimitRuleMapper;
import com.financesystem.finance_api.modules.tenant.audit.TenantAuditPayloads;
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
    private final AuditTrailService auditTrailService;

    public DeleteLimitRuleUseCase(
            LimitRuleRepository limitRuleRepository,
            LimitRuleMapper limitRuleMapper,
            AuditTrailService auditTrailService
    ) {
        this.limitRuleRepository = limitRuleRepository;
        this.limitRuleMapper = limitRuleMapper;
        this.auditTrailService = auditTrailService;
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

        LimitRule saved = limitRuleRepository.save(deactivated);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.LIMIT_RULE_DEACTIVATED,
                "LIMIT_RULE",
                saved.id().toString(),
                TenantAuditPayloads.details(
                        "code", saved.code(),
                        "activeFrom", existing.active(),
                        "activeTo", saved.active(),
                        "reason", "Soft deactivated"
                ),
                TenantAuditPayloads.limitRuleState(existing),
                TenantAuditPayloads.limitRuleState(saved)
        );

        return limitRuleMapper.toResponse(saved);
    }
}
