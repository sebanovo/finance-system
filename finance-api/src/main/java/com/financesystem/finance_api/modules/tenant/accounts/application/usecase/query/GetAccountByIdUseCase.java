package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.AccountNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetAccountByIdUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public GetAccountByIdUseCase(
            AccountRepository accountRepository,
            AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Transactional(readOnly = true)
    public AccountOwnerResponse execute(UUID id) {
        return accountRepository.findViewById(id)
                .map(accountMapper::toOwnerResponse)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }
}
