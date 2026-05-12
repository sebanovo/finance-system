package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.lifecycle;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.AccountNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.InvalidAccountStatusException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class FreezeAccountUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AuditTrailService auditTrailService;

    public FreezeAccountUseCase(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            AuditTrailService auditTrailService
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public AccountOwnerResponse execute(UUID accountId, String reason) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId
                ));

        if (existingAccount.status() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException(
                    "Closed accounts cannot be frozen"
            );
        }

        Account updatedAccount = new Account(
                existingAccount.id(),
                existingAccount.userId(),
                existingAccount.accountNumber(),
                existingAccount.accountName(),
                existingAccount.customAlias(),
                existingAccount.accountType(),
                existingAccount.currency(),
                existingAccount.availableBalance(),
                existingAccount.heldBalance(),
                AccountStatus.FROZEN,
                normalizeReason(reason),
                false,
                existingAccount.primary(),
                existingAccount.openedAt(),
                existingAccount.closedAt(),
                existingAccount.createdAt(),
                existingAccount.updatedAt()
        );

        Account savedAccount = accountRepository.save(updatedAccount);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ACCOUNT_FROZEN,
                "ACCOUNT",
                savedAccount.id().toString(),
                Map.of(
                        "accountNumber", savedAccount.accountNumber(),
                        "reason", savedAccount.statusReason()
                )
        );

        AccountOwnerView view = accountRepository.findViewById(savedAccount.id())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account view not found with id: " + savedAccount.id()
                ));

        return accountMapper.toOwnerResponse(view);
    }

    private String normalizeReason(String reason) {
        if (reason == null) {
            return "Frozen by administrator";
        }

        String trimmed = reason.trim();

        return trimmed.isEmpty()
                ? "Frozen by administrator"
                : trimmed;
    }
}