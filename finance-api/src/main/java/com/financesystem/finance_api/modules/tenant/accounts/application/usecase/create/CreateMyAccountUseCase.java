package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.create;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.CreateMyAccountRequest;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.accounts.application.service.AccountNumberGeneratorService;
import com.financesystem.finance_api.modules.tenant.accounts.application.service.CurrentTenantAccountUserService;
import com.financesystem.finance_api.modules.tenant.audit.TenantAuditPayloads;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.AccountNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CreateMyAccountUseCase {

    private static final long DIRECT_CREATION_LIMIT = 2;

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGeneratorService accountNumberGeneratorService;
    private final CurrentTenantAccountUserService currentTenantAccountUserService;
    private final AuditTrailService auditTrailService;

    public CreateMyAccountUseCase(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            AccountNumberGeneratorService accountNumberGeneratorService,
            CurrentTenantAccountUserService currentTenantAccountUserService,
            AuditTrailService auditTrailService
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.accountNumberGeneratorService = accountNumberGeneratorService;
        this.currentTenantAccountUserService = currentTenantAccountUserService;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public AccountOwnerResponse execute(CreateMyAccountRequest request) {
        validateTransactionalAccountType(request.accountType());

        UUID currentUserId = currentTenantAccountUserService.getCurrentUserId();

        long currentAccounts = accountRepository.countActiveOrPendingByUserId(currentUserId);

        AccountStatus initialStatus = currentAccounts < DIRECT_CREATION_LIMIT
                ? AccountStatus.ACTIVE
                : AccountStatus.PENDING_APPROVAL;

        boolean active = initialStatus == AccountStatus.ACTIVE;
        boolean primary = !accountRepository.existsPrimaryByUserId(currentUserId);

        String accountNumber = accountNumberGeneratorService.generate(
                request.accountType(),
                request.currency()
        );

        Account accountToCreate = new Account(
                null,
                currentUserId,
                accountNumber,
                request.accountName(),
                normalizeNullable(request.customAlias()),
                request.accountType(),
                request.currency(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                initialStatus,
                initialStatus == AccountStatus.PENDING_APPROVAL
                        ? "Account requires approval because the direct creation limit was exceeded"
                        : null,
                active,
                primary,
                null,
                null,
                null,
                null
        );

        Account createdAccount = accountRepository.save(accountToCreate);

        String eventType = initialStatus == AccountStatus.PENDING_APPROVAL
                ? AuditEventTypes.ACCOUNT_APPROVAL_REQUESTED
                : AuditEventTypes.ACCOUNT_CREATED;

        auditTrailService.recordTenantEvent(
                eventType,
                "ACCOUNT",
                createdAccount.id().toString(),
                TenantAuditPayloads.details(
                        "userId", createdAccount.userId().toString(),
                        "accountNumber", createdAccount.accountNumber(),
                        "accountType", createdAccount.accountType().name(),
                        "currency", createdAccount.currency().name(),
                        "status", createdAccount.status().name(),
                        "directCreationLimit", DIRECT_CREATION_LIMIT,
                        "accountName", createdAccount.accountName().name(),
                        "customAlias", createdAccount.customAlias()
                ),
                null,
                TenantAuditPayloads.accountState(createdAccount)
        );

        AccountOwnerView view = accountRepository.findViewById(createdAccount.id())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account view not found with id: " + createdAccount.id()
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

    private void validateTransactionalAccountType(AccountType accountType) {
        if (accountType == null || !accountType.isTransactional()) {
            throw new BusinessException(
                    "Account type " + (accountType != null ? accountType.name() : "null")
                            + " is not available for the current bank core. Use WALLET, SAVINGS, CHECKING or PREPAID_CARD."
            );
        }
    }
}
