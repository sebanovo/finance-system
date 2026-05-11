package com.financesystem.finance_api.modules.tenant.accounts.application.service;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountNumberPrefix;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountSequenceRepository;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class AccountNumberGeneratorService {

    private final AccountSequenceRepository accountSequenceRepository;

    public AccountNumberGeneratorService(AccountSequenceRepository accountSequenceRepository) {
        this.accountSequenceRepository = accountSequenceRepository;
    }

    public String generate(AccountType accountType, CurrencyCode currency) {
        long nextValue = accountSequenceRepository.nextValue(accountType, currency);
        String prefix = AccountNumberPrefix.from(accountType);
        int year = Year.now().getValue();

        return "%s-%s-%d-%06d".formatted(
                prefix,
                currency.name(),
                year,
                nextValue
        );
    }
}
