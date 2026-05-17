package com.financesystem.finance_api.modules.tenant.accounting.application.usecase.period;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.AccountingPeriodResponse;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.CloseAccountingPeriodRequest;
import com.financesystem.finance_api.modules.tenant.accounting.application.mapper.AccountingMapper;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriod;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodStatus;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.AccountingPeriodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CloseAccountingPeriodUseCase {

    private final AccountingPeriodRepository accountingPeriodRepository;
    private final AccountingMapper accountingMapper;

    public CloseAccountingPeriodUseCase(
            AccountingPeriodRepository accountingPeriodRepository,
            AccountingMapper accountingMapper
    ) {
        this.accountingPeriodRepository = accountingPeriodRepository;
        this.accountingMapper = accountingMapper;
    }

    @Transactional
    public AccountingPeriodResponse execute(UUID id, CloseAccountingPeriodRequest request) {
        AccountingPeriod existing = accountingPeriodRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Accounting period not found with id: " + id));

        if (existing.status() == AccountingPeriodStatus.CLOSED) {
            throw new BusinessException("Accounting period is already closed");
        }

        AccountingPeriod closed = new AccountingPeriod(
                existing.id(),
                existing.periodCode(),
                existing.periodType(),
                AccountingPeriodStatus.CLOSED,
                existing.startDate(),
                existing.endDate(),
                Instant.now(),
                request.reason() != null && !request.reason().isBlank() ? request.reason().trim() : existing.description(),
                existing.createdAt(),
                existing.updatedAt()
        );

        return accountingMapper.toResponse(accountingPeriodRepository.save(closed));
    }
}
