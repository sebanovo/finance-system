package com.financesystem.finance_api.modules.tenant.accounts.infraestructure.persistence;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.*;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepositoryAdapter implements AccountRepository {

    private static final List<AccountStatus> ACTIVE_OR_PENDING_STATUSES = List.of(
            AccountStatus.ACTIVE,
            AccountStatus.PENDING_APPROVAL,
            AccountStatus.PENDING_VERIFICATION,
            AccountStatus.BLOCKED,
            AccountStatus.SUSPENDED,
            AccountStatus.FROZEN
    );

    private final AccountJpaRepository jpaRepository;

    public AccountRepositoryAdapter(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = toEntity(account);
        AccountEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<AccountOwnerView> findViewById(UUID id) {
        return jpaRepository.findViewById(id).map(this::toView);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber).map(this::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AccountOwnerView> findAllViews() {
        return jpaRepository.findAllViews()
                .stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public List<Account> findAllByUserId(UUID userId) {
        return jpaRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AccountOwnerView> findAllViewsByUserId(UUID userId) {
        return jpaRepository.findAllViewsByUserId(userId)
                .stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public long countByUserId(UUID userId) {
        return jpaRepository.countByUserId(userId);
    }

    @Override
    public long countActiveOrPendingByUserId(UUID userId) {
        return jpaRepository.countByUserIdAndStatusIn(userId, ACTIVE_OR_PENDING_STATUSES);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public boolean existsPrimaryByUserId(UUID userId) {
        return jpaRepository.existsByUserIdAndPrimaryTrue(userId);
    }

    private AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();

        entity.setId(account.id());
        entity.setUserId(account.userId());
        entity.setAccountNumber(account.accountNumber());
        entity.setAccountName(account.accountName());
        entity.setCustomAlias(account.customAlias());
        entity.setAccountType(account.accountType());
        entity.setCurrency(account.currency());
        entity.setAvailableBalance(account.availableBalance());
        entity.setHeldBalance(account.heldBalance());
        entity.setStatus(account.status());
        entity.setStatusReason(account.statusReason());
        entity.setActive(account.active());
        entity.setPrimary(account.primary());
        entity.setOpenedAt(account.openedAt());
        entity.setClosedAt(account.closedAt());

        return entity;
    }

    private Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getUserId(),
                entity.getAccountNumber(),
                entity.getAccountName(),
                entity.getCustomAlias(),
                entity.getAccountType(),
                entity.getCurrency(),
                entity.getAvailableBalance(),
                entity.getHeldBalance(),
                entity.getStatus(),
                entity.getStatusReason(),
                entity.isActive(),
                entity.isPrimary(),
                entity.getOpenedAt(),
                entity.getClosedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private AccountOwnerView toView(AccountOwnerViewProjection projection) {
        return new AccountOwnerView(
                projection.getId(),
                projection.getUserId(),
                projection.getUserEmail(),
                projection.getUserFirstName(),
                projection.getUserLastName(),
                projection.getAccountNumber(),
                AccountName.valueOf(projection.getAccountName()),
                projection.getCustomAlias(),
                AccountType.valueOf(projection.getAccountType()),
                CurrencyCode.valueOf(projection.getCurrency()),
                projection.getAvailableBalance(),
                projection.getHeldBalance(),
                AccountStatus.valueOf(projection.getStatus()),
                projection.getStatusReason(),
                projection.getActive(),
                projection.getPrimary(),
                projection.getOpenedAt(),
                projection.getClosedAt(),
                projection.getCreatedAt(),
                projection.getUpdatedAt()
        );
    }
}
