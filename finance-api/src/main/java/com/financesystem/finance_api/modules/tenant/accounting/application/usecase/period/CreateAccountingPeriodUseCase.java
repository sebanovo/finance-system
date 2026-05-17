package com.financesystem.finance_api.modules.tenant.accounting.application.usecase.period;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.AccountingPeriodResponse;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.CreateAccountingPeriodRequest;
import com.financesystem.finance_api.modules.tenant.accounting.application.mapper.AccountingMapper;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriod;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodStatus;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.AccountingPeriodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class CreateAccountingPeriodUseCase {

    private final AccountingPeriodRepository accountingPeriodRepository;
    private final AccountingMapper accountingMapper;

    public CreateAccountingPeriodUseCase(
            AccountingPeriodRepository accountingPeriodRepository,
            AccountingMapper accountingMapper
    ) {
        this.accountingPeriodRepository = accountingPeriodRepository;
        this.accountingMapper = accountingMapper;
    }

    @Transactional
    public AccountingPeriodResponse execute(CreateAccountingPeriodRequest request) {
        String periodCode = normalizeCode(request.periodCode());

        if (request.endDate().isBefore(request.startDate())) {
            throw new BusinessException("End date cannot be before start date");
        }

        accountingPeriodRepository.findByPeriodCode(periodCode).ifPresent(existing -> {
            throw new BusinessException("Accounting period with code '" + periodCode + "' already exists");
        });

        AccountingPeriod period = new AccountingPeriod(
                null,
                periodCode,
                request.periodType(),
                AccountingPeriodStatus.OPEN,
                request.startDate(),
                request.endDate(),
                null,
                normalizeNullable(request.description()),
                null,
                null
        );

        return accountingMapper.toResponse(accountingPeriodRepository.save(period));
    }

    private String normalizeCode(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException("Period code is required");
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
