package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.query;

import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountLookupResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.AccountNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class LookupMyAccountByNumberUseCase {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public LookupMyAccountByNumberUseCase(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Transactional(readOnly = true)
    public AccountLookupResponse execute(String accountNumber) {
        String normalizedAccountNumber = normalizeAccountNumber(accountNumber);
        if (normalizedAccountNumber.isEmpty()) {
            throw new AccountNotFoundException("Account not found with number: " + accountNumber);
        }

        AccountOwnerView account = accountRepository.findViewByAccountNumber(normalizedAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + normalizedAccountNumber));

        return accountMapper.toLookupResponse(account);
    }

    private String normalizeAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return "";
        }

        return accountNumber.trim().toUpperCase(Locale.ROOT);
    }
}
