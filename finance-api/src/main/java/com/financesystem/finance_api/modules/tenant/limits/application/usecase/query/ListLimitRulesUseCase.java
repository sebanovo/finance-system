package com.financesystem.finance_api.modules.tenant.limits.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.limits.application.dto.LimitRuleResponse;
import com.financesystem.finance_api.modules.tenant.limits.application.mapper.LimitRuleMapper;
import com.financesystem.finance_api.modules.tenant.limits.domain.repository.LimitRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListLimitRulesUseCase {

    private final LimitRuleRepository limitRuleRepository;
    private final LimitRuleMapper limitRuleMapper;

    public ListLimitRulesUseCase(
            LimitRuleRepository limitRuleRepository,
            LimitRuleMapper limitRuleMapper
    ) {
        this.limitRuleRepository = limitRuleRepository;
        this.limitRuleMapper = limitRuleMapper;
    }

    @Transactional(readOnly = true)
    public List<LimitRuleResponse> execute() {
        return limitRuleRepository.findAll()
                .stream()
                .map(limitRuleMapper::toResponse)
                .toList();
    }
}
