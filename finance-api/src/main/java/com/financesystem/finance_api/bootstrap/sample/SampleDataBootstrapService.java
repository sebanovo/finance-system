package com.financesystem.finance_api.bootstrap.sample;

import com.financesystem.finance_api.modules.platform.tenants.application.dto.CreateTenantRequest;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.usecase.CreateTenantUseCase;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import com.financesystem.finance_api.modules.platform.onboarding.application.service.TenantOwnerAdminProvisioningService;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionProvisioningService;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class SampleDataBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataBootstrapService.class);
    private static final String DEMO_PASSWORD = "password";
    private static final String DEMO_IDEMPOTENCY_PREFIX = "sample-bootstrap";
    private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal("150.00");

    private final CreateTenantUseCase createTenantUseCase;
    private final TenantOwnerAdminProvisioningService tenantOwnerAdminProvisioningService;
    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformSubscriptionProvisioningService platformSubscriptionProvisioningService;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public SampleDataBootstrapService(
            CreateTenantUseCase createTenantUseCase,
            TenantOwnerAdminProvisioningService tenantOwnerAdminProvisioningService,
            PlatformTenantRepository platformTenantRepository,
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformSubscriptionProvisioningService platformSubscriptionProvisioningService,
            @Qualifier("targetDataSource") DataSource targetDataSource,
            PasswordEncoder passwordEncoder
    ) {
        this.createTenantUseCase = createTenantUseCase;
        this.tenantOwnerAdminProvisioningService = tenantOwnerAdminProvisioningService;
        this.platformTenantRepository = platformTenantRepository;
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformSubscriptionProvisioningService = platformSubscriptionProvisioningService;
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
        this.passwordEncoder = passwordEncoder;
    }

    public void seedDemoTenants() {
        logger.info("Seeding optional sample tenants and demo data...");

        for (TenantSeed seed : sampleTenants()) {
            try {
                PlatformTenantResponse tenant = ensureTenant(seed);
                seedTenantBundle(seed, tenant);
            } catch (Exception exception) {
                logger.warn("Sample tenant '{}' could not be fully seeded. Continuing with the next seed.", seed.slug(), exception);
            }
        }

        logger.info("Optional sample tenants seeding completed.");
    }

    private PlatformTenantResponse ensureTenant(TenantSeed seed) {
        return platformTenantRepository.findBySlug(seed.slug())
                .map(tenant -> new PlatformTenantResponse(
                        tenant.id(),
                        tenant.name(),
                        tenant.slug(),
                        tenant.schemaName(),
                        tenant.status().name(),
                        tenant.planId(),
                        tenant.active(),
                        tenant.createdAt(),
                        tenant.updatedAt()
                ))
                .orElseGet(() -> {
                    PlatformTenantResponse tenant = createTenantUseCase.execute(
                            new CreateTenantRequest(seed.name(), seed.slug(), seed.planCode())
                    );

                    tenantOwnerAdminProvisioningService.provisionOwnerAdminWithoutVerification(
                            tenant.schemaName(),
                            tenant.slug(),
                            seed.ownerEmail(),
                            DEMO_PASSWORD,
                            seed.ownerFirstName(),
                            seed.ownerLastName()
                    );

                    return tenant;
                });
    }

    private void seedTenantBundle(TenantSeed seed, PlatformTenantResponse tenant) {
        String schemaName = tenant.schemaName();
        logger.info("Seeding demo data for tenant '{}' in schema '{}'.", seed.slug(), schemaName);

        UUID ownerUserId = upsertTenantUser(
                schemaName,
                seed.ownerEmail(),
                seed.ownerFirstName(),
                seed.ownerLastName(),
                "OWNER_ADMIN"
        );

        ensurePlatformSubscription(seed);
        PlatformTenant platformTenant = platformTenantRepository.findBySlug(seed.slug())
                .orElseThrow(() -> new IllegalStateException("Tenant not found for platform audit seeding: " + seed.slug()));
        seedPlatformAuditEvents(platformTenant, seed, ownerUserId);

        ensureAccountSequence(schemaName, "WALLET", "BOB", 1L);
        ensureAccountSequence(schemaName, "SAVINGS", "BOB", 1L);

        String ownerAccountNumber = accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L);
        String savingsAccountNumber = accountNumber(seed.accountPrefix(), "SAVINGS", "BOB", 1L);

        UUID ownerAccountId = upsertTenantAccount(
                schemaName,
                ownerUserId,
                ownerAccountNumber,
                "MAIN_WALLET",
                "WALLET",
                "BOB",
                seed.ownerStartingBalance(),
                true,
                "Billetera principal demo"
        );

        UUID savingsAccountId = upsertTenantAccount(
                schemaName,
                ownerUserId,
                savingsAccountNumber,
                "SAVINGS_ACCOUNT",
                "SAVINGS",
                "BOB",
                seed.savingsStartingBalance(),
                false,
                "Cuenta de ahorro demo"
        );

        UUID ownerTransactionId = seedOwnerFinanceBundle(
                tenant,
                schemaName,
                seed,
                ownerUserId,
                ownerAccountId,
                savingsAccountId
        );

        runOptionalSeedStep("service enrollment", seed.slug(), () -> upsertServiceEnrollment(
                schemaName,
                ownerUserId,
                seed.serviceProviderCode(),
                seed.serviceCustomerCode(),
                seed.serviceCustomerName(),
                seed.serviceAlias()
        ));

        runOptionalSeedStep("tenant users", seed.slug(), () -> seedEnterpriseTenantUsers(
                tenant,
                schemaName,
                seed,
                ownerUserId,
                ownerAccountId
        ));

        logger.info(
                "Demo bundle seeded for tenant '{}': owner '{}'.",
                seed.slug(),
                seed.ownerEmail()
        );
    }

    private void ensurePlatformSubscription(TenantSeed seed) {
        PlatformTenant tenant = platformTenantRepository.findBySlug(seed.slug())
                .orElseThrow(() -> new IllegalStateException("Tenant not found for subscription seeding: " + seed.slug()));

        if (platformSubscriptionRepository.findCurrentByTenantId(tenant.id()).isPresent()) {
            return;
        }

        platformSubscriptionProvisioningService.assignCurrentSubscription(
                tenant.id(),
                seed.planCode(),
                null,
                true
        );
    }

    private void runOptionalSeedStep(String stepName, String tenantSlug, Runnable step) {
        try {
            step.run();
        } catch (Exception exception) {
            logger.warn("Sample tenant '{}' failed during {} seeding. Continuing with the next step.", tenantSlug, stepName, exception);
        }
    }

    private UUID seedOwnerFinanceBundle(
            PlatformTenantResponse tenant,
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            UUID ownerAccountId,
            UUID savingsAccountId
    ) {
        String ownerAccountNumber = accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L);
        String savingsAccountNumber = accountNumber(seed.accountPrefix(), "SAVINGS", "BOB", 1L);

        List<UUID> ownerTransactionIds = seedAlternatingTransferSeriesIds(
                schemaName,
                seed,
                ownerUserId,
                ownerAccountId,
                ownerAccountNumber,
                seed.ownerStartingBalance(),
                savingsAccountId,
                savingsAccountNumber,
                seed.savingsStartingBalance(),
                "owner-transfer",
                10
        );
        UUID ownerTransactionId = ownerTransactionIds.get(ownerTransactionIds.size() - 1);

        upsertAccountingPeriods(schemaName, seed, ownerTransactionId);
        upsertJournalEntry(schemaName, seed, ownerTransactionId, ownerAccountId, savingsAccountId);
        upsertLimitRules(schemaName, seed, ownerUserId);
        upsertFxConfiguration(schemaName, seed);
        upsertOperationFees(schemaName, seed);
        upsertNotifications(schemaName, seed, ownerUserId, ownerTransactionId, "owner");
        upsertTenantAuditEvents(schemaName, seed, ownerUserId, ownerTransactionId);
        seedPublicServicePaymentsForUser(
                schemaName,
                tenant,
                seed,
                ownerUserId,
                ownerAccountId,
                ownerAccountNumber,
                seed.serviceCustomerCode(),
                seed.serviceCustomerName(),
                seed.serviceAlias(),
                ownerTransactionIds,
                "owner",
                0
        );
        seedLoanBundleForUser(
                schemaName,
                seed,
                ownerUserId,
                ownerAccountId,
                ownerAccountNumber,
                savingsAccountId,
                savingsAccountNumber,
                "owner"
        );

        return ownerTransactionId;
    }

    private void seedEnterpriseTenantUsers(
            PlatformTenantResponse tenant,
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            UUID ownerAccountId
    ) {
        List<TenantUserSeed> userSeeds = tenantUserSeeds(seed);
        for (int index = 0; index < userSeeds.size(); index++) {
            TenantUserSeed userSeed = userSeeds.get(index);
            int userNumber = index + 1;

            UUID userUserId = upsertTenantUser(
                    schemaName,
                    userSeed.email(),
                    userSeed.firstName(),
                    userSeed.lastName(),
                    "USER"
            );

            long walletSequence = userNumber + 1L;
            long checkingSequence = userNumber;
            ensureAccountSequence(schemaName, "WALLET", "BOB", walletSequence);
            ensureAccountSequence(schemaName, "CHECKING", "BOB", checkingSequence);

            String walletAccountNumber = accountNumber(seed.accountPrefix(), "WALLET", "BOB", walletSequence);
            String checkingAccountNumber = accountNumber(seed.accountPrefix(), "CHECKING", "BOB", checkingSequence);

            UUID walletAccountId = upsertTenantAccount(
                    schemaName,
                    userUserId,
                    walletAccountNumber,
                    "MAIN_WALLET",
                    "WALLET",
                    "BOB",
                    new BigDecimal("1000.00"),
                    true,
                    userSeed.firstName() + " wallet"
            );

            UUID checkingAccountId = upsertTenantAccount(
                    schemaName,
                    userUserId,
                    checkingAccountNumber,
                    "CHECKING_ACCOUNT",
                    "CHECKING",
                    "BOB",
                    new BigDecimal("0.00"),
                    false,
                    userSeed.firstName() + " checking"
            );

            List<UUID> transferTransactionIds = seedAlternatingTransferSeriesIds(
                    schemaName,
                    seed,
                    userUserId,
                    walletAccountId,
                    walletAccountNumber,
                    new BigDecimal("1000.00"),
                    checkingAccountId,
                    checkingAccountNumber,
                    new BigDecimal("0.00"),
                    "user-" + String.format("%02d", userNumber) + "-transfer",
                    10
            );
            UUID lastTransactionId = transferTransactionIds.get(transferTransactionIds.size() - 1);

            upsertNotifications(schemaName, seed, userUserId, lastTransactionId, "user-" + String.format("%02d", userNumber));
            insertTenantAuditEvent(
                    schemaName,
                    seed,
                    userUserId,
                    "TRANSFER_RECEIVED",
                    "TRANSACTIONS",
                    lastTransactionId.toString(),
                    "Usuario demo recibió una serie de transferencias"
            );
            seedPublicServicePaymentsForUser(
                    schemaName,
                    tenant,
                    seed,
                    userUserId,
                    checkingAccountId,
                    checkingAccountNumber,
                    tenantServiceCustomerCode(seed, userNumber),
                    tenantServiceCustomerName(seed, userSeed.firstName(), userNumber),
                    tenantServiceAlias(seed, userSeed.firstName(), userNumber),
                    transferTransactionIds,
                    "user-" + String.format("%02d", userNumber),
                    userNumber
            );
            seedLoanBundleForUser(
                    schemaName,
                    seed,
                    userUserId,
                    walletAccountId,
                    walletAccountNumber,
                    checkingAccountId,
                    checkingAccountNumber,
                    "user-" + String.format("%02d", userNumber)
            );
        }
    }

    private List<UUID> seedAlternatingTransferSeriesIds(
            String schemaName,
            TenantSeed seed,
            UUID requestedByUserId,
            UUID firstAccountId,
            String firstAccountNumber,
            BigDecimal firstStartingBalance,
            UUID secondAccountId,
            String secondAccountNumber,
            BigDecimal secondStartingBalance,
            String transactionKeyPrefix,
            int transactionCount
    ) {
        BigDecimal firstBalance = firstStartingBalance;
        BigDecimal secondBalance = secondStartingBalance;
        List<UUID> transactionIds = new ArrayList<>();

        for (int index = 1; index <= transactionCount; index++) {
            boolean firstIsSource = index % 2 == 1;

            UUID sourceAccountId = firstIsSource ? firstAccountId : secondAccountId;
            UUID targetAccountId = firstIsSource ? secondAccountId : firstAccountId;
            String sourceAccountNumber = firstIsSource ? firstAccountNumber : secondAccountNumber;
            String targetAccountNumber = firstIsSource ? secondAccountNumber : firstAccountNumber;
            BigDecimal sourceBefore = firstIsSource ? firstBalance : secondBalance;
            BigDecimal targetBefore = firstIsSource ? secondBalance : firstBalance;

            UUID transactionId = upsertTransferTransaction(
                    schemaName,
                    seed,
                    requestedByUserId,
                    sourceAccountId,
                    targetAccountId,
                    transactionKeyPrefix + "-" + index
            );
            transactionIds.add(transactionId);

            BigDecimal sourceAfter = sourceBefore.subtract(TRANSFER_AMOUNT);
            BigDecimal targetAfter = targetBefore.add(TRANSFER_AMOUNT);

            upsertTransactionMovement(
                    schemaName,
                    transactionId,
                    sourceAccountId,
                    "DEBIT",
                    TRANSFER_AMOUNT,
                    sourceBefore,
                    sourceAfter,
                    "Salida por transferencia demo"
            );

            upsertTransactionMovement(
                    schemaName,
                    transactionId,
                    targetAccountId,
                    "CREDIT",
                    TRANSFER_AMOUNT,
                    targetBefore,
                    targetAfter,
                    "Entrada por transferencia demo"
            );

            jdbcTemplate.update(
                    """
                    UPDATE %s.tenant_accounts
                    SET available_balance = CASE
                            WHEN account_number = ? THEN ?
                            WHEN account_number = ? THEN ?
                            ELSE available_balance
                        END,
                        updated_at = NOW()
                    WHERE account_number IN (?, ?)
                    """.formatted(schemaName),
                    sourceAccountNumber,
                    sourceAfter,
                    targetAccountNumber,
                    targetAfter,
                    sourceAccountNumber,
                    targetAccountNumber
            );

            if (firstIsSource) {
                firstBalance = sourceAfter;
                secondBalance = targetAfter;
            } else {
                secondBalance = sourceAfter;
                firstBalance = targetAfter;
            }
        }

        return transactionIds;
    }

    private UUID seedAlternatingTransferSeries(
            String schemaName,
            TenantSeed seed,
            UUID requestedByUserId,
            UUID firstAccountId,
            String firstAccountNumber,
            BigDecimal firstStartingBalance,
            UUID secondAccountId,
            String secondAccountNumber,
            BigDecimal secondStartingBalance,
            String transactionKeyPrefix,
            int transactionCount
    ) {
        List<UUID> transactionIds = seedAlternatingTransferSeriesIds(
                schemaName,
                seed,
                requestedByUserId,
                firstAccountId,
                firstAccountNumber,
                firstStartingBalance,
                secondAccountId,
                secondAccountNumber,
                secondStartingBalance,
                transactionKeyPrefix,
                transactionCount
        );
        return transactionIds.get(transactionIds.size() - 1);
    }

    private List<TenantUserSeed> tenantUserSeeds(TenantSeed seed) {
        return List.of(
                new TenantUserSeed(seed.userEmail(), seed.userFirstName(), seed.userLastName()),
                generatedTenantUserSeed(seed, 2),
                generatedTenantUserSeed(seed, 3),
                generatedTenantUserSeed(seed, 4),
                generatedTenantUserSeed(seed, 5),
                generatedTenantUserSeed(seed, 6),
                generatedTenantUserSeed(seed, 7),
                generatedTenantUserSeed(seed, 8),
                generatedTenantUserSeed(seed, 9)
        );
    }

    private TenantUserSeed generatedTenantUserSeed(TenantSeed seed, int index) {
        return new TenantUserSeed(
                "user" + index + "@gmail.com",
                "user" + index,
                seed.slug()
        );
    }

    private void seedPlatformAuditEvents(PlatformTenant tenant, TenantSeed seed, UUID ownerUserId) {
        insertPlatformAuditEvent(
                tenant,
                seed,
                ownerUserId,
                "TENANT_BOOTSTRAPPED",
                "TENANT",
                tenant.id().toString(),
                "Tenant demo created and seeded"
        );
        insertPlatformAuditEvent(
                tenant,
                seed,
                ownerUserId,
                "SUBSCRIPTION_ASSIGNED",
                "SUBSCRIPTION",
                seed.planCode(),
                "Current subscription assigned for demo tenant"
        );
    }

    private void insertPlatformAuditEvent(
            PlatformTenant tenant,
            TenantSeed seed,
            UUID ownerUserId,
            String eventType,
            String resourceType,
            String resourceId,
            String details
    ) {
        UUID eventId = deterministicUuid("public:platform-audit:" + tenant.slug() + ":" + eventType + ":" + resourceId);
        jdbcTemplate.update(
                """
                INSERT INTO public.platform_audit_events (
                    id, actor_subject, actor_id, actor_email, tenant_slug, event_type, resource_type,
                    resource_id, event_details, source, outcome, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'BOOTSTRAP', 'SUCCESS', NOW())
                ON CONFLICT (id) DO UPDATE SET
                    actor_subject = EXCLUDED.actor_subject,
                    actor_id = EXCLUDED.actor_id,
                    actor_email = EXCLUDED.actor_email,
                    tenant_slug = EXCLUDED.tenant_slug,
                    event_type = EXCLUDED.event_type,
                    resource_type = EXCLUDED.resource_type,
                    resource_id = EXCLUDED.resource_id,
                    event_details = EXCLUDED.event_details,
                    source = EXCLUDED.source,
                    outcome = EXCLUDED.outcome
                """,
                eventId,
                ownerUserId.toString(),
                ownerUserId,
                seed.ownerEmail(),
                tenant.slug(),
                eventType,
                resourceType,
                resourceId,
                details
        );
    }

    private record TenantUserSeed(
            String email,
            String firstName,
            String lastName
    ) {
    }

    private UUID upsertTenantUser(
            String schemaName,
            String email,
            String firstName,
            String lastName,
            String roleName
    ) {
        String normalizedEmail = email.trim().toLowerCase();
        String normalizedFirstName = firstName.trim();
        String normalizedLastName = lastName.trim();
        String passwordHash = passwordEncoder.encode(DEMO_PASSWORD);
        UUID userId = deterministicUuid(schemaName + ":user:" + normalizedEmail);

        UUID persistedUserId = jdbcTemplate.queryForObject(
                """
                INSERT INTO %s.tenant_users (
                    id, email, password_hash, first_name, last_name, active, status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, true, 'ACTIVE', NOW(), NOW())
                ON CONFLICT (email) DO UPDATE SET
                    password_hash = EXCLUDED.password_hash,
                    first_name = EXCLUDED.first_name,
                    last_name = EXCLUDED.last_name,
                    active = true,
                    status = 'ACTIVE',
                    updated_at = NOW()
                RETURNING id
                """.formatted(schemaName),
                UUID.class,
                userId,
                normalizedEmail,
                passwordHash,
                normalizedFirstName,
                normalizedLastName
        );

        UUID roleId = fetchRequiredRoleId(schemaName, roleName);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_user_roles (user_id, role_id, assigned_at)
                VALUES (?, ?, NOW())
                ON CONFLICT (user_id, role_id) DO NOTHING
                """.formatted(schemaName),
                persistedUserId,
                roleId
        );

        return persistedUserId;
    }

    private UUID upsertTenantAccount(
            String schemaName,
            UUID userId,
            String accountNumber,
            String accountName,
            String accountType,
            String currency,
            BigDecimal availableBalance,
            boolean primaryAccount,
            String customAlias
    ) {
        UUID accountId = deterministicUuid(schemaName + ":account:" + accountNumber);
        String normalizedAlias = customAlias == null ? null : customAlias.trim();

        return jdbcTemplate.queryForObject(
                """
                INSERT INTO %s.tenant_accounts (
                    id, user_id, account_number, account_name, custom_alias, account_type, currency,
                    available_balance, held_balance, status, active, is_primary, opened_at, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 'ACTIVE', true, ?, NOW(), NOW(), NOW())
                ON CONFLICT (account_number) DO UPDATE SET
                    user_id = EXCLUDED.user_id,
                    account_name = EXCLUDED.account_name,
                    custom_alias = EXCLUDED.custom_alias,
                    account_type = EXCLUDED.account_type,
                    currency = EXCLUDED.currency,
                    available_balance = EXCLUDED.available_balance,
                    held_balance = 0,
                    status = 'ACTIVE',
                    active = true,
                    is_primary = EXCLUDED.is_primary,
                    status_reason = NULL,
                    updated_at = NOW()
                RETURNING id
                """.formatted(schemaName),
                UUID.class,
                accountId,
                userId,
                accountNumber,
                accountName,
                normalizedAlias,
                accountType,
                currency,
                availableBalance,
                primaryAccount
        );
    }

    private UUID upsertTransferTransaction(
            String schemaName,
            TenantSeed seed,
            UUID requestedByUserId,
            UUID sourceAccountId,
            UUID targetAccountId,
            String transactionKeySuffix
    ) {
        String idempotencyKey = DEMO_IDEMPOTENCY_PREFIX + ":" + seed.slug() + ":" + transactionKeySuffix;
        UUID transactionId = deterministicUuid(schemaName + ":transaction:" + idempotencyKey);
        BigDecimal amount = TRANSFER_AMOUNT;
        String externalReference = "SEED-" + seed.slug().toUpperCase() + "-" + transactionKeySuffix.toUpperCase().replaceAll("[^A-Z0-9]+", "-");

        return jdbcTemplate.queryForObject(
                """
                INSERT INTO %s.tenant_transactions (
                    id, type, status, channel, amount, currency, source_account_id, target_account_id,
                    external_reference, idempotency_key, description, failure_reason, metadata,
                    parent_transaction_id, reversed_transaction_id, requested_by_user_id, approved_by_user_id,
                    processed_at, created_at, updated_at
                )
                VALUES (
                    ?, 'TRANSFER', 'COMPLETED', 'MANUAL', ?, 'BOB', ?, ?,
                    ?, ?, ?, NULL, '{}'::jsonb,
                    NULL, NULL, ?, ?, NOW(), NOW(), NOW()
                )
                ON CONFLICT (requested_by_user_id, idempotency_key) DO UPDATE SET
                    type = EXCLUDED.type,
                    status = EXCLUDED.status,
                    channel = EXCLUDED.channel,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    source_account_id = EXCLUDED.source_account_id,
                    target_account_id = EXCLUDED.target_account_id,
                    external_reference = EXCLUDED.external_reference,
                    description = EXCLUDED.description,
                    failure_reason = NULL,
                    metadata = EXCLUDED.metadata,
                    approved_by_user_id = EXCLUDED.approved_by_user_id,
                    processed_at = NOW(),
                    updated_at = NOW()
                RETURNING id
                """.formatted(schemaName),
                UUID.class,
                transactionId,
                amount,
                sourceAccountId,
                targetAccountId,
                externalReference,
                idempotencyKey,
                "Demo transfer between seeded accounts - " + transactionKeySuffix,
                requestedByUserId,
                requestedByUserId
        );
    }

    private void seedRegularUserBundle(
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            UUID ownerAccountId
    ) {
        UUID userUserId = upsertTenantUser(
                schemaName,
                seed.userEmail(),
                seed.userFirstName(),
                seed.userLastName(),
                "USER"
        );

        ensureAccountSequence(schemaName, "CHECKING", "BOB", 1L);

        String userAccountNumber = accountNumber(seed.accountPrefix(), "CHECKING", "BOB", 1L);
        UUID userAccountId = upsertTenantAccount(
                schemaName,
                userUserId,
                userAccountNumber,
                "CHECKING_ACCOUNT",
                "CHECKING",
                "BOB",
                new BigDecimal("0.00"),
                false,
                "Cuenta bancaria usuario demo"
        );

        UUID userTransactionId = upsertTransferTransaction(
                schemaName,
                seed,
                ownerUserId,
                ownerAccountId,
                userAccountId,
                "user-transfer-1"
        );

        BigDecimal ownerBefore = seed.ownerStartingBalance().subtract(TRANSFER_AMOUNT);
        BigDecimal ownerAfter = ownerBefore.subtract(TRANSFER_AMOUNT);
        BigDecimal userBefore = new BigDecimal("0.00");
        BigDecimal userAfter = userBefore.add(TRANSFER_AMOUNT);

        upsertTransactionMovement(
                schemaName,
                userTransactionId,
                ownerAccountId,
                "DEBIT",
                TRANSFER_AMOUNT,
                ownerBefore,
                ownerAfter,
                "Salida por transferencia a usuario demo"
        );

        upsertTransactionMovement(
                schemaName,
                userTransactionId,
                userAccountId,
                "CREDIT",
                TRANSFER_AMOUNT,
                userBefore,
                userAfter,
                "Entrada por transferencia demo"
        );

        jdbcTemplate.update(
                """
                UPDATE %s.tenant_accounts
                SET available_balance = CASE
                        WHEN account_number = ? THEN ?
                        WHEN account_number = ? THEN ?
                        ELSE available_balance
                    END,
                    updated_at = NOW()
                WHERE account_number IN (?, ?)
                """.formatted(schemaName),
                accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L),
                ownerAfter,
                userAccountNumber,
                userAfter,
                accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L),
                userAccountNumber
        );

        upsertNotifications(schemaName, seed, userUserId, userTransactionId, "user");
        insertTenantAuditEvent(
                schemaName,
                seed,
                userUserId,
                "TRANSFER_RECEIVED",
                "TRANSACTIONS",
                userTransactionId.toString(),
                "Usuario demo recibió una transferencia"
        );
    }

    private void upsertTransactionMovements(
            String schemaName,
            TenantSeed seed,
            UUID transactionId,
            UUID sourceAccountId,
            UUID targetAccountId
    ) {
        BigDecimal ownerBefore = seed.ownerStartingBalance();
        BigDecimal ownerAfter = ownerBefore.subtract(TRANSFER_AMOUNT);
        BigDecimal customerBefore = seed.savingsStartingBalance();
        BigDecimal customerAfter = customerBefore.add(TRANSFER_AMOUNT);

        upsertTransactionMovement(
                schemaName,
                transactionId,
                sourceAccountId,
                "DEBIT",
                TRANSFER_AMOUNT,
                ownerBefore,
                ownerAfter,
                "Salida por transferencia demo"
        );

        upsertTransactionMovement(
                schemaName,
                transactionId,
                targetAccountId,
                "CREDIT",
                TRANSFER_AMOUNT,
                customerBefore,
                customerAfter,
                "Entrada por transferencia demo"
        );

        jdbcTemplate.update(
                """
                UPDATE %s.tenant_accounts
                SET available_balance = CASE
                        WHEN account_number = ? THEN ?
                        WHEN account_number = ? THEN ?
                        ELSE available_balance
                    END,
                    updated_at = NOW()
                WHERE account_number IN (?, ?)
                """.formatted(schemaName),
                accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L),
                ownerAfter,
                accountNumber(seed.accountPrefix(), "SAVINGS", "BOB", 1L),
                customerAfter,
                accountNumber(seed.accountPrefix(), "WALLET", "BOB", 1L),
                accountNumber(seed.accountPrefix(), "SAVINGS", "BOB", 1L)
        );
    }

    private void upsertTransactionMovement(
            String schemaName,
            UUID transactionId,
            UUID accountId,
            String movementType,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            String description
    ) {
        UUID movementId = deterministicUuid(schemaName + ":movement:" + transactionId + ":" + accountId + ":" + movementType);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_transaction_movements (
                    id, transaction_id, account_id, movement_type, amount, currency,
                    balance_before, balance_after, description, created_at
                )
                VALUES (?, ?, ?, ?, ?, 'BOB', ?, ?, ?, NOW())
                ON CONFLICT (id) DO UPDATE SET
                    transaction_id = EXCLUDED.transaction_id,
                    account_id = EXCLUDED.account_id,
                    movement_type = EXCLUDED.movement_type,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    balance_before = EXCLUDED.balance_before,
                    balance_after = EXCLUDED.balance_after,
                    description = EXCLUDED.description
                """.formatted(schemaName),
                movementId,
                transactionId,
                accountId,
                movementType,
                amount,
                balanceBefore,
                balanceAfter,
                description
        );
    }

    private void upsertAccountingPeriods(String schemaName, TenantSeed seed, UUID sourceTransactionId) {
        String currentPeriodCode = currentPeriodCode(seed);
        String previousPeriodCode = previousPeriodCode(seed);

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_accounting_periods (
                    id, period_code, period_type, status, start_date, end_date, closed_at, description, created_at, updated_at
                )
                VALUES (?, ?, 'MONTHLY', 'OPEN', date_trunc('month', NOW())::date, (date_trunc('month', NOW()) + INTERVAL '1 month - 1 day')::date, NULL, ?, NOW(), NOW())
                ON CONFLICT (period_code) DO UPDATE SET
                    period_type = 'MONTHLY',
                    status = 'OPEN',
                    closed_at = NULL,
                    description = EXCLUDED.description,
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":period:" + currentPeriodCode),
                currentPeriodCode,
                "Periodo mensual abierto demo"
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_accounting_periods (
                    id, period_code, period_type, status, start_date, end_date, closed_at, description, created_at, updated_at
                )
                VALUES (?, ?, 'MONTHLY', 'CLOSED', (date_trunc('month', NOW()) - INTERVAL '1 month')::date, (date_trunc('month', NOW()) - INTERVAL '1 day')::date, NOW(), ?, NOW(), NOW())
                ON CONFLICT (period_code) DO UPDATE SET
                    period_type = 'MONTHLY',
                    status = 'CLOSED',
                    closed_at = NOW(),
                    description = EXCLUDED.description,
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":period:" + previousPeriodCode),
                previousPeriodCode,
                "Periodo mensual cerrado demo"
        );
    }

    private void upsertJournalEntry(String schemaName, TenantSeed seed, UUID sourceTransactionId, UUID debitAccountId, UUID creditAccountId) {
        String entryNumber = seed.slug().toUpperCase() + "-JE-001";
        String periodCode = currentPeriodCode(seed);
        UUID entryId = deterministicUuid(schemaName + ":journal:" + entryNumber);
        UUID periodId = deterministicUuid(schemaName + ":period:" + periodCode);

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_journal_entries (
                    id, entry_number, source_transaction_id, period_id, entry_type, status,
                    description, reference, total_debits, total_credits, posted_at, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, 'TRANSFER', 'POSTED', ?, ?, ?, ?, NOW(), NOW(), NOW())
                ON CONFLICT (entry_number) DO UPDATE SET
                    source_transaction_id = EXCLUDED.source_transaction_id,
                    period_id = EXCLUDED.period_id,
                    entry_type = EXCLUDED.entry_type,
                    status = EXCLUDED.status,
                    description = EXCLUDED.description,
                    reference = EXCLUDED.reference,
                    total_debits = EXCLUDED.total_debits,
                    total_credits = EXCLUDED.total_credits,
                    posted_at = NOW(),
                    updated_at = NOW()
                """.formatted(schemaName),
                entryId,
                entryNumber,
                sourceTransactionId,
                periodId,
                "Asiento contable demo",
                "SEED-" + seed.slug().toUpperCase() + "-001",
                TRANSFER_AMOUNT,
                TRANSFER_AMOUNT
        );

        upsertJournalLine(schemaName, entryId, 1, "MAIN_WALLET", "Billetera principal", "DEBIT", TRANSFER_AMOUNT, "BOB", "Débito por transferencia demo");
        upsertJournalLine(schemaName, entryId, 2, "SAVINGS_ACCOUNT", "Cuenta de ahorro", "CREDIT", TRANSFER_AMOUNT, "BOB", "Crédito por transferencia demo");
    }

    private void upsertJournalLine(
            String schemaName,
            UUID journalEntryId,
            int lineNo,
            String accountCode,
            String accountName,
            String lineType,
            BigDecimal amount,
            String currency,
            String description
    ) {
        UUID lineId = deterministicUuid(schemaName + ":journal-line:" + journalEntryId + ":" + lineNo);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_journal_lines (
                    id, journal_entry_id, line_no, account_code, account_name, line_type,
                    amount, currency, description, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
                ON CONFLICT (journal_entry_id, line_no) DO UPDATE SET
                    account_code = EXCLUDED.account_code,
                    account_name = EXCLUDED.account_name,
                    line_type = EXCLUDED.line_type,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    description = EXCLUDED.description
                """.formatted(schemaName),
                lineId,
                journalEntryId,
                lineNo,
                accountCode,
                accountName,
                lineType,
                amount,
                currency,
                description
        );
    }

    private void upsertLimitRules(String schemaName, TenantSeed seed, UUID ownerUserId) {
        insertLimitRule(schemaName, seed, ownerUserId, "DAILY_TRANSFER_AMOUNT", "Límite diario de transferencias", "DAILY_AMOUNT", "TENANT", "DAILY", "TRANSFER", null, "BOB", new BigDecimal("2500.00"), null, true);
        insertLimitRule(schemaName, seed, ownerUserId, "MONTHLY_WITHDRAWAL_COUNT", "Límite mensual de retiros", "MONTHLY_COUNT", "TENANT", "MONTHLY", "WITHDRAWAL", null, null, null, 20L, true);
    }

    private void upsertFxConfiguration(String schemaName, TenantSeed seed) {
        upsertFxRate(schemaName, seed, "BOB", "USD", new BigDecimal("0.14500000"), "BOB a USD");
        upsertFxRate(schemaName, seed, "USD", "BOB", new BigDecimal("6.89000000"), "USD a BOB");
        upsertFxRate(schemaName, seed, "BOB", "EUR", new BigDecimal("0.13200000"), "BOB a EUR");
        upsertFxRate(schemaName, seed, "EUR", "BOB", new BigDecimal("7.42000000"), "EUR a BOB");
    }

    private void upsertFxRate(String schemaName, TenantSeed seed, String sourceCurrency, String targetCurrency, BigDecimal rate, String description) {
        UUID rateId = deterministicUuid(schemaName + ":fx-rate:" + sourceCurrency + ":" + targetCurrency);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_fx_exchange_rates (
                    id, source_currency, target_currency, rate, active, description, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, true, ?, NOW(), NOW())
                ON CONFLICT (source_currency, target_currency) DO UPDATE SET
                    rate = EXCLUDED.rate,
                    active = true,
                    description = EXCLUDED.description,
                    updated_at = NOW()
                """.formatted(schemaName),
                rateId,
                sourceCurrency,
                targetCurrency,
                rate,
                description + " - " + seed.slug()
        );
    }

    private void upsertOperationFees(String schemaName, TenantSeed seed) {
        upsertOperationFee(schemaName, seed, "TRANSFER", "FIXED", new BigDecimal("1.50"), "SEPARATE", "Tarifa por transferencia");
        upsertOperationFee(schemaName, seed, "WITHDRAWAL", "PERCENTAGE", new BigDecimal("0.50"), "SEPARATE", "Tarifa por retiro");
        upsertOperationFee(schemaName, seed, "PAYMENT", "FIXED", new BigDecimal("0.80"), "INCLUDED", "Tarifa por pago");
        upsertOperationFee(schemaName, seed, "SERVICE_PAYMENT", "FIXED", new BigDecimal("0.75"), "SEPARATE", "Tarifa por pago de servicios");
    }

    private void upsertOperationFee(
            String schemaName,
            TenantSeed seed,
            String operationCode,
            String feeType,
            BigDecimal feeValue,
            String calculationMode,
            String description
    ) {
        UUID feeId = deterministicUuid(schemaName + ":operation-fee:" + operationCode);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_operation_fees (
                    id, operation_code, fee_type, fee_value, calculation_mode, active, description, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, true, ?, NOW(), NOW())
                ON CONFLICT (operation_code) DO UPDATE SET
                    fee_type = EXCLUDED.fee_type,
                    fee_value = EXCLUDED.fee_value,
                    calculation_mode = EXCLUDED.calculation_mode,
                    active = true,
                    description = EXCLUDED.description,
                    updated_at = NOW()
                """.formatted(schemaName),
                feeId,
                operationCode,
                feeType,
                feeValue,
                calculationMode,
                description + " - " + seed.slug()
        );
    }

    private void insertLimitRule(
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            String suffix,
            String name,
            String limitType,
            String scopeType,
            String period,
            String transactionType,
            String accountType,
            String currency,
            BigDecimal maxAmount,
            Long maxCount,
            boolean review
    ) {
        String code = seed.slug().toUpperCase() + "-" + suffix;
        UUID ruleId = deterministicUuid(schemaName + ":limit:" + code);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_limit_rules (
                    id, code, name, description, limit_type, scope_type, period, transaction_type, account_type,
                    currency, min_amount, max_amount, max_count, active, require_review_exceed,
                    created_by_user_id, updated_by_user_id, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?, true, ?, ?, ?, NOW(), NOW())
                ON CONFLICT (code) DO UPDATE SET
                    name = EXCLUDED.name,
                    description = EXCLUDED.description,
                    limit_type = EXCLUDED.limit_type,
                    scope_type = EXCLUDED.scope_type,
                    period = EXCLUDED.period,
                    transaction_type = EXCLUDED.transaction_type,
                    account_type = EXCLUDED.account_type,
                    currency = EXCLUDED.currency,
                    min_amount = EXCLUDED.min_amount,
                    max_amount = EXCLUDED.max_amount,
                    max_count = EXCLUDED.max_count,
                    active = true,
                    require_review_exceed = EXCLUDED.require_review_exceed,
                    updated_by_user_id = EXCLUDED.updated_by_user_id,
                    updated_at = NOW()
                """.formatted(schemaName),
                ruleId,
                code,
                name,
                "Regla demo " + name,
                limitType,
                scopeType,
                period,
                transactionType,
                accountType,
                currency,
                maxAmount,
                maxCount,
                review,
                ownerUserId,
                ownerUserId
        );
    }

    private void upsertNotifications(String schemaName, TenantSeed seed, UUID userId, UUID transactionId, String notificationKeySuffix) {
        UUID unreadId = deterministicUuid(schemaName + ":notification:" + notificationKeySuffix + ":unread");
        UUID readId = deterministicUuid(schemaName + ":notification:" + notificationKeySuffix + ":read");
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_notifications (
                    id, user_id, type, category, priority, title, body, data, image_url, action_url,
                    read_at, opened_at, archived_at, expires_at, created_at, updated_at
                )
                VALUES (?, ?, 'TRANSFER_SENT', 'TRANSACTIONS', 'HIGH', ?, ?, '{}'::jsonb, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW())
                ON CONFLICT (id) DO UPDATE SET
                    title = EXCLUDED.title,
                    body = EXCLUDED.body,
                    updated_at = NOW()
                """.formatted(schemaName),
                unreadId,
                userId,
                "Transacción demo completada",
                "Se generó la transacción demo " + transactionId
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_notifications (
                    id, user_id, type, category, priority, title, body, data, image_url, action_url,
                    read_at, opened_at, archived_at, expires_at, created_at, updated_at
                )
                VALUES (?, ?, 'SYSTEM_NOTICE', 'ACCOUNTS', 'NORMAL', ?, ?, '{}'::jsonb, NULL, NULL, NOW(), NOW(), NULL, NULL, NOW(), NOW())
                ON CONFLICT (id) DO UPDATE SET
                    title = EXCLUDED.title,
                    body = EXCLUDED.body,
                    read_at = NOW(),
                    opened_at = NOW(),
                    updated_at = NOW()
                """.formatted(schemaName),
                readId,
                userId,
                "Resumen de cuentas demo",
                "Tus cuentas demo ya están listas"
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_notification_preferences (
                    id, user_id, category, push_enabled, in_app_enabled, email_enabled, sms_enabled, created_at, updated_at
                )
                VALUES (?, ?, 'TRANSACTIONS', true, true, false, false, NOW(), NOW())
                ON CONFLICT (user_id, category) DO UPDATE SET
                    push_enabled = true,
                    in_app_enabled = true,
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":notification-pref:" + notificationKeySuffix + ":transactions"),
                userId
        );
    }

    private void upsertTenantAuditEvents(String schemaName, TenantSeed seed, UUID ownerUserId, UUID transactionId) {
        insertTenantAuditEvent(schemaName, seed, ownerUserId, "LOGIN_SUCCESS", "AUTH", ownerUserId.toString(), "Owner login demo");
        insertTenantAuditEvent(schemaName, seed, ownerUserId, "TRANSACTION_CREATED", "TRANSACTIONS", transactionId.toString(), "Demo transfer created");
    }

    private void insertTenantAuditEvent(
            String schemaName,
            TenantSeed seed,
            UUID ownerUserId,
            String eventType,
            String resourceType,
            String resourceId,
            String details
    ) {
        UUID eventId = deterministicUuid(schemaName + ":audit:" + eventType + ":" + resourceId);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_audit_events (
                    id, actor_subject, actor_id, actor_email, tenant_slug, event_type, resource_type,
                    resource_id, event_details, source, outcome, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'BOOTSTRAP', 'SUCCESS', NOW())
                ON CONFLICT (id) DO UPDATE SET
                    actor_subject = EXCLUDED.actor_subject,
                    actor_id = EXCLUDED.actor_id,
                    actor_email = EXCLUDED.actor_email,
                    tenant_slug = EXCLUDED.tenant_slug,
                    event_type = EXCLUDED.event_type,
                    resource_type = EXCLUDED.resource_type,
                    resource_id = EXCLUDED.resource_id,
                    event_details = EXCLUDED.event_details,
                    source = EXCLUDED.source,
                    outcome = EXCLUDED.outcome
                """.formatted(schemaName),
                eventId,
                ownerUserId.toString(),
                ownerUserId,
                seed.ownerEmail(),
                seed.slug(),
                eventType,
                resourceType,
                resourceId,
                details
        );
    }

    // ---- Rich demo activity: extra users, many transactions and varied audit events ----

    private static final String[] FIRST_NAMES = {
            "Mateo", "Valentina", "Santiago", "Camila", "Sebastian", "Daniela", "Nicolas", "Lucia",
            "Gabriel", "Sofia", "Emilia", "Tomas", "Renata", "Joaquin", "Antonella", "Benjamin",
            "Isabela", "Maximiliano", "Catalina", "Agustin"
    };
    private static final String[] LAST_NAMES = {
            "Gutierrez", "Ramirez", "Flores", "Castro", "Romero", "Vargas", "Suarez", "Mendez",
            "Cabrera", "Ortiz", "Delgado", "Rios", "Navarro", "Herrera", "Aguilar", "Pena",
            "Cardenas", "Ledezma", "Antezana", "Choque"
    };
    private static final int EXTRA_USERS_PER_TENANT = 6;
    private static final int BULK_TX_PER_TENANT = 54;
    private static final int BULK_AUDIT_PER_TENANT = 30;

    private record SeedAccount(UUID userId, UUID accountId, String email) {
    }

    private int stableHash(String value) {
        return Math.abs(deterministicUuid(value).hashCode());
    }

    private String emailDomain(TenantSeed seed) {
        return seed.slug().replace("-", "") + ".com";
    }

    private void seedRichActivity(String schemaName, TenantSeed seed, UUID ownerUserId,
                                  UUID ownerAccountId, UUID savingsAccountId) {
        List<SeedAccount> accounts = new ArrayList<>();
        accounts.add(new SeedAccount(ownerUserId, ownerAccountId, seed.ownerEmail()));
        accounts.add(new SeedAccount(ownerUserId, savingsAccountId, seed.ownerEmail()));

        ensureAccountSequence(schemaName, "CHECKING", "BOB", EXTRA_USERS_PER_TENANT + 2L);
        for (int i = 0; i < EXTRA_USERS_PER_TENANT; i++) {
            int h = stableHash(seed.slug() + ":extra-user:" + i);
            String first = FIRST_NAMES[h % FIRST_NAMES.length];
            String last = LAST_NAMES[(h / 7) % LAST_NAMES.length];
            String email = (first + "." + last + (i + 1)).toLowerCase(Locale.ROOT) + "@" + emailDomain(seed);
            UUID userId = upsertTenantUser(schemaName, email, first, last, "USER");

            long seq = i + 2L; // CHECKING seq 1 is used by the primary regular user
            String accountNumber = accountNumber(seed.accountPrefix(), "CHECKING", "BOB", seq);
            BigDecimal balance = BigDecimal.valueOf(300 + (h % 4200)).setScale(2, RoundingMode.HALF_UP);
            UUID accountId = upsertTenantAccount(schemaName, userId, accountNumber, "CHECKING_ACCOUNT",
                    "CHECKING", "BOB", balance, false, "Cuenta de " + first);
            accounts.add(new SeedAccount(userId, accountId, email));
        }

        seedManyTransactions(schemaName, seed, accounts);
        seedManyAuditEvents(schemaName, seed, accounts);
    }

    private void seedManyTransactions(String schemaName, TenantSeed seed, List<SeedAccount> accounts) {
        // Weighted type mix for a realistic distribution.
        String[] types = {"DEPOSIT", "WITHDRAWAL", "TRANSFER", "PAYMENT", "DEPOSIT",
                "WITHDRAWAL", "TRANSFER", "PAYMENT", "FEE", "DEPOSIT", "TRANSFER", "PAYMENT"};
        for (int idx = 0; idx < BULK_TX_PER_TENANT; idx++) {
            int h = stableHash(seed.slug() + ":bulk-tx:" + idx);
            String type = types[h % types.length];
            String status = (idx % 11 == 0) ? "FAILED" : (idx % 19 == 0) ? "REVERSED" : "COMPLETED";
            int daysAgo = (h % 150) + 1;
            int hoursAgo = (h / 11) % 24;
            BigDecimal amount = BigDecimal.valueOf(20 + (h % 498000) / 100.0).setScale(2, RoundingMode.HALF_UP);

            SeedAccount a = accounts.get(h % accounts.size());
            SeedAccount b = accounts.get((h / 13 + 1) % accounts.size());
            if (b.accountId().equals(a.accountId())) {
                b = accounts.get((h / 13 + 2) % accounts.size());
            }

            UUID source;
            UUID target;
            String channel;
            switch (type) {
                case "DEPOSIT" -> { source = null; target = a.accountId(); channel = "CASHBOX"; }
                case "TRANSFER" -> { source = a.accountId(); target = b.accountId(); channel = "MANUAL"; }
                case "PAYMENT" -> { source = a.accountId(); target = null; channel = "API"; }
                case "FEE" -> { source = a.accountId(); target = null; channel = "SYSTEM"; }
                default -> { source = a.accountId(); target = null; channel = "CASHBOX"; } // WITHDRAWAL
            }
            UUID requestedBy = source != null ? a.userId() : a.userId();

            UUID txId = deterministicUuid(schemaName + ":bulk-tx:" + idx);
            String idemKey = "sample-bulk:" + seed.slug() + ":" + idx;
            String failureReason = "FAILED".equals(status) ? "Fondos insuficientes" : null;

            try {
                jdbcTemplate.update(
                        """
                        INSERT INTO %s.tenant_transactions (
                            id, type, status, channel, amount, currency, source_account_id, target_account_id,
                            external_reference, idempotency_key, description, failure_reason, metadata,
                            parent_transaction_id, reversed_transaction_id, requested_by_user_id, approved_by_user_id,
                            processed_at, created_at, updated_at
                        )
                        VALUES (
                            ?, ?, ?, ?, ?, 'BOB', ?, ?,
                            ?, ?, ?, ?, '{}'::jsonb,
                            NULL, NULL, ?, ?,
                            NOW() - make_interval(days => ?, hours => ?),
                            NOW() - make_interval(days => ?, hours => ?),
                            NOW() - make_interval(days => ?, hours => ?)
                        )
                        ON CONFLICT (requested_by_user_id, idempotency_key) DO NOTHING
                        """.formatted(schemaName),
                        txId, type, status, channel, amount, source, target,
                        "SEED-" + seed.slug().toUpperCase() + "-" + idx, idemKey,
                        demoTransactionDescription(type), failureReason,
                        requestedBy, requestedBy,
                        daysAgo, hoursAgo, daysAgo, hoursAgo, daysAgo, hoursAgo);

                if ("COMPLETED".equals(status)) {
                    BigDecimal base = BigDecimal.valueOf(amount.doubleValue() + (h % 6000))
                            .setScale(2, RoundingMode.HALF_UP);
                    if (source != null) {
                        insertSeedMovement(schemaName, txId, source, "DEBIT", amount,
                                base, base.subtract(amount), daysAgo, hoursAgo);
                    }
                    if (target != null) {
                        insertSeedMovement(schemaName, txId, target, "CREDIT", amount,
                                base, base.add(amount), daysAgo, hoursAgo);
                    }
                }
            } catch (Exception e) {
                logger.warn("Sample bulk transaction {} for '{}' failed: {}", idx, seed.slug(), e.getMessage());
            }
        }
    }

    private void insertSeedMovement(String schemaName, UUID transactionId, UUID accountId, String movementType,
                                    BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter,
                                    int daysAgo, int hoursAgo) {
        UUID movementId = deterministicUuid(schemaName + ":bulk-movement:" + transactionId + ":" + accountId + ":" + movementType);
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_transaction_movements (
                    id, transaction_id, account_id, movement_type, amount, currency,
                    balance_before, balance_after, description, created_at
                )
                VALUES (?, ?, ?, ?, ?, 'BOB', ?, ?, ?, NOW() - make_interval(days => ?, hours => ?))
                ON CONFLICT (id) DO NOTHING
                """.formatted(schemaName),
                movementId, transactionId, accountId, movementType, amount,
                balanceBefore.max(BigDecimal.ZERO), balanceAfter.max(BigDecimal.ZERO),
                "Movimiento demo", daysAgo, hoursAgo);
    }

    private String demoTransactionDescription(String type) {
        return switch (type) {
            case "DEPOSIT" -> "Depósito en efectivo";
            case "WITHDRAWAL" -> "Retiro de cajero";
            case "TRANSFER" -> "Transferencia entre cuentas";
            case "PAYMENT" -> "Pago de servicio";
            case "FEE" -> "Comisión de mantenimiento";
            default -> "Movimiento demo";
        };
    }

    private void seedManyAuditEvents(String schemaName, TenantSeed seed, List<SeedAccount> accounts) {
        String[][] events = {
                {"LOGIN", "USER", "SECURITY"}, {"LOGIN_SUCCESS", "USER", "SECURITY"},
                {"LOGOUT", "USER", "SECURITY"}, {"PASSWORD_CHANGED", "USER", "SECURITY"},
                {"TRANSACTION_CREATED", "TRANSACTION", "TRANSACTIONS"}, {"TRANSACTION_COMPLETED", "TRANSACTION", "TRANSACTIONS"},
                {"ACCOUNT_CREATED", "ACCOUNT", "ACCOUNTS"}, {"ACCOUNT_BLOCKED", "ACCOUNT", "ACCOUNTS"},
                {"USER_CREATED", "USER", "USERS"}, {"REPORT_EXECUTED", "REPORT", "REPORTS"},
                {"REPORT_EXPORTED", "REPORT", "REPORTS"}, {"LIMIT_RULE_CREATED", "LIMIT", "LIMITS"}
        };
        for (int idx = 0; idx < BULK_AUDIT_PER_TENANT; idx++) {
            int h = stableHash(seed.slug() + ":bulk-audit:" + idx);
            String[] ev = events[h % events.length];
            SeedAccount actor = accounts.get(h % accounts.size());
            int daysAgo = (h % 140) + 1;
            int hoursAgo = (h / 5) % 24;
            String outcome = (idx % 9 == 0) ? "FAILURE" : "SUCCESS";
            UUID eventId = deterministicUuid(schemaName + ":bulk-audit:" + idx);
            try {
                jdbcTemplate.update(
                        """
                        INSERT INTO %s.tenant_audit_events (
                            id, actor_subject, actor_id, actor_email, tenant_slug, event_type, resource_type,
                            resource_id, event_details, source, outcome, created_at
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'BOOTSTRAP', ?, NOW() - make_interval(days => ?, hours => ?))
                        ON CONFLICT (id) DO NOTHING
                        """.formatted(schemaName),
                        eventId, actor.userId().toString(), actor.userId(), actor.email(), seed.slug(),
                        ev[0], ev[1], deterministicUuid(schemaName + ":auditres:" + idx).toString(),
                        "Evento de auditoría demo", outcome, daysAgo, hoursAgo);
            } catch (Exception e) {
                logger.warn("Sample bulk audit {} for '{}' failed: {}", idx, seed.slug(), e.getMessage());
            }
        }
    }

    private String currentPeriodCode(TenantSeed seed) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return seed.slug().toUpperCase() + "-CUR-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());
    }

    private String previousPeriodCode(TenantSeed seed) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).minusMonths(1);
        return seed.slug().toUpperCase() + "-PREV-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());
    }

    private void upsertServiceEnrollment(
            String schemaName,
            UUID userId,
            String providerCode,
            String serviceCustomerCode,
            String serviceCustomerName,
            String alias
    ) {
        UUID providerId = jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM public.service_providers
                WHERE code = ?
                LIMIT 1
                """,
                UUID.class,
                providerCode
        );

        if (providerId == null) {
            logger.warn("Service provider '{}' not found. Skipping service enrollment seed.", providerCode);
            return;
        }

        String providerName = jdbcTemplate.queryForObject(
                """
                SELECT name
                FROM public.service_providers
                WHERE id = ?
                """,
                String.class,
                providerId
        );

        String providerCategory = jdbcTemplate.queryForObject(
                """
                SELECT category
                FROM public.service_providers
                WHERE id = ?
                """,
                String.class,
                providerId
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_service_enrollments (
                    id, user_id, provider_id, provider_code, provider_name, provider_category,
                    service_customer_code, service_customer_name, alias, status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE', NOW(), NOW())
                ON CONFLICT (user_id, provider_id, service_customer_code) DO UPDATE SET
                    provider_code = EXCLUDED.provider_code,
                    provider_name = EXCLUDED.provider_name,
                    provider_category = EXCLUDED.provider_category,
                    service_customer_name = EXCLUDED.service_customer_name,
                    alias = EXCLUDED.alias,
                    status = 'ACTIVE',
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":service-enrollment:" + userId + ":" + providerCode + ":" + serviceCustomerCode),
                userId,
                providerId,
                providerCode,
                providerName,
                providerCategory,
                serviceCustomerCode,
                serviceCustomerName,
                alias
        );
    }

    private void seedPublicServicePaymentsForUser(
            String schemaName,
            PlatformTenantResponse tenant,
            TenantSeed seed,
            UUID userId,
            UUID accountId,
            String accountNumber,
            String serviceCustomerCode,
            String serviceCustomerName,
            String serviceAlias,
            List<UUID> transactionIds,
            String keyPrefix,
            int userNumber
    ) {
        UUID providerId = jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM public.service_providers
                WHERE code = ?
                LIMIT 1
                """,
                UUID.class,
                seed.serviceProviderCode()
        );

        if (providerId == null) {
            logger.warn("Service provider '{}' not found. Skipping service bill seed.", seed.serviceProviderCode());
            return;
        }

        upsertServiceEnrollment(
                schemaName,
                userId,
                seed.serviceProviderCode(),
                serviceCustomerCode,
                serviceCustomerName,
                serviceAlias
        );

        UUID serviceCustomerId = upsertPublicServiceCustomer(providerId, serviceCustomerCode, serviceCustomerName);

        if ("owner".equals(keyPrefix)) {
            upsertPublicServiceBill(
                    providerId,
                    serviceCustomerId,
                    serviceCustomerCode,
                    serviceCustomerName,
                    billingPeriod(OffsetDateTime.now(ZoneOffset.UTC)),
                    servicePendingAmount(seed),
                    "PENDING",
                    LocalDate.now(ZoneOffset.UTC).plusDays(10),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        if (userNumber > 0 && userNumber <= 5) {
            int pendingBillCount = Math.min(4, transactionIds.size());
            for (int index = 0; index < pendingBillCount; index++) {
                int billIndex = index + 1;
                String billingPeriod = billingPeriod(OffsetDateTime.now(ZoneOffset.UTC).minusMonths(billIndex));
                BigDecimal pendingAmount = servicePendingAmount(seed).add(BigDecimal.valueOf(billIndex * 25L));
                LocalDate dueDate = LocalDate.now(ZoneOffset.UTC).plusDays(10L + billIndex);
                String billKeySuffix = keyPrefix + "-pending-" + String.format("%02d", billIndex);

                upsertPublicServiceBill(
                        providerId,
                        serviceCustomerId,
                        serviceCustomerCode,
                        serviceCustomerName,
                        billingPeriod,
                        pendingAmount,
                        "PENDING",
                        dueDate,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
            }
            return;
        }

        for (int index = 0; index < transactionIds.size(); index++) {
            int paymentIndex = index + 1;
            UUID transactionId = transactionIds.get(index);
            String billingPeriod = billingPeriod(OffsetDateTime.now(ZoneOffset.UTC).minusMonths(paymentIndex));
            BigDecimal paidAmount = servicePaidAmount(seed, paymentIndex, serviceCustomerCode);
            LocalDate dueDate = LocalDate.now(ZoneOffset.UTC).minusDays(5L + paymentIndex);
            String paymentKeySuffix = keyPrefix + "-" + String.format("%02d", paymentIndex);

            UUID paidBillId = upsertPublicServiceBill(
                    providerId,
                    serviceCustomerId,
                    serviceCustomerCode,
                    serviceCustomerName,
                    billingPeriod,
                    paidAmount,
                    "PAID",
                    dueDate,
                    tenant.id(),
                    tenant.slug(),
                    userId,
                    accountId,
                    accountNumber,
                    transactionId
            );

            upsertPublicServicePayment(
                    paidBillId,
                    providerId,
                    tenant.id(),
                    tenant.slug(),
                    userId,
                    accountId,
                    accountNumber,
                    transactionId,
                    paidAmount,
                    "BOB",
                    seed.slug(),
                    paymentKeySuffix
            );
        }
    }

    private void seedLoanBundleForUser(
            String schemaName,
            TenantSeed seed,
            UUID userId,
            UUID firstAccountId,
            String firstAccountNumber,
            UUID secondAccountId,
            String secondAccountNumber,
            String keyPrefix
    ) {
        seedLoanSeriesForAccount(schemaName, seed, userId, firstAccountId, firstAccountNumber, keyPrefix + "-first");
        seedLoanSeriesForAccount(schemaName, seed, userId, secondAccountId, secondAccountNumber, keyPrefix + "-second");
    }

    private void seedLoanSeriesForAccount(
            String schemaName,
            TenantSeed seed,
            UUID userId,
            UUID accountId,
            String accountNumber,
            String keyPrefix
    ) {
        for (int loanIndex = 1; loanIndex <= 4; loanIndex++) {
            boolean receivedLoan = loanIndex == 1;
            String loanKey = keyPrefix + "-loan-" + loanIndex;
            BigDecimal principal = seedLoanPrincipal(seed, keyPrefix, loanIndex);
            BigDecimal annualInterestRate = seedLoanInterestRate(seed, loanIndex);
            OffsetDateTime disbursedAt = receivedLoan ? OffsetDateTime.now(ZoneOffset.UTC).minusDays(20L + loanIndex) : null;

            UUID loanId = upsertTenantLoan(
                    schemaName,
                    userId,
                    accountId,
                    loanKey,
                    principal,
                    "BOB",
                    annualInterestRate,
                    receivedLoan ? 12 : 18,
                    receivedLoan ? "DISBURSED" : "REJECTED",
                    receivedLoan ? "Prestamo demo recibido y desembolsado" : "Prestamo demo rechazado",
                    receivedLoan ? "Solicitud aprobada para dataset demo" : "Solicitud rechazada para dataset demo",
                    disbursedAt,
                    null
            );

            if (receivedLoan) {
                seedLoanInstallments(schemaName, loanId, principal, annualInterestRate, seed.slug());
            }
        }
    }

    private UUID upsertTenantLoan(
            String schemaName,
            UUID userId,
            UUID accountId,
            String loanKey,
            BigDecimal principal,
            String currency,
            BigDecimal annualInterestRate,
            int termMonths,
            String status,
            String purpose,
            String statusReason,
            OffsetDateTime disbursedAt,
            OffsetDateTime closedAt
    ) {
        UUID loanId = deterministicUuid(schemaName + ":loan:" + loanKey);
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO %s.tenant_loans (
                    id, user_id, account_id, principal, currency, annual_interest_rate, term_months,
                    interest_method, status, purpose, status_reason, disbursed_at, closed_at, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, 'FLAT', ?, ?, ?, ?, ?, NOW(), NOW())
                ON CONFLICT (id) DO UPDATE SET
                    user_id = EXCLUDED.user_id,
                    account_id = EXCLUDED.account_id,
                    principal = EXCLUDED.principal,
                    currency = EXCLUDED.currency,
                    annual_interest_rate = EXCLUDED.annual_interest_rate,
                    term_months = EXCLUDED.term_months,
                    interest_method = EXCLUDED.interest_method,
                    status = EXCLUDED.status,
                    purpose = EXCLUDED.purpose,
                    status_reason = EXCLUDED.status_reason,
                    disbursed_at = EXCLUDED.disbursed_at,
                    closed_at = EXCLUDED.closed_at,
                    updated_at = NOW()
                RETURNING id
                """.formatted(schemaName),
                UUID.class,
                loanId,
                userId,
                accountId,
                principal,
                currency,
                annualInterestRate,
                termMonths,
                status,
                purpose,
                statusReason,
                disbursedAt,
                closedAt
        );
    }

    private void seedLoanInstallments(
            String schemaName,
            UUID loanId,
            BigDecimal principal,
            BigDecimal annualInterestRate,
            String seedSlug
    ) {
        BigDecimal principalPerInstallment = principal.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
        BigDecimal interestTotal = principal.multiply(annualInterestRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal interestPerInstallment = interestTotal.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        for (int installmentNumber = 1; installmentNumber <= 4; installmentNumber++) {
            LocalDate dueDate = today.minusMonths(4L - installmentNumber);
            BigDecimal totalDue = principalPerInstallment.add(interestPerInstallment).setScale(2, RoundingMode.HALF_UP);
            String status = dueDate.isBefore(today) ? "OVERDUE" : "PENDING";
            upsertLoanInstallment(
                    schemaName,
                    loanId,
                    installmentNumber,
                    dueDate,
                    principalPerInstallment,
                    interestPerInstallment,
                    totalDue,
                    BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                    status,
                    seedSlug
            );
        }
    }

    private UUID upsertLoanInstallment(
            String schemaName,
            UUID loanId,
            int installmentNumber,
            LocalDate dueDate,
            BigDecimal principalDue,
            BigDecimal interestDue,
            BigDecimal totalDue,
            BigDecimal paidAmount,
            String status,
            String seedSlug
    ) {
        UUID installmentId = deterministicUuid(schemaName + ":loan-installment:" + seedSlug + ":" + loanId + ":" + installmentNumber);
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO %s.tenant_loan_installments (
                    id, loan_id, number, due_date, principal_due, interest_due, total_due, paid_amount,
                    status, paid_at, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NOW(), NOW())
                ON CONFLICT (loan_id, number) DO UPDATE SET
                    due_date = EXCLUDED.due_date,
                    principal_due = EXCLUDED.principal_due,
                    interest_due = EXCLUDED.interest_due,
                    total_due = EXCLUDED.total_due,
                    paid_amount = EXCLUDED.paid_amount,
                    status = EXCLUDED.status,
                    updated_at = NOW()
                RETURNING id
                """.formatted(schemaName),
                UUID.class,
                installmentId,
                loanId,
                installmentNumber,
                dueDate,
                principalDue,
                interestDue,
                totalDue,
                paidAmount,
                status
        );
    }

    private BigDecimal seedLoanPrincipal(TenantSeed seed, String keyPrefix, int loanIndex) {
        int variant = Math.abs((seed.slug() + ":" + keyPrefix + ":" + loanIndex).hashCode() % 8);
        return BigDecimal.valueOf(800L + (loanIndex * 150L) + (variant * 25L)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal seedLoanInterestRate(TenantSeed seed, int loanIndex) {
        int variant = Math.abs((seed.slug() + ":loan-rate:" + loanIndex).hashCode() % 6);
        return BigDecimal.valueOf(12L + loanIndex + variant).setScale(2, RoundingMode.HALF_UP);
    }

    private UUID upsertPublicServiceCustomer(UUID providerId, String serviceCustomerCode, String customerName) {
        UUID customerId = deterministicUuid("public:service-customer:" + providerId + ":" + serviceCustomerCode);
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO public.service_customers (
                    id, provider_id, service_customer_code, customer_name, status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, 'ACTIVE', NOW(), NOW())
                ON CONFLICT (provider_id, service_customer_code) DO UPDATE SET
                    customer_name = EXCLUDED.customer_name,
                    status = 'ACTIVE',
                    updated_at = NOW()
                RETURNING id
                """,
                UUID.class,
                customerId,
                providerId,
                serviceCustomerCode,
                customerName
        );
    }

    private UUID upsertPublicServiceBill(
            UUID providerId,
            UUID serviceCustomerId,
            String serviceCustomerCode,
            String customerName,
            String billingPeriod,
            BigDecimal amount,
            String status,
            LocalDate dueDate,
            UUID paidByTenantId,
            String paidByTenantSlug,
            UUID paidByUserId,
            UUID paidByAccountId,
            String paidByAccountNumber,
            UUID paidTransactionId
    ) {
        UUID billId = deterministicUuid("public:service-bill:" + providerId + ":" + serviceCustomerCode + ":" + billingPeriod);
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO public.service_bills (
                    id, provider_id, service_customer_id, service_customer_code, customer_name, billing_period,
                    amount, currency, due_date, status, paid_by_tenant_id, paid_by_tenant_slug, paid_by_user_id,
                    paid_by_account_id, paid_by_account_number, paid_transaction_id, paid_at, created_by_superadmin_id,
                    created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, 'BOB', ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NOW(), NOW())
                ON CONFLICT (provider_id, service_customer_code, billing_period) DO UPDATE SET
                    service_customer_id = EXCLUDED.service_customer_id,
                    customer_name = EXCLUDED.customer_name,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    due_date = EXCLUDED.due_date,
                    status = EXCLUDED.status,
                    paid_by_tenant_id = EXCLUDED.paid_by_tenant_id,
                    paid_by_tenant_slug = EXCLUDED.paid_by_tenant_slug,
                    paid_by_user_id = EXCLUDED.paid_by_user_id,
                    paid_by_account_id = EXCLUDED.paid_by_account_id,
                    paid_by_account_number = EXCLUDED.paid_by_account_number,
                    paid_transaction_id = EXCLUDED.paid_transaction_id,
                    paid_at = EXCLUDED.paid_at,
                    updated_at = NOW()
                RETURNING id
                """,
                UUID.class,
                billId,
                providerId,
                serviceCustomerId,
                serviceCustomerCode,
                customerName,
                billingPeriod,
                amount,
                dueDate,
                status,
                paidByTenantId,
                paidByTenantSlug,
                paidByUserId,
                paidByAccountId,
                paidByAccountNumber,
                paidTransactionId,
                status.equalsIgnoreCase("PAID") ? OffsetDateTime.now(ZoneOffset.UTC) : null
        );
    }

    private void upsertPublicServicePayment(
            UUID billId,
            UUID providerId,
            UUID paidByTenantId,
            String paidByTenantSlug,
            UUID paidByUserId,
            UUID paidByAccountId,
            String paidByAccountNumber,
            UUID paidTransactionId,
            BigDecimal amount,
            String currency,
            String seedSlug,
            String paymentKeySuffix
    ) {
        UUID paymentId = deterministicUuid("public:service-payment:" + billId);
        String idempotencyKey = DEMO_IDEMPOTENCY_PREFIX + ":" + seedSlug + ":service-payment:" + paymentKeySuffix;
        String receiptNumber = "SP-SEED-" + seedSlug.toUpperCase() + "-" + paymentKeySuffix.toUpperCase().replaceAll("[^A-Z0-9]+", "-");

        jdbcTemplate.update(
                """
                INSERT INTO public.service_bill_payments (
                    id, bill_id, provider_id, paid_by_tenant_id, paid_by_tenant_slug, paid_by_user_id,
                    paid_by_account_id, paid_by_account_number, paid_transaction_id, amount, currency,
                    receipt_number, idempotency_key, status, paid_at, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PAID', NOW(), NOW())
                ON CONFLICT (bill_id) DO UPDATE SET
                    provider_id = EXCLUDED.provider_id,
                    paid_by_tenant_id = EXCLUDED.paid_by_tenant_id,
                    paid_by_tenant_slug = EXCLUDED.paid_by_tenant_slug,
                    paid_by_user_id = EXCLUDED.paid_by_user_id,
                    paid_by_account_id = EXCLUDED.paid_by_account_id,
                    paid_by_account_number = EXCLUDED.paid_by_account_number,
                    paid_transaction_id = EXCLUDED.paid_transaction_id,
                    amount = EXCLUDED.amount,
                    currency = EXCLUDED.currency,
                    receipt_number = EXCLUDED.receipt_number,
                    idempotency_key = EXCLUDED.idempotency_key,
                    status = 'PAID',
                    paid_at = NOW()
                """,
                paymentId,
                billId,
                providerId,
                paidByTenantId,
                paidByTenantSlug,
                paidByUserId,
                paidByAccountId,
                paidByAccountNumber,
                paidTransactionId,
                amount,
                currency,
                receiptNumber,
                idempotencyKey
        );
    }

    private BigDecimal servicePendingAmount(TenantSeed seed) {
        int variant = Math.abs(seed.slug().hashCode() % 7);
        return BigDecimal.valueOf(39L + variant).setScale(2);
    }

    private BigDecimal servicePaidAmount(TenantSeed seed, int paymentIndex, String serviceCustomerCode) {
        int variant = Math.abs((seed.slug() + ":" + serviceCustomerCode + ":" + paymentIndex).hashCode() % 9);
        return BigDecimal.valueOf(24L + paymentIndex + variant).setScale(2);
    }

    private String tenantServiceCustomerCode(TenantSeed seed, int userNumber) {
        return seed.serviceCustomerCode() + "-U" + String.format("%02d", userNumber);
    }

    private String tenantServiceCustomerName(TenantSeed seed, String firstName, int userNumber) {
        return seed.serviceCustomerName() + " - " + firstName + " " + String.format("%02d", userNumber);
    }

    private String tenantServiceAlias(TenantSeed seed, String firstName, int userNumber) {
        return seed.serviceAlias() + " - " + firstName + " " + String.format("%02d", userNumber);
    }

    private String billingPeriod(OffsetDateTime dateTime) {
        return dateTime.getYear() + "-" + String.format("%02d", dateTime.getMonthValue());
    }

    private void ensureAccountSequence(String schemaName, String accountType, String currency, long currentValue) {
        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_account_sequences (
                    id, account_type, currency, current_value, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, NOW(), NOW())
                ON CONFLICT (account_type, currency) DO UPDATE SET
                    current_value = GREATEST(tenant_account_sequences.current_value, EXCLUDED.current_value),
                    updated_at = NOW()
                """.formatted(schemaName),
                deterministicUuid(schemaName + ":sequence:" + accountType + ":" + currency),
                accountType,
                currency,
                currentValue
        );
    }

    private UUID fetchRequiredRoleId(String schemaName, String roleName) {
        UUID roleId = jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM %s.tenant_roles
                WHERE name = ?
                LIMIT 1
                """.formatted(schemaName),
                UUID.class,
                roleName
        );

        if (roleId == null) {
            throw new IllegalStateException("Required role '" + roleName + "' was not found in schema '" + schemaName + "'");
        }

        return roleId;
    }

    private String accountNumber(String tenantPrefix, String accountType, String currency, long sequence) {
        return "%s-%s-%s-%06d".formatted(
                tenantPrefix,
                accountShortCode(accountType),
                currency,
                sequence
        );
    }

    private String accountShortCode(String accountType) {
        return switch (accountType) {
            case "WALLET" -> "WAL";
            case "SAVINGS" -> "SAV";
            case "CHECKING" -> "CHK";
            case "CREDIT_CARD" -> "CCD";
            case "PREPAID_CARD" -> "PPD";
            case "LOAN" -> "LOA";
            default -> "ACC";
        };
    }

    private UUID deterministicUuid(String seedValue) {
        return UUID.nameUUIDFromBytes(seedValue.getBytes(StandardCharsets.UTF_8));
    }

    private List<TenantSeed> sampleTenants() {
        return List.of(
                new TenantSeed("tenant1", "tenant1", "ENTERPRISE", "tenant1@gmail.com", "tenant1", "owner", "user1@gmail.com", "user1", "tenant1", "TEN1", "ELECTRICITY_CRE", "tenant1-svc", "tenant1 customer", "tenant1 alias", new BigDecimal("1200.00"), new BigDecimal("420.00")),
                new TenantSeed("tenant2", "tenant2", "ENTERPRISE", "tenant2@gmail.com", "tenant2", "owner", "user1@gmail.com", "user1", "tenant2", "TEN2", "INTERNET_ENTEL", "tenant2-svc", "tenant2 customer", "tenant2 alias", new BigDecimal("980.00"), new BigDecimal("305.00")),
                new TenantSeed("tenant3", "tenant3", "ENTERPRISE", "tenant3@gmail.com", "tenant3", "owner", "user1@gmail.com", "user1", "tenant3", "TEN3", "WATER_SAGUAPAC", "tenant3-svc", "tenant3 customer", "tenant3 alias", new BigDecimal("1500.00"), new BigDecimal("260.00")),
                new TenantSeed("tenant4", "tenant4", "ENTERPRISE", "tenant4@gmail.com", "tenant4", "owner", "user1@gmail.com", "user1", "tenant4", "TEN4", "TV_TIGO", "tenant4-svc", "tenant4 customer", "tenant4 alias", new BigDecimal("2100.00"), new BigDecimal("510.00")),
                new TenantSeed("tenant5", "tenant5", "ENTERPRISE", "tenant5@gmail.com", "tenant5", "owner", "user1@gmail.com", "user1", "tenant5", "TEN5", "ELECTRICITY_CRE", "tenant5-svc", "tenant5 customer", "tenant5 alias", new BigDecimal("800.00"), new BigDecimal("190.00")),
                new TenantSeed("tenant6", "tenant6", "ENTERPRISE", "tenant6@gmail.com", "tenant6", "owner", "user1@gmail.com", "user1", "tenant6", "TEN6", "INTERNET_ENTEL", "tenant6-svc", "tenant6 customer", "tenant6 alias", new BigDecimal("1750.00"), new BigDecimal("380.00")),
                new TenantSeed("tenant7", "tenant7", "ENTERPRISE", "tenant7@gmail.com", "tenant7", "owner", "user1@gmail.com", "user1", "tenant7", "TEN7", "WATER_SAGUAPAC", "tenant7-svc", "tenant7 customer", "tenant7 alias", new BigDecimal("1325.00"), new BigDecimal("275.00")),
                new TenantSeed("tenant8", "tenant8", "ENTERPRISE", "tenant8@gmail.com", "tenant8", "owner", "user1@gmail.com", "user1", "tenant8", "TEN8", "TV_TIGO", "tenant8-svc", "tenant8 customer", "tenant8 alias", new BigDecimal("990.00"), new BigDecimal("215.00")),
                new TenantSeed("tenant9", "tenant9", "ENTERPRISE", "tenant9@gmail.com", "tenant9", "owner", "user1@gmail.com", "user1", "tenant9", "TEN9", "ELECTRICITY_CRE", "tenant9-svc", "tenant9 customer", "tenant9 alias", new BigDecimal("2450.00"), new BigDecimal("610.00")),
                new TenantSeed("tenant10", "tenant10", "ENTERPRISE", "tenant10@gmail.com", "tenant10", "owner", "user1@gmail.com", "user1", "tenant10", "TEN10", "INTERNET_ENTEL", "tenant10-svc", "tenant10 customer", "tenant10 alias", new BigDecimal("1110.00"), new BigDecimal("330.00"))
        );
    }

    private record TenantSeed(
            String name,
            String slug,
            String planCode,
            String ownerEmail,
            String ownerFirstName,
            String ownerLastName,
            String userEmail,
            String userFirstName,
            String userLastName,
            String accountPrefix,
            String serviceProviderCode,
            String serviceCustomerCode,
            String serviceCustomerName,
            String serviceAlias,
            BigDecimal ownerStartingBalance,
            BigDecimal savingsStartingBalance
    ) {
    }
}
