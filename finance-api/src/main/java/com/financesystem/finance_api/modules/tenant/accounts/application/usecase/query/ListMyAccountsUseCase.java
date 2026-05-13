package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.accounts.application.service.CurrentTenantAccountUserService;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListMyAccountsUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CurrentTenantAccountUserService currentTenantAccountUserService;

    public ListMyAccountsUseCase(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            CurrentTenantAccountUserService currentTenantAccountUserService
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.currentTenantAccountUserService = currentTenantAccountUserService;
    }

    @Transactional(readOnly = true)
    public List<AccountOwnerResponse> execute() {
        UUID currentUserId = currentTenantAccountUserService.getCurrentUserId();

        return accountRepository.findAllViewsByUserId(currentUserId)
                .stream()
                .map(accountMapper::toOwnerResponse)
                .toList();
    }
}
