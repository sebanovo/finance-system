package com.financesystem.finance_api.modules.tenant.accounts.infraestructure.persistence;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    List<AccountEntity> findAllByUserId(UUID userId);

    long countByUserId(UUID userId);

    long countByUserIdAndStatusIn(UUID userId, List<AccountStatus> statuses);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByUserIdAndPrimaryTrue(UUID userId);

    @Query(value = """
            SELECT
                a.id AS id,
                a.user_id AS userId,
                u.email AS userEmail,
                u.first_name AS userFirstName,
                u.last_name AS userLastName,
                a.account_number AS accountNumber,
                a.account_name AS accountName,
                a.custom_alias AS customAlias,
                a.account_type AS accountType,
                a.currency AS currency,
                a.available_balance AS availableBalance,
                a.held_balance AS heldBalance,
                a.status AS status,
                a.status_reason AS statusReason,
                a.active AS active,
                a.is_primary AS primary,
                a.opened_at AS openedAt,
                a.closed_at AS closedAt,
                a.created_at AS createdAt,
                a.updated_at AS updatedAt
            FROM tenant_accounts a
            JOIN tenant_users u ON u.id = a.user_id
            ORDER BY a.created_at DESC
            """, nativeQuery = true)
    List<AccountOwnerViewProjection> findAllViews();

    @Query(value = """
            SELECT
                a.id AS id,
                a.user_id AS userId,
                u.email AS userEmail,
                u.first_name AS userFirstName,
                u.last_name AS userLastName,
                a.account_number AS accountNumber,
                a.account_name AS accountName,
                a.custom_alias AS customAlias,
                a.account_type AS accountType,
                a.currency AS currency,
                a.available_balance AS availableBalance,
                a.held_balance AS heldBalance,
                a.status AS status,
                a.status_reason AS statusReason,
                a.active AS active,
                a.is_primary AS primary,
                a.opened_at AS openedAt,
                a.closed_at AS closedAt,
                a.created_at AS createdAt,
                a.updated_at AS updatedAt
            FROM tenant_accounts a
            JOIN tenant_users u ON u.id = a.user_id
            WHERE a.id = :id
            """, nativeQuery = true)
    Optional<AccountOwnerViewProjection> findViewById(UUID id);

    @Query(value = """
            SELECT
                a.id AS id,
                a.user_id AS userId,
                u.email AS userEmail,
                u.first_name AS userFirstName,
                u.last_name AS userLastName,
                a.account_number AS accountNumber,
                a.account_name AS accountName,
                a.custom_alias AS customAlias,
                a.account_type AS accountType,
                a.currency AS currency,
                a.available_balance AS availableBalance,
                a.held_balance AS heldBalance,
                a.status AS status,
                a.status_reason AS statusReason,
                a.active AS active,
                a.is_primary AS primary,
                a.opened_at AS openedAt,
                a.closed_at AS closedAt,
                a.created_at AS createdAt,
                a.updated_at AS updatedAt
            FROM tenant_accounts a
            JOIN tenant_users u ON u.id = a.user_id
            WHERE a.user_id = :userId
            ORDER BY a.is_primary DESC, a.created_at DESC
            """, nativeQuery = true)
    List<AccountOwnerViewProjection> findAllViewsByUserId(UUID userId);
}
