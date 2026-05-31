package com.financesystem.finance_api.modules.tenant.dashboard.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.domain.model.TenantAuditEvent;
import com.financesystem.finance_api.modules.governance.audit.domain.repository.TenantAuditEventRepository;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.Notification;
import com.financesystem.finance_api.modules.governance.notifications.domain.repository.NotificationRepository;
import com.financesystem.finance_api.modules.identity.auth.application.usecase.GetCurrentAuthenticatedTenantUserUseCase;
import com.financesystem.finance_api.modules.identity.auth.application.dto.AuthenticatedTenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance_api.modules.tenant.accounting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.AccountingPeriodRepository;
import com.financesystem.finance_api.modules.tenant.accounting.domain.repository.JournalEntryRepository;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.dashboard.application.dto.TenantOwnerDashboardResponse;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxExchangeRate;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.OperationFee;
import com.financesystem.finance_api.modules.tenant.fx.domain.repository.FxExchangeRateRepository;
import com.financesystem.finance_api.modules.tenant.fx.domain.repository.OperationFeeRepository;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRule;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRuleType;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitScopeType;
import com.financesystem.finance_api.modules.tenant.limits.domain.repository.LimitRuleRepository;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.Transaction;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionStatus;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;
import com.financesystem.finance_api.modules.tenant.transactions.domain.repository.TransactionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetTenantOwnerDashboardUseCase {

    private static final ZoneId DASHBOARD_ZONE = ZoneId.of("America/La_Paz");
    private static final String DASHBOARD_PERIOD = "MONTH";
    private static final String BASE_CURRENCY = CurrencyCode.BOB.name();
    private static final int MAX_RECENT_ITEMS = 12;
    private static final int MAX_ACTIVE_RULES = 10;

    private final SecurityContextFacade securityContextFacade;
    private final GetCurrentAuthenticatedTenantUserUseCase getCurrentAuthenticatedTenantUserUseCase;
    private final TenantUserRepository tenantUserRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LimitRuleRepository limitRuleRepository;
    private final AccountingPeriodRepository accountingPeriodRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final FxExchangeRateRepository fxExchangeRateRepository;
    private final OperationFeeRepository operationFeeRepository;
    private final TenantAuditEventRepository tenantAuditEventRepository;
    private final NotificationRepository notificationRepository;

    public GetTenantOwnerDashboardUseCase(
            SecurityContextFacade securityContextFacade,
            GetCurrentAuthenticatedTenantUserUseCase getCurrentAuthenticatedTenantUserUseCase,
            TenantUserRepository tenantUserRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            LimitRuleRepository limitRuleRepository,
            AccountingPeriodRepository accountingPeriodRepository,
            JournalEntryRepository journalEntryRepository,
            FxExchangeRateRepository fxExchangeRateRepository,
            OperationFeeRepository operationFeeRepository,
            TenantAuditEventRepository tenantAuditEventRepository,
            NotificationRepository notificationRepository
    ) {
        this.securityContextFacade = securityContextFacade;
        this.getCurrentAuthenticatedTenantUserUseCase = getCurrentAuthenticatedTenantUserUseCase;
        this.tenantUserRepository = tenantUserRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.limitRuleRepository = limitRuleRepository;
        this.accountingPeriodRepository = accountingPeriodRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.fxExchangeRateRepository = fxExchangeRateRepository;
        this.operationFeeRepository = operationFeeRepository;
        this.tenantAuditEventRepository = tenantAuditEventRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public TenantOwnerDashboardResponse execute() {
        AuthenticatedTenantUserResponse currentUser = getCurrentAuthenticatedTenantUserUseCase.execute();
        ensureTenantOwner(currentUser.roles());
        String tenantSlug = requireTenantSlug();
        OffsetDateTime generatedAt = OffsetDateTime.now(DASHBOARD_ZONE);
        PeriodWindow currentWindow = buildCurrentWindow(generatedAt);
        PeriodWindow previousWindow = currentWindow.shiftMonths(-1);

        List<TenantUser> users = tenantUserRepository.findAll();
        List<AccountOwnerView> accounts = accountRepository.findAllViews();
        List<Transaction> transactions = transactionRepository.findAll();
        List<LimitRule> limitRules = limitRuleRepository.findAll();
        List<AccountingPeriod> periods = accountingPeriodRepository.findAll();
        List<JournalEntry> journalEntries = journalEntryRepository.findAll();
        List<FxExchangeRate> fxExchangeRates = fxExchangeRateRepository.findAll();
        List<OperationFee> operationFees = operationFeeRepository.findAll();
        List<TenantAuditEvent> recentAuditEvents = tenantAuditEventRepository.findRecent(MAX_RECENT_ITEMS);
        List<Notification> recentNotifications = notificationRepository.findInboxByUserIdOrderByCreatedAtDesc(currentUser.id())
                .stream()
                .limit(MAX_RECENT_ITEMS)
                .toList();
        long unreadNotifications = notificationRepository.countUnreadInboxByUserId(currentUser.id());

        Map<String, BigDecimal> fxRates = buildExchangeRateMap(fxExchangeRates);
        AtomicBoolean partialData = new AtomicBoolean(false);
        String baseCurrency = BASE_CURRENCY;

        BigDecimal totalAccountBalance = sumAccountBalances(accounts, baseCurrency, fxRates, partialData);
        BigDecimal totalTransactionAmount = sumTransactionAmounts(transactions, baseCurrency, fxRates, partialData);

        List<DashboardOwnerAccountView> pendingApproval = buildFilteredAccounts(accounts, List.of(AccountStatus.PENDING_APPROVAL), MAX_RECENT_ITEMS);
        List<DashboardOwnerAccountView> blockedOrFrozen = buildFilteredAccounts(accounts, List.of(AccountStatus.BLOCKED, AccountStatus.FROZEN), MAX_RECENT_ITEMS);
        List<TenantOwnerDashboardResponse.CountItem> accountStatusItems = countByAccountStatus(accounts);
        List<TenantOwnerDashboardResponse.CountItem> accountTypeItems = countByAccountType(accounts);
        List<TenantOwnerDashboardResponse.BalanceItem> accountCurrencyItems = buildAccountCurrencyItems(accounts, partialData);
        List<TenantOwnerDashboardResponse.DailyCountPoint> dailyVolumes = buildDailyTransactionVolumes(transactions, currentWindow);
        List<TenantOwnerDashboardResponse.DailyMoneyPoint> dailyAmounts = buildDailyTransactionAmounts(transactions, currentWindow, baseCurrency, fxRates, partialData);
        List<TenantOwnerDashboardResponse.TransactionItem> recentTransactionItems = buildRecentTransactions(transactions, accounts, baseCurrency, fxRates, partialData);
        List<TenantOwnerDashboardResponse.CountItem> transactionStatusItems = countByTransactionStatus(transactions);
        List<TenantOwnerDashboardResponse.CountItem> transactionTypeItems = countByTransactionType(transactions);
        List<TenantOwnerDashboardResponse.CountItem> limitTypeItems = countByLimitType(limitRules);
        List<TenantOwnerDashboardResponse.CountItem> limitScopeItems = countByLimitScope(limitRules);
        List<TenantOwnerDashboardResponse.LimitRuleItem> activeLimitRules = buildActiveLimitRules(limitRules, MAX_ACTIVE_RULES);
        List<TenantOwnerDashboardResponse.CountItem> periodStatusItems = countByPeriodStatus(periods);
        List<TenantOwnerDashboardResponse.CountItem> periodTypeItems = countByPeriodType(periods);
        List<TenantOwnerDashboardResponse.CountItem> journalStatusItems = countByJournalStatus(journalEntries);
        List<TenantOwnerDashboardResponse.CountItem> journalTypeItems = countByJournalType(journalEntries);
        List<TenantOwnerDashboardResponse.JournalEntryItem> recentJournalEntries = buildRecentJournalEntries(journalEntries);
        List<TenantOwnerDashboardResponse.FxRateItem> exchangeRateItems = buildExchangeRateItems(fxExchangeRates);
        List<TenantOwnerDashboardResponse.OperationFeeItem> operationFeeItems = buildOperationFeeItems(operationFees);
        List<TenantAuditEvent> auditEvents = recentAuditEvents;

        long usersTotal = users.size();
        long usersActive = users.stream().filter(TenantUser::active).count();
        long usersInactive = usersTotal - usersActive;

        long accountsTotal = accounts.size();
        long accountsActive = accountStatusCount(accounts, AccountStatus.ACTIVE);
        long accountsBlocked = accountStatusCount(accounts, AccountStatus.BLOCKED);
        long accountsFrozen = accountStatusCount(accounts, AccountStatus.FROZEN);
        long accountsPendingApproval = accountStatusCount(accounts, AccountStatus.PENDING_APPROVAL);

        long transactionsTotal = transactions.size();
        long transactionsCompleted = transactionStatusCount(transactions, TransactionStatus.COMPLETED);
        long transactionsPending = transactionStatusCount(transactions, TransactionStatus.PENDING)
                + transactionStatusCount(transactions, TransactionStatus.PENDING_REVIEW)
                + transactionStatusCount(transactions, TransactionStatus.PROCESSING)
                + transactionStatusCount(transactions, TransactionStatus.AUTHORIZED);
        long transactionsFailed = transactionStatusCount(transactions, TransactionStatus.FAILED)
                + transactionStatusCount(transactions, TransactionStatus.CANCELLED)
                + transactionStatusCount(transactions, TransactionStatus.EXPIRED);
        long transactionsReversed = transactionStatusCount(transactions, TransactionStatus.REVERSED)
                + transactionStatusCount(transactions, TransactionStatus.PARTIALLY_REFUNDED);

        long limitsTotal = limitRules.size();
        long limitsActive = limitRules.stream().filter(LimitRule::active).count();
        long limitsReviewRequired = limitRules.stream().filter(rule -> rule.active() && rule.requireReviewExceed()).count();

        long openPeriods = periods.stream().filter(period -> period.status() == AccountingPeriodStatus.OPEN).count();
        long closedPeriods = periods.stream().filter(period -> period.status() == AccountingPeriodStatus.CLOSED).count();

        long journalEntryCount = journalEntries.size();
        long auditEventCount = tenantAuditEventRepository.count();

        TenantOwnerDashboardResponse.ComparisonSummary comparisonSummary = buildComparisonSummary(
                accounts,
                transactions,
                baseCurrency,
                fxRates,
                currentWindow,
                previousWindow,
                partialData
        );

        List<TenantOwnerDashboardResponse.AlertItem> alerts = buildAlerts(
                accountsPendingApproval,
                accountsBlocked + accountsFrozen,
                transactionsFailed,
                limitsReviewRequired,
                openPeriods,
                unreadNotifications,
                partialData.get()
        );

        List<TenantOwnerDashboardResponse.InsightItem> insights = buildInsights(
                accounts,
                transactions,
                limitsReviewRequired,
                openPeriods,
                baseCurrency,
                fxRates,
                partialData
        );

        List<TenantOwnerDashboardResponse.ActivityItem> recentActivity = buildRecentActivity(
                transactions,
                recentJournalEntries,
                auditEvents,
                recentNotifications,
                accounts,
                baseCurrency,
                fxRates,
                partialData
        );

        String dataCompleteness = partialData.get() ? "PARTIAL" : "COMPLETE";
        String generatedBy = resolveDisplayName(currentUser);

        return new TenantOwnerDashboardResponse(
                new TenantOwnerDashboardResponse.Metadata(
                        generatedAt,
                        DASHBOARD_ZONE.getId(),
                        tenantSlug,
                        generatedBy,
                        baseCurrency,
                        dataCompleteness
                ),
                new TenantOwnerDashboardResponse.Filters(
                        DASHBOARD_PERIOD,
                        generatedAt.getYear(),
                        generatedAt.getMonthValue(),
                        null,
                        currentWindow.from(),
                        currentWindow.to(),
                        baseCurrency
                ),
                new TenantOwnerDashboardResponse.Comparisons(
                        new TenantOwnerDashboardResponse.TimeRange(previousWindow.from(), previousWindow.to()),
                        comparisonSummary
                ),
                new TenantOwnerDashboardResponse.Summary(
                        new TenantOwnerDashboardResponse.UsersSummary(usersTotal, usersActive, usersInactive),
                        new TenantOwnerDashboardResponse.AccountsSummary(
                                accountsTotal,
                                accountsActive,
                                accountsBlocked,
                                accountsFrozen,
                                accountsPendingApproval,
                                money(totalAccountBalance, baseCurrency)
                        ),
                        new TenantOwnerDashboardResponse.TransactionsSummary(
                                transactionsTotal,
                                transactionsCompleted,
                                transactionsPending,
                                transactionsFailed,
                                transactionsReversed,
                                money(totalTransactionAmount, baseCurrency)
                        ),
                        new TenantOwnerDashboardResponse.LimitsSummary(limitsTotal, limitsActive, limitsReviewRequired),
                        new TenantOwnerDashboardResponse.AccountingSummary(openPeriods, closedPeriods, journalEntryCount),
                        new TenantOwnerDashboardResponse.NotificationsSummary(unreadNotifications),
                        new TenantOwnerDashboardResponse.AuditSummary(auditEventCount)
                ),
                new TenantOwnerDashboardResponse.AccountsSection(
                        accountStatusItems,
                        accountTypeItems,
                        accountCurrencyItems,
                        new TenantOwnerDashboardResponse.SectionBucket(
                                pendingApproval.size(),
                                pendingApproval.stream().map(this::toAccountItem).toList()
                        ),
                        new TenantOwnerDashboardResponse.SectionBucket(
                                blockedOrFrozen.size(),
                                blockedOrFrozen.stream().map(this::toAccountItem).toList()
                        )
                ),
                new TenantOwnerDashboardResponse.TransactionsSection(
                        transactionStatusItems,
                        transactionTypeItems,
                        dailyVolumes,
                        dailyAmounts,
                        recentTransactionItems
                ),
                new TenantOwnerDashboardResponse.LimitsSection(
                        limitTypeItems,
                        limitScopeItems,
                        activeLimitRules
                ),
                new TenantOwnerDashboardResponse.AccountingSection(
                        periodStatusItems,
                        periodTypeItems,
                        journalStatusItems,
                        journalTypeItems,
                        recentJournalEntries
                ),
                new TenantOwnerDashboardResponse.FxSection(
                        exchangeRateItems,
                        operationFeeItems
                ),
                alerts,
                insights,
                recentActivity
        );
    }

    private void ensureTenantOwner(List<String> roles) {
        if (roles == null || roles.stream().noneMatch(role -> "OWNER_ADMIN".equalsIgnoreCase(role))) {
            throw new AccessDeniedException("Tenant owner dashboard requires OWNER_ADMIN role");
        }
    }

    private String requireTenantSlug() {
        String tenantSlug = securityContextFacade.getCurrentTenantSlug();
        if (tenantSlug == null || tenantSlug.isBlank()) {
            return TenantContextHolder.getRequired().tenantSlug();
        }
        return tenantSlug;
    }

    private PeriodWindow buildCurrentWindow(OffsetDateTime generatedAt) {
        OffsetDateTime from = generatedAt.toLocalDate()
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay()
                .atZone(DASHBOARD_ZONE)
                .toOffsetDateTime();
        return new PeriodWindow(from, generatedAt);
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByAccountStatus(List<AccountOwnerView> accounts) {
        return countItems(
                accounts.stream().collect(Collectors.groupingBy(account -> account.status().name(), Collectors.counting())),
                accountStatusLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByAccountType(List<AccountOwnerView> accounts) {
        return countItems(
                accounts.stream().collect(Collectors.groupingBy(account -> account.accountType().name(), Collectors.counting())),
                accountTypeLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.BalanceItem> buildAccountCurrencyItems(
            List<AccountOwnerView> accounts,
            AtomicBoolean partialData
    ) {
        Map<String, List<AccountOwnerView>> grouped = accounts.stream()
                .collect(Collectors.groupingBy(account -> account.currency().name()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    String currency = entry.getKey();
                    List<AccountOwnerView> currencyAccounts = entry.getValue();
                    BigDecimal balance = currencyAccounts.stream()
                            .map(this::toAccountRawBalance)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (balance == null) {
                        partialData.set(true);
                        balance = BigDecimal.ZERO;
                    }
                    return new TenantOwnerDashboardResponse.BalanceItem(
                            currency,
                            humanize(currency),
                            currencyAccounts.size(),
                            money(balance, currency)
                    );
                })
                .toList();
    }

    private List<DashboardOwnerAccountView> buildFilteredAccounts(
            List<AccountOwnerView> accounts,
            List<AccountStatus> statuses,
            int limit
    ) {
        Set<AccountStatus> statusSet = new HashSet<>(statuses);
        return accounts.stream()
                .filter(account -> statusSet.contains(account.status()))
                .sorted(Comparator.comparing(AccountOwnerView::createdAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(limit)
                .map(account -> new DashboardOwnerAccountView(
                        account,
                        money(toAccountRawBalance(account), account.currency().name())
                ))
                .toList();
    }

    private List<TenantOwnerDashboardResponse.DailyCountPoint> buildDailyTransactionVolumes(
            List<Transaction> transactions,
            PeriodWindow currentWindow
    ) {
        Map<LocalDate, Long> countsByDate = transactions.stream()
                .map(transaction -> effectiveTransactionTimestamp(transaction).atZone(DASHBOARD_ZONE).toLocalDate())
                .filter(date -> !date.isBefore(currentWindow.from().toLocalDate()) && !date.isAfter(currentWindow.to().toLocalDate()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return eachDate(currentWindow.from().toLocalDate(), currentWindow.to().toLocalDate())
                .stream()
                .map(date -> new TenantOwnerDashboardResponse.DailyCountPoint(date, countsByDate.getOrDefault(date, 0L)))
                .toList();
    }

    private List<TenantOwnerDashboardResponse.DailyMoneyPoint> buildDailyTransactionAmounts(
            List<Transaction> transactions,
            PeriodWindow currentWindow,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        Map<LocalDate, BigDecimal> amountsByDate = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (!isInWindow(effectiveTransactionTimestamp(transaction), currentWindow)) {
                continue;
            }

            LocalDate date = effectiveTransactionTimestamp(transaction).atZone(DASHBOARD_ZONE).toLocalDate();
            BigDecimal convertedAmount = convertToBaseCurrency(
                    transaction.amount(),
                    transaction.currency(),
                    baseCurrency,
                    fxRates,
                    partialData
            );
            amountsByDate.merge(date, convertedAmount, BigDecimal::add);
        }

        return eachDate(currentWindow.from().toLocalDate(), currentWindow.to().toLocalDate())
                .stream()
                .map(date -> new TenantOwnerDashboardResponse.DailyMoneyPoint(
                        date,
                        money(amountsByDate.getOrDefault(date, BigDecimal.ZERO), baseCurrency)
                ))
                .toList();
    }

    private List<TenantOwnerDashboardResponse.TransactionItem> buildRecentTransactions(
            List<Transaction> transactions,
            List<AccountOwnerView> accounts,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        Map<String, String> accountNumbersById = accounts.stream()
                .collect(Collectors.toMap(account -> account.id().toString(), AccountOwnerView::accountNumber, (left, right) -> left));

        return transactions.stream()
                .sorted(Comparator.comparing(this::effectiveTransactionTimestamp).reversed())
                .limit(MAX_RECENT_ITEMS)
                .map(transaction -> new TenantOwnerDashboardResponse.TransactionItem(
                        transaction.id().toString(),
                        transaction.type().name(),
                        transaction.status().name(),
                        money(convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData), baseCurrency),
                        transaction.currency(),
                        transaction.sourceAccountId() != null ? accountNumbersById.get(transaction.sourceAccountId().toString()) : null,
                        transaction.targetAccountId() != null ? accountNumbersById.get(transaction.targetAccountId().toString()) : null,
                        safeText(transaction.description()),
                        toDashboardTime(transaction.processedAt()),
                        toDashboardTime(transaction.createdAt())
                ))
                .toList();
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByTransactionStatus(List<Transaction> transactions) {
        return countItems(
                transactions.stream().collect(Collectors.groupingBy(transaction -> transaction.status().name(), Collectors.counting())),
                transactionStatusLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByTransactionType(List<Transaction> transactions) {
        return countItems(
                transactions.stream().collect(Collectors.groupingBy(transaction -> transaction.type().name(), Collectors.counting())),
                transactionTypeLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByLimitType(List<LimitRule> limitRules) {
        return countItems(
                limitRules.stream().collect(Collectors.groupingBy(rule -> rule.limitType().name(), Collectors.counting())),
                limitTypeLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByLimitScope(List<LimitRule> limitRules) {
        return countItems(
                limitRules.stream().collect(Collectors.groupingBy(rule -> rule.scopeType().name(), Collectors.counting())),
                limitScopeLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.LimitRuleItem> buildActiveLimitRules(List<LimitRule> limitRules, int limit) {
        return limitRules.stream()
                .filter(LimitRule::active)
                .sorted(Comparator.comparing(LimitRule::createdAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(limit)
                .map(rule -> new TenantOwnerDashboardResponse.LimitRuleItem(
                        rule.id().toString(),
                        safeText(rule.name()),
                        rule.limitType().name(),
                        rule.scopeType().name(),
                        rule.period() != null ? rule.period().name() : null,
                        rule.transactionType() != null ? rule.transactionType().name() : null,
                        rule.accountType() != null ? rule.accountType().name() : null,
                        rule.currency() != null ? rule.currency().name() : null,
                        rule.minAmount(),
                        rule.maxAmount(),
                        rule.maxCount(),
                        rule.requireReviewExceed(),
                        rule.active()
                ))
                .toList();
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByPeriodStatus(List<AccountingPeriod> periods) {
        return countItems(
                periods.stream().collect(Collectors.groupingBy(period -> period.status().name(), Collectors.counting())),
                periodStatusLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByPeriodType(List<AccountingPeriod> periods) {
        return countItems(
                periods.stream().collect(Collectors.groupingBy(period -> period.periodType().name(), Collectors.counting())),
                periodTypeLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByJournalStatus(List<JournalEntry> journalEntries) {
        return countItems(
                journalEntries.stream().collect(Collectors.groupingBy(entry -> entry.status().name(), Collectors.counting())),
                journalStatusLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.CountItem> countByJournalType(List<JournalEntry> journalEntries) {
        return countItems(
                journalEntries.stream().collect(Collectors.groupingBy(entry -> entry.entryType().name(), Collectors.counting())),
                journalTypeLabelMapper()
        );
    }

    private List<TenantOwnerDashboardResponse.JournalEntryItem> buildRecentJournalEntries(List<JournalEntry> journalEntries) {
        return journalEntries.stream()
                .sorted(Comparator.comparing(this::journalEntryTimestamp).reversed())
                .limit(MAX_RECENT_ITEMS)
                .map(entry -> new TenantOwnerDashboardResponse.JournalEntryItem(
                        entry.id().toString(),
                        safeText(entry.entryNumber()),
                        entry.status().name(),
                        entry.entryType().name(),
                        safeText(entry.description()),
                        entry.totalDebits(),
                        entry.totalCredits(),
                        toDashboardTime(entry.postedAt()),
                        toDashboardTime(entry.createdAt())
                ))
                .toList();
    }

    private List<TenantOwnerDashboardResponse.FxRateItem> buildExchangeRateItems(List<FxExchangeRate> fxExchangeRates) {
        return fxExchangeRates.stream()
                .sorted(Comparator.comparing(FxExchangeRate::updatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(rate -> new TenantOwnerDashboardResponse.FxRateItem(
                        rate.id().toString(),
                        rate.sourceCurrency().name(),
                        rate.targetCurrency().name(),
                        rate.rate(),
                        rate.active(),
                        safeText(rate.description()),
                        toDashboardTime(rate.createdAt()),
                        toDashboardTime(rate.updatedAt())
                ))
                .toList();
    }

    private List<TenantOwnerDashboardResponse.OperationFeeItem> buildOperationFeeItems(List<OperationFee> operationFees) {
        return operationFees.stream()
                .sorted(Comparator.comparing(OperationFee::updatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(fee -> new TenantOwnerDashboardResponse.OperationFeeItem(
                        fee.id().toString(),
                        fee.operationCode().name(),
                        fee.feeType().name(),
                        fee.feeValue(),
                        fee.calculationMode().name(),
                        fee.active(),
                        safeText(fee.description()),
                        toDashboardTime(fee.createdAt()),
                        toDashboardTime(fee.updatedAt())
                ))
                .toList();
    }

    private TenantOwnerDashboardResponse.ComparisonSummary buildComparisonSummary(
            List<AccountOwnerView> accounts,
            List<Transaction> transactions,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            PeriodWindow currentWindow,
            PeriodWindow previousWindow,
            AtomicBoolean partialData
    ) {
        long currentAccountOpenings = accounts.stream().filter(account -> isInWindow(account.createdAt(), currentWindow)).count();
        long previousAccountOpenings = accounts.stream().filter(account -> isInWindow(account.createdAt(), previousWindow)).count();

        List<Transaction> currentTransactions = transactions.stream()
                .filter(transaction -> isInWindow(effectiveTransactionTimestamp(transaction), currentWindow))
                .toList();
        List<Transaction> previousTransactions = transactions.stream()
                .filter(transaction -> isInWindow(effectiveTransactionTimestamp(transaction), previousWindow))
                .toList();

        long currentFailed = currentTransactions.stream().filter(this::isFailedTransaction).count();
        long previousFailed = previousTransactions.stream().filter(this::isFailedTransaction).count();

        BigDecimal currentTransactionAmount = currentTransactions.stream()
                .map(transaction -> convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal previousTransactionAmount = previousTransactions.stream()
                .map(transaction -> convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TenantOwnerDashboardResponse.ComparisonSummary(
                percentageChange(BigDecimal.valueOf(currentAccountOpenings), BigDecimal.valueOf(previousAccountOpenings)),
                percentageChange(BigDecimal.valueOf(currentTransactions.size()), BigDecimal.valueOf(previousTransactions.size())),
                percentageChange(currentTransactionAmount, previousTransactionAmount),
                percentageChange(BigDecimal.valueOf(currentFailed), BigDecimal.valueOf(previousFailed))
        );
    }

    private List<TenantOwnerDashboardResponse.AlertItem> buildAlerts(
            long pendingAccounts,
            long blockedOrFrozenAccounts,
            long failedTransactions,
            long reviewRequiredLimits,
            long openPeriods,
            long unreadNotifications,
            boolean partialData
    ) {
        List<TenantOwnerDashboardResponse.AlertItem> alerts = new ArrayList<>();

        if (pendingAccounts > 0) {
            alerts.add(new TenantOwnerDashboardResponse.AlertItem(
                    "ACCOUNTS_PENDING_APPROVAL",
                    pendingAccounts > 20 ? "CRITICAL" : "WARN",
                    "Pending account approvals",
                    "There are " + pendingAccounts + " accounts waiting for approval.",
                    pendingAccounts,
                    "Review account approvals"
            ));
        }

        if (blockedOrFrozenAccounts > 0) {
            alerts.add(new TenantOwnerDashboardResponse.AlertItem(
                    "ACCOUNTS_BLOCKED_OR_FROZEN",
                    blockedOrFrozenAccounts > 10 ? "CRITICAL" : "WARN",
                    "Blocked or frozen accounts",
                    "There are " + blockedOrFrozenAccounts + " blocked or frozen accounts.",
                    blockedOrFrozenAccounts,
                    "Review affected accounts"
            ));
        }

        if (failedTransactions > 0) {
            alerts.add(new TenantOwnerDashboardResponse.AlertItem(
                    "FAILED_TRANSACTIONS",
                    failedTransactions > 25 ? "CRITICAL" : "WARN",
                    "Failed transactions",
                    "There are " + failedTransactions + " failed transactions in the current period.",
                    failedTransactions,
                    "Open transaction failures"
            ));
        }

        if (reviewRequiredLimits > 0) {
            alerts.add(new TenantOwnerDashboardResponse.AlertItem(
                    "LIMITS_REVIEW_REQUIRED",
                    reviewRequiredLimits > 10 ? "CRITICAL" : "INFO",
                    "Limits require review",
                    reviewRequiredLimits + " limit rules require manual review when exceeded.",
                    reviewRequiredLimits,
                    "Review limits"
            ));
        }

        if (openPeriods == 0) {
            alerts.add(new TenantOwnerDashboardResponse.AlertItem(
                    "NO_OPEN_ACCOUNTING_PERIOD",
                    "WARN",
                    "No open accounting period",
                    "There is no open accounting period configured for the tenant.",
                    0,
                    "Open an accounting period"
            ));
        }

        if (unreadNotifications > 0) {
            alerts.add(new TenantOwnerDashboardResponse.AlertItem(
                    "UNREAD_NOTIFICATIONS",
                    unreadNotifications > 15 ? "INFO" : "INFO",
                    "Unread notifications",
                    "You have " + unreadNotifications + " unread notifications.",
                    unreadNotifications,
                    "Open notifications"
            ));
        }

        if (partialData) {
            alerts.add(new TenantOwnerDashboardResponse.AlertItem(
                    "PARTIAL_DATA",
                    "WARN",
                    "Partial FX conversion",
                    "Some balances or amounts could not be converted to the base currency because exchange rates are missing.",
                    0,
                    "Review FX rates"
            ));
        }

        return alerts;
    }

    private List<TenantOwnerDashboardResponse.InsightItem> buildInsights(
            List<AccountOwnerView> accounts,
            List<Transaction> transactions,
            long reviewRequiredLimits,
            long openPeriods,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        List<TenantOwnerDashboardResponse.InsightItem> insights = new ArrayList<>();

        accounts.stream()
                .max(Comparator.comparing(account -> toAccountRawBalance(account)))
                .ifPresent(account -> insights.add(new TenantOwnerDashboardResponse.InsightItem(
                        "TOP_ACCOUNT_BALANCE",
                        "INFO",
                        "Largest account balance",
                        "Account " + safeText(account.accountNumber()) + " currently holds the largest balance in the tenant.",
                        "UP",
                        money(toAccountRawBalance(account), account.currency().name()).amount().toPlainString() + " " + account.currency().name()
                )));

        long completed = transactions.stream().filter(transaction -> transaction.status() == TransactionStatus.COMPLETED).count();
        long total = transactions.size();
        if (total > 0) {
            BigDecimal successRate = percentageOf(BigDecimal.valueOf(completed), BigDecimal.valueOf(total));
            insights.add(new TenantOwnerDashboardResponse.InsightItem(
                    "TRANSACTION_SUCCESS_RATE",
                    successRate.compareTo(BigDecimal.valueOf(95)) >= 0 ? "INFO" : "WARN",
                    "Transaction success rate",
                    "The current transaction success rate is " + successRate.toPlainString() + "%.",
                    successRate.compareTo(BigDecimal.valueOf(95)) >= 0 ? "UP" : "STABLE",
                    successRate.toPlainString() + "%"
            ));
        }

        if (reviewRequiredLimits > 0) {
            insights.add(new TenantOwnerDashboardResponse.InsightItem(
                    "LIMIT_REVIEW_PRESSURE",
                    "WARN",
                    "Limit review pressure",
                    "There are " + reviewRequiredLimits + " limit rules that may require manual attention.",
                    "UP",
                    String.valueOf(reviewRequiredLimits)
            ));
        }

        if (openPeriods == 0) {
            insights.add(new TenantOwnerDashboardResponse.InsightItem(
                    "ACCOUNTING_PERIOD_BLOCKED",
                    "WARN",
                    "Accounting closed",
                    "No open accounting period is available, which can block postings.",
                    "DOWN",
                    "0"
            ));
        }

        if (partialData.get()) {
            insights.add(new TenantOwnerDashboardResponse.InsightItem(
                    "FX_PARTIAL_DATA",
                    "WARN",
                    "FX data incomplete",
                    "Some totals could not be fully converted to " + baseCurrency + " because an exchange rate is missing.",
                    "STABLE",
                    "PARTIAL"
            ));
        }

        return insights;
    }

    private List<TenantOwnerDashboardResponse.ActivityItem> buildRecentActivity(
            List<Transaction> transactions,
            List<TenantOwnerDashboardResponse.JournalEntryItem> journalEntries,
            List<TenantAuditEvent> auditEvents,
            List<Notification> notifications,
            List<AccountOwnerView> accounts,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        Map<String, String> accountNumbersById = accounts.stream()
                .collect(Collectors.toMap(account -> account.id().toString(), AccountOwnerView::accountNumber, (left, right) -> left));

        List<TenantOwnerDashboardResponse.ActivityItem> activities = new ArrayList<>();

        transactions.stream()
                .sorted(Comparator.comparing(this::effectiveTransactionTimestamp).reversed())
                .limit(5)
                .forEach(transaction -> activities.add(new TenantOwnerDashboardResponse.ActivityItem(
                        "TRANSACTION",
                        transaction.type().name() + " " + transaction.status().name(),
                        buildTransactionDescription(transaction, accountNumbersById, baseCurrency, fxRates, partialData),
                        toDashboardTime(effectiveTransactionTimestamp(transaction)),
                        "tenant.transactions",
                        transaction.id().toString(),
                        isFailedTransaction(transaction) ? "WARN" : "INFO"
                )));

        journalEntries.stream()
                .sorted(Comparator.comparing((TenantOwnerDashboardResponse.JournalEntryItem item) -> item.postedAt() != null ? item.postedAt() : item.createdAt()).reversed())
                .limit(5)
                .forEach(entry -> activities.add(new TenantOwnerDashboardResponse.ActivityItem(
                        "JOURNAL_ENTRY",
                        "Journal entry " + entry.status(),
                        safeText(entry.entryNumber()) + " posted with debits " + entry.totalDebits() + " and credits " + entry.totalCredits(),
                        entry.postedAt() != null ? entry.postedAt() : entry.createdAt(),
                        "tenant.accounting",
                        entry.id(),
                        "INFO"
                )));

        auditEvents.stream()
                .limit(5)
                .forEach(event -> activities.add(new TenantOwnerDashboardResponse.ActivityItem(
                        "AUDIT_EVENT",
                        safeText(event.eventType()),
                        safeText(event.eventDetails()),
                        toDashboardTime(event.createdAt()),
                        "tenant.audit",
                        event.id().toString(),
                        "INFO"
                )));

        notifications.stream()
                .limit(5)
                .forEach(notification -> activities.add(new TenantOwnerDashboardResponse.ActivityItem(
                        "NOTIFICATION",
                        safeText(notification.title()),
                        safeText(notification.body()),
                        toDashboardTime(notification.createdAt()),
                        "tenant.notifications",
                        notification.id().toString(),
                        notification.readAt() == null ? "WARN" : "INFO"
                )));

        return activities.stream()
                .sorted(Comparator.comparing(TenantOwnerDashboardResponse.ActivityItem::timestamp, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(MAX_RECENT_ITEMS)
                .toList();
    }

    private String buildTransactionDescription(
            Transaction transaction,
            Map<String, String> accountNumbersById,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        String amount = money(convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData), baseCurrency)
                .amount()
                .toPlainString();
        String source = transaction.sourceAccountId() != null ? accountNumbersById.getOrDefault(transaction.sourceAccountId().toString(), transaction.sourceAccountId().toString()) : "N/A";
        String target = transaction.targetAccountId() != null ? accountNumbersById.getOrDefault(transaction.targetAccountId().toString(), transaction.targetAccountId().toString()) : "N/A";
        return transaction.type().name() + " transaction for " + amount + " " + baseCurrency + " between " + source + " and " + target;
    }

    private String resolveDisplayName(AuthenticatedTenantUserResponse currentUser) {
        StringBuilder builder = new StringBuilder();
        if (currentUser.firstName() != null && !currentUser.firstName().isBlank()) {
            builder.append(currentUser.firstName().trim());
        }
        if (currentUser.lastName() != null && !currentUser.lastName().isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(currentUser.lastName().trim());
        }
        String value = builder.toString().trim();
        if (!value.isBlank()) {
            return value;
        }
        if (currentUser.email() != null && !currentUser.email().isBlank()) {
            return currentUser.email().trim();
        }
        String subject = securityContextFacade.getCurrentSubject();
        return subject != null ? subject : "unknown";
    }

    private List<LocalDate> eachDate(LocalDate from, LocalDate to) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            dates.add(cursor);
            cursor = cursor.plusDays(1);
        }
        return dates;
    }

    private Map<String, BigDecimal> buildExchangeRateMap(List<FxExchangeRate> fxExchangeRates) {
        Map<String, BigDecimal> rates = new HashMap<>();
        for (FxExchangeRate rate : fxExchangeRates) {
            if (rate.sourceCurrency() == null || rate.targetCurrency() == null || rate.rate() == null) {
                continue;
            }
            String forwardKey = rateKey(rate.sourceCurrency().name(), rate.targetCurrency().name());
            rates.put(forwardKey, rate.rate());
            if (rate.rate().compareTo(BigDecimal.ZERO) > 0) {
                String reverseKey = rateKey(rate.targetCurrency().name(), rate.sourceCurrency().name());
                rates.putIfAbsent(reverseKey, BigDecimal.ONE.divide(rate.rate(), 12, RoundingMode.HALF_UP));
            }
        }
        return rates;
    }

    private BigDecimal sumAccountBalances(
            List<AccountOwnerView> accounts,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        return accounts.stream()
                .map(account -> convertToBaseCurrency(toAccountRawBalance(account), account.currency().name(), baseCurrency, fxRates, partialData))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumTransactionAmounts(
            List<Transaction> transactions,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        return transactions.stream()
                .map(transaction -> convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal convertToBaseCurrency(
            BigDecimal amount,
            String currency,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }

        if (currency == null || currency.isBlank() || baseCurrency.equalsIgnoreCase(currency)) {
            return amount;
        }

        BigDecimal rate = fxRates.get(rateKey(currency, baseCurrency));
        if (rate != null) {
            return amount.multiply(rate);
        }

        partialData.set(true);
        return BigDecimal.ZERO;
    }

    private BigDecimal percentageChange(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            if (current == null || current.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            return BigDecimal.valueOf(100);
        }

        return current.subtract(previous)
                .divide(previous.abs(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percentageOf(BigDecimal part, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return part.multiply(BigDecimal.valueOf(100))
                .divide(total, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal toAccountRawBalance(AccountOwnerView account) {
        BigDecimal available = account.availableBalance() != null ? account.availableBalance() : BigDecimal.ZERO;
        BigDecimal held = account.heldBalance() != null ? account.heldBalance() : BigDecimal.ZERO;
        return available.add(held);
    }

    private OffsetDateTime toDashboardTime(Instant instant) {
        return instant != null ? instant.atZone(DASHBOARD_ZONE).toOffsetDateTime() : null;
    }

    private boolean isInWindow(Instant instant, PeriodWindow window) {
        if (instant == null) {
            return false;
        }
        OffsetDateTime value = instant.atZone(DASHBOARD_ZONE).toOffsetDateTime();
        return !value.isBefore(window.from()) && !value.isAfter(window.to());
    }

    private Instant effectiveTransactionTimestamp(Transaction transaction) {
        if (transaction.processedAt() != null) {
            return transaction.processedAt();
        }
        return transaction.createdAt();
    }

    private Instant journalEntryTimestamp(JournalEntry entry) {
        if (entry.postedAt() != null) {
            return entry.postedAt();
        }
        return entry.createdAt();
    }

    private boolean isFailedTransaction(Transaction transaction) {
        return transaction.status() == TransactionStatus.FAILED
                || transaction.status() == TransactionStatus.CANCELLED
                || transaction.status() == TransactionStatus.EXPIRED;
    }

    private long accountStatusCount(List<AccountOwnerView> accounts, AccountStatus status) {
        return accounts.stream().filter(account -> account.status() == status).count();
    }

    private long transactionStatusCount(List<Transaction> transactions, TransactionStatus status) {
        return transactions.stream().filter(transaction -> transaction.status() == status).count();
    }

    private String rateKey(String sourceCurrency, String targetCurrency) {
        return sourceCurrency + "->" + targetCurrency;
    }

    private TenantOwnerDashboardResponse.Money money(BigDecimal amount, String currency) {
        BigDecimal normalized = amount == null ? BigDecimal.ZERO : amount.setScale(2, RoundingMode.HALF_UP);
        return new TenantOwnerDashboardResponse.Money(normalized, currency);
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private List<TenantOwnerDashboardResponse.CountItem> countItems(
            Map<String, Long> counts,
            Function<String, String> labelMapper
    ) {
        return counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TenantOwnerDashboardResponse.CountItem(
                        entry.getKey(),
                        labelMapper.apply(entry.getKey()),
                        entry.getValue()
                ))
                .toList();
    }

    private Function<String, String> accountStatusLabelMapper() {
        return code -> humanize(code);
    }

    private Function<String, String> accountTypeLabelMapper() {
        return code -> humanize(code);
    }

    private Function<String, String> transactionStatusLabelMapper() {
        return code -> humanize(code);
    }

    private Function<String, String> transactionTypeLabelMapper() {
        return code -> humanize(code);
    }

    private Function<String, String> limitTypeLabelMapper() {
        return code -> humanize(code);
    }

    private Function<String, String> limitScopeLabelMapper() {
        return code -> humanize(code);
    }

    private Function<String, String> periodStatusLabelMapper() {
        return code -> humanize(code);
    }

    private Function<String, String> periodTypeLabelMapper() {
        return code -> humanize(code);
    }

    private Function<String, String> journalStatusLabelMapper() {
        return code -> humanize(code);
    }

    private Function<String, String> journalTypeLabelMapper() {
        return code -> humanize(code);
    }

    private String humanize(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String normalized = code.trim().toLowerCase(Locale.ROOT).replace('_', ' ');
        StringBuilder builder = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : normalized.toCharArray()) {
            if (capitalizeNext && Character.isLetter(c)) {
                builder.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                builder.append(c);
            }
            if (c == ' ') {
                capitalizeNext = true;
            }
        }
        return builder.toString();
    }

    private TenantOwnerDashboardResponse.AccountItem toAccountItem(DashboardOwnerAccountView view) {
        AccountOwnerView account = view.account();
        return new TenantOwnerDashboardResponse.AccountItem(
                account.id().toString(),
                safeText(account.accountNumber()),
                fullName(account.userFirstName(), account.userLastName()),
                account.accountType().name(),
                account.status().name(),
                view.balance(),
                toDashboardTime(account.createdAt())
        );
    }

    private String fullName(String firstName, String lastName) {
        StringBuilder builder = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            builder.append(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(lastName.trim());
        }
        String value = builder.toString().trim();
        return value.isEmpty() ? null : value;
    }

    private static final class PeriodWindow {
        private final OffsetDateTime from;
        private final OffsetDateTime to;

        private PeriodWindow(OffsetDateTime from, OffsetDateTime to) {
            this.from = from;
            this.to = to;
        }

        private OffsetDateTime from() {
            return from;
        }

        private OffsetDateTime to() {
            return to;
        }

        private PeriodWindow shiftMonths(int months) {
            return new PeriodWindow(from.plusMonths(months), to.plusMonths(months));
        }
    }

    private record DashboardOwnerAccountView(AccountOwnerView account, TenantOwnerDashboardResponse.Money balance) {
    }
}
