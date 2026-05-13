package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountBalanceResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.accounts.application.service.CurrentTenantAccountUserService;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.AccountNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetMyAccountBalanceUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CurrentTenantAccountUserService currentTenantAccountUserService;

    public GetMyAccountBalanceUseCase(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            CurrentTenantAccountUserService currentTenantAccountUserService
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.currentTenantAccountUserService = currentTenantAccountUserService;
    }

    @Transactional(readOnly = true)
    public AccountBalanceResponse execute(UUID accountId) {
        UUID currentUserId = currentTenantAccountUserService.getCurrentUserId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId
                ));

        if (!account.userId().equals(currentUserId)) {
            throw new AccountNotFoundException("Account not found with id: " + accountId);
        }

        return accountMapper.toBalanceResponse(account);
    }
}
