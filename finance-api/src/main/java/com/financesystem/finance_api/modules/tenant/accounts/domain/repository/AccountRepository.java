package com.financesystem.finance_api.modules.tenant.accounts.domain.repository;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(UUID id);

    Optional<AccountOwnerView> findViewById(UUID id);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAll();

    List<AccountOwnerView> findAllViews();

    List<Account> findAllByUserId(UUID userId);

    List<AccountOwnerView> findAllViewsByUserId(UUID userId);

    long countByUserId(UUID userId);

    long countActiveOrPendingByUserId(UUID userId);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsPrimaryByUserId(UUID userId);
}
