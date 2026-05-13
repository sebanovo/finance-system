package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAccountsUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public ListAccountsUseCase(
            AccountRepository accountRepository,
            AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Transactional(readOnly = true)
    public List<AccountOwnerResponse> execute() {
        return accountRepository.findAllViews()
                .stream()
                .map(accountMapper::toOwnerResponse)
                .toList();
    }
}
