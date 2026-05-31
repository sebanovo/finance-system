package com.financesystem.finance_api.modules.tenant.accounting.application.usecase.period;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.AccountingPeriodResponse;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.CloseAccountingPeriodRequest;
import com.financesystem.finance_api.modules.tenant.accounting.application.mapper.AccountingMapper;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriod;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.AccountingPeriodStatus;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.AccountingPeriodRepository;
import com.financesystem.finance_api.modules.tenant.audit.TenantAuditPayloads;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CloseAccountingPeriodUseCase {

    private final AccountingPeriodRepository accountingPeriodRepository;
    private final AccountingMapper accountingMapper;
    private final AuditTrailService auditTrailService;

    public CloseAccountingPeriodUseCase(
            AccountingPeriodRepository accountingPeriodRepository,
            AccountingMapper accountingMapper,
            AuditTrailService auditTrailService
    ) {
        this.accountingPeriodRepository = accountingPeriodRepository;
        this.accountingMapper = accountingMapper;
        this.auditTrailService = auditTrailService;
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

        AccountingPeriod saved = accountingPeriodRepository.save(closed);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ACCOUNTING_PERIOD_CLOSED,
                "ACCOUNTING_PERIOD",
                saved.id().toString(),
                TenantAuditPayloads.details(
                        "periodCode", saved.periodCode(),
                        "periodType", saved.periodType(),
                        "status", saved.status(),
                        "closedAt", saved.closedAt(),
                        "reason", saved.description()
                ),
                TenantAuditPayloads.accountingPeriodState(existing),
                TenantAuditPayloads.accountingPeriodState(saved)
        );

        return accountingMapper.toResponse(saved);
    }
}
