package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.create;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.CreateAccountRequest;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.accounts.application.service.AccountNumberGeneratorService;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.AccountAlreadyExistsException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.AccountNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CreateAccountUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGeneratorService accountNumberGeneratorService;
    private final AuditTrailService auditTrailService;

    public CreateAccountUseCase(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            AccountNumberGeneratorService accountNumberGeneratorService,
            AuditTrailService auditTrailService
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.accountNumberGeneratorService = accountNumberGeneratorService;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public AccountOwnerResponse execute(CreateAccountRequest request) {
        validateTransactionalAccountType(request.accountType());

        String accountNumber = accountNumberGeneratorService.generate(
                request.accountType(),
                request.currency()
        );

        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new AccountAlreadyExistsException(
                    "Account number already exists: " + accountNumber
            );
        }

        boolean shouldBePrimary = Boolean.TRUE.equals(request.primary())
                || !accountRepository.existsPrimaryByUserId(request.userId());

        Account accountToCreate = new Account(
                null,
                request.userId(),
                accountNumber,
                request.accountName(),
                normalizeNullable(request.customAlias()),
                request.accountType(),
                request.currency(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                null,
                true,
                shouldBePrimary,
                null,
                null,
                null,
                null
        );

        Account createdAccount = accountRepository.save(accountToCreate);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ACCOUNT_CREATED,
                "ACCOUNT",
                createdAccount.id().toString(),
                Map.of(
                        "userId", createdAccount.userId().toString(),
                        "accountNumber", createdAccount.accountNumber(),
                        "accountType", createdAccount.accountType().name(),
                        "currency", createdAccount.currency().name(),
                        "primary", createdAccount.primary(),
                        "accountName", createdAccount.accountName().name(),
                        "customAlias", createdAccount.customAlias() != null ? createdAccount.customAlias() : ""
                )
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
