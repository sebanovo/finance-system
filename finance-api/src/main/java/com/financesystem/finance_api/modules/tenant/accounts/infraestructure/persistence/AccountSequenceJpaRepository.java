package com.financesystem.finance_api.modules.tenant.accounts.infraestructure.persistence;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AccountSequenceJpaRepository extends JpaRepository<AccountSequenceEntity, UUID> {

    Optional<AccountSequenceEntity> findByAccountTypeAndCurrency(
            AccountType accountType,
            CurrencyCode currency
    );

    @Modifying
    @Query(value = """
            INSERT INTO tenant_account_sequences (account_type, currency, current_value)
            VALUES (:accountType, :currency, 0)
            ON CONFLICT (account_type, currency) DO NOTHING
            """, nativeQuery = true)
    void ensureSequenceExists(String accountType, String currency);

    @Query(value = """
            UPDATE tenant_account_sequences
            SET current_value = current_value + 1,
                updated_at = NOW()
            WHERE account_type = :accountType
              AND currency = :currency
            RETURNING current_value
            """, nativeQuery = true)
    Long incrementAndReturn(String accountType, String currency);
}
