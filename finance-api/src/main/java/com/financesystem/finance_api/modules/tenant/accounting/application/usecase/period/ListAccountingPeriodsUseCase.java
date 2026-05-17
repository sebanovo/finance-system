package com.financesystem.finance_api.modules.tenant.accounting.application.usecase.period;

import com.financesystem.finance_api.modules.tenant.accounting.application.dto.AccountingPeriodResponse;
import com.financesystem.finance_api.modules.tenant.accounting.application.mapper.AccountingMapper;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.AccountingPeriodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAccountingPeriodsUseCase {

    private final AccountingPeriodRepository accountingPeriodRepository;
    private final AccountingMapper accountingMapper;

    public ListAccountingPeriodsUseCase(
            AccountingPeriodRepository accountingPeriodRepository,
            AccountingMapper accountingMapper
    ) {
        this.accountingPeriodRepository = accountingPeriodRepository;
        this.accountingMapper = accountingMapper;
    }

    @Transactional(readOnly = true)
    public List<AccountingPeriodResponse> execute() {
        return accountingPeriodRepository.findAll()
                .stream()
                .map(accountingMapper::toResponse)
                .toList();
    }
}
