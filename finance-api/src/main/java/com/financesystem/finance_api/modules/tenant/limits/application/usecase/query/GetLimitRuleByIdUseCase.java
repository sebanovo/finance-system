package com.financesystem.finance_api.modules.tenant.limits.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitRuleResponse;
import com.financesystem.finance_api.modules.tenant.limits.application.mapper.LimitRuleMapper;
import com.financesystem.finance_api.modules.tenant.limits.domain.exception.LimitRuleNotFoundException;
import com.financesystem.finance_api.modules.tenant.limits.domain.repository.LimitRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetLimitRuleByIdUseCase {

    private final LimitRuleRepository limitRuleRepository;
    private final LimitRuleMapper limitRuleMapper;

    public GetLimitRuleByIdUseCase(
            LimitRuleRepository limitRuleRepository,
            LimitRuleMapper limitRuleMapper
    ) {
        this.limitRuleRepository = limitRuleRepository;
        this.limitRuleMapper = limitRuleMapper;
    }

    @Transactional(readOnly = true)
    public LimitRuleResponse execute(UUID id) {
        return limitRuleRepository.findById(id)
                .map(limitRuleMapper::toResponse)
                .orElseThrow(() -> new LimitRuleNotFoundException("Limit rule not found with id: " + id));
    }
}
