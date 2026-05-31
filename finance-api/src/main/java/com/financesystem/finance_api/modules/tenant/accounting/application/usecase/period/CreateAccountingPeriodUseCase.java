package com.financesystem.finance_api.modules.tenant.accounting.application.usecase.period;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.AccountingPeriodResponse;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.CreateAccountingPeriodRequest;
import com.financesystem.finance_api.modules.tenant.accounting.application.mapper.AccountingMapper;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriod;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodStatus;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.AccountingPeriodRepository;
import com.financesystem.finance_api.modules.tenant.audit.TenantAuditPayloads;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class CreateAccountingPeriodUseCase {

    private final AccountingPeriodRepository accountingPeriodRepository;
    private final AccountingMapper accountingMapper;
    private final AuditTrailService auditTrailService;

    public CreateAccountingPeriodUseCase(
            AccountingPeriodRepository accountingPeriodRepository,
            AccountingMapper accountingMapper,
            AuditTrailService auditTrailService
    ) {
        this.accountingPeriodRepository = accountingPeriodRepository;
        this.accountingMapper = accountingMapper;
        this.auditTrailService = auditTrailService;
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

        AccountingPeriod saved = accountingPeriodRepository.save(period);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ACCOUNTING_PERIOD_CREATED,
                "ACCOUNTING_PERIOD",
                saved.id().toString(),
                TenantAuditPayloads.details(
                        "periodCode", saved.periodCode(),
                        "periodType", saved.periodType(),
                        "status", saved.status(),
                        "startDate", saved.startDate(),
                        "endDate", saved.endDate()
                ),
                null,
                TenantAuditPayloads.accountingPeriodState(saved)
        );

        return accountingMapper.toResponse(saved);
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
