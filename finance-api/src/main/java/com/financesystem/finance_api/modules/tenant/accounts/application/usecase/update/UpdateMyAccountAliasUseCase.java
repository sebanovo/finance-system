package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.update;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.UpdateAccountAliasRequest;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.accounts.application.service.CurrentTenantAccountUserService;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.AccountNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class UpdateMyAccountAliasUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CurrentTenantAccountUserService currentTenantAccountUserService;
    private final AuditTrailService auditTrailService;

    public UpdateMyAccountAliasUseCase(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            CurrentTenantAccountUserService currentTenantAccountUserService,
            AuditTrailService auditTrailService
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.currentTenantAccountUserService = currentTenantAccountUserService;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public AccountOwnerResponse execute(UUID accountId, UpdateAccountAliasRequest request) {
        UUID currentUserId = currentTenantAccountUserService.getCurrentUserId();

        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId
                ));

        if (!existingAccount.userId().equals(currentUserId)) {
            throw new AccountNotFoundException("Account not found with id: " + accountId);
        }

        Account updatedAccount = new Account(
                existingAccount.id(),
                existingAccount.userId(),
                existingAccount.accountNumber(),
                existingAccount.accountName(),
                request.customAlias() != null
                        ? normalizeNullable(request.customAlias())
                        : existingAccount.customAlias(),
                existingAccount.accountType(),
                existingAccount.currency(),
                existingAccount.availableBalance(),
                existingAccount.heldBalance(),
                existingAccount.status(),
                existingAccount.statusReason(),
                existingAccount.active(),
                existingAccount.primary(),
                existingAccount.openedAt(),
                existingAccount.closedAt(),
                existingAccount.createdAt(),
                existingAccount.updatedAt()
        );

        Account savedAccount = accountRepository.save(updatedAccount);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ACCOUNT_ALIAS_UPDATED,
                "ACCOUNT",
                savedAccount.id().toString(),
                Map.of(
                        "accountNumber", savedAccount.accountNumber(),
                        "accountName", savedAccount.accountName(),
                        "customAlias", savedAccount.customAlias() != null ? savedAccount.customAlias(): ""
                )
        );

        AccountOwnerView view = accountRepository.findViewById(savedAccount.id())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account view not found with id: " + savedAccount.id()
                ));

        return accountMapper.toOwnerResponse(view);
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
