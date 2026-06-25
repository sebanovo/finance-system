package com.financesystem.finance_api.modules.identity.auth.domain.repository;

import com.financesystem.finance_api.modules.identity.auth.domain.model.AccountActivationToken;

import java.time.Instant;
import java.util.Optional;

public interface AccountActivationTokenRepository {

    AccountActivationToken save(AccountActivationToken accountActivationToken);

    Optional<AccountActivationToken> findByToken(String token);

    void invalidateAllByEmail(String email, Instant usedAt);

    void markUsed(String token, Instant usedAt);
}
