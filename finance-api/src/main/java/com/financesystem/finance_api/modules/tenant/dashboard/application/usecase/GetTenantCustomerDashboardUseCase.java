package com.financesystem.finance_api.modules.tenant.dashboard.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.Notification;
import com.financesystem.finance_api.modules.governance.notifications.domain.repository.NotificationRepository;
import com.financesystem.finance_api.modules.identity.auth.application.dto.AuthenticatedTenantUserResponse;
import com.financesystem.finance_api.modules.identity.auth.application.usecase.GetCurrentAuthenticatedTenantUserUseCase;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.dashboard.application.dto.TenantCustomerDashboardResponse;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxExchangeRate;
import com.financesystem.finance_api.modules.tenant.fx.domain.repository.FxExchangeRateRepository;
import com.financesystem.finance_api.modules.tenant.limits.domain.model.LimitRule;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GetTenantCustomerDashboardUseCase {

    private static final ZoneId DASHBOARD_ZONE = ZoneId.of("America/La_Paz");
    private static final String BASE_CURRENCY = "BOB";
    private static final int MAX_RECENT_ITEMS = 8;
    private static final int MAX_NOTIFICATION_ITEMS = 5;

    private final SecurityContextFacade securityContextFacade;
    private final GetCurrentAuthenticatedTenantUserUseCase getCurrentAuthenticatedTenantUserUseCase;
    private final TenantUserRepository tenantUserRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LimitRuleRepository limitRuleRepository;
    private final FxExchangeRateRepository fxExchangeRateRepository;
    private final NotificationRepository notificationRepository;

    public GetTenantCustomerDashboardUseCase(
            SecurityContextFacade securityContextFacade,
            GetCurrentAuthenticatedTenantUserUseCase getCurrentAuthenticatedTenantUserUseCase,
            TenantUserRepository tenantUserRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            LimitRuleRepository limitRuleRepository,
            FxExchangeRateRepository fxExchangeRateRepository,
            NotificationRepository notificationRepository
    ) {
        this.securityContextFacade = securityContextFacade;
        this.getCurrentAuthenticatedTenantUserUseCase = getCurrentAuthenticatedTenantUserUseCase;
        this.tenantUserRepository = tenantUserRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.limitRuleRepository = limitRuleRepository;
        this.fxExchangeRateRepository = fxExchangeRateRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public TenantCustomerDashboardResponse execute() {
        AuthenticatedTenantUserResponse currentUser = getCurrentAuthenticatedTenantUserUseCase.execute();
        TenantUser tenantUser = tenantUserRepository.findById(currentUser.id())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));

        String tenantSlug = requireTenantSlug();
        OffsetDateTime generatedAt = OffsetDateTime.now(DASHBOARD_ZONE);
        PeriodWindow currentMonth = currentMonthWindow(generatedAt);

        List<AccountOwnerView> accounts = accountRepository.findAllViewsByUserId(currentUser.id());
        Map<String, BigDecimal> fxRates = buildFxRates(fxExchangeRateRepository.findAll());
        AtomicBoolean partialData = new AtomicBoolean(false);
        Set<UUID> accountIds = accounts.stream().map(AccountOwnerView::id).collect(Collectors.toSet());

        List<Transaction> allTransactions = loadUserTransactions(accounts);
        List<Transaction> currentMonthTransactions = allTransactions.stream()
                .filter(transaction -> isInWindow(effectiveTimestamp(transaction), currentMonth))
                .toList();

        BigDecimal totalBalance = sumAccountBalances(accounts, BASE_CURRENCY, fxRates, partialData);
        BigDecimal monthlyIncome = sumIncomingAmount(currentMonthTransactions, accountIds, BASE_CURRENCY, fxRates, partialData);
        BigDecimal monthlyExpenses = sumOutgoingAmount(currentMonthTransactions, accountIds, BASE_CURRENCY, fxRates, partialData);
        long pendingTransactions = countPendingTransactions(allTransactions);
        long unreadNotifications = notificationRepository.countUnreadInboxByUserId(currentUser.id());

        List<TenantCustomerDashboardResponse.AccountItem> accountItems = buildAccountItems(accounts);
        List<TenantCustomerDashboardResponse.CurrencyBalanceItem> balancesByCurrency = buildBalancesByCurrency(accounts);
        List<TenantCustomerDashboardResponse.DailyMoneyPoint> monthlyVolume = buildMonthlyVolume(currentMonthTransactions, currentMonth, BASE_CURRENCY, fxRates, partialData);
        List<TenantCustomerDashboardResponse.TransactionAggregateItem> byType = buildTransactionByType(currentMonthTransactions, accountIds, BASE_CURRENCY, fxRates, partialData);
        List<TenantCustomerDashboardResponse.TransactionItem> recentItems = buildRecentTransactions(allTransactions, accounts, BASE_CURRENCY, fxRates, partialData, accountIds);
        List<TenantCustomerDashboardResponse.TransactionItem> pendingItems = buildPendingTransactions(allTransactions, accounts, BASE_CURRENCY, fxRates, partialData, accountIds);
        LimitDashboard limits = buildLimits(allTransactions, currentMonth, BASE_CURRENCY, fxRates, partialData, accountIds);
        List<TenantCustomerDashboardResponse.NotificationItem> notificationItems = buildNotifications(currentUser.id());

        String dataCompleteness = partialData.get() ? "PARTIAL" : "COMPLETE";
        String generatedBy = resolveDisplayName(tenantUser);

        List<TenantCustomerDashboardResponse.AlertItem> alerts = buildAlerts(
                accounts,
                pendingTransactions,
                unreadNotifications,
                partialData.get(),
                totalBalance,
                monthlyIncome,
                monthlyExpenses
        );

        List<TenantCustomerDashboardResponse.InsightItem> insights = buildInsights(
                accounts,
                monthlyIncome,
                monthlyExpenses,
                pendingTransactions,
                totalBalance
        );

        return new TenantCustomerDashboardResponse(
                new TenantCustomerDashboardResponse.Metadata(
                        generatedAt,
                        DASHBOARD_ZONE.getId(),
                        tenantSlug,
                        generatedBy,
                        BASE_CURRENCY,
                        dataCompleteness
                ),
                new TenantCustomerDashboardResponse.Summary(
                        accounts.size(),
                        money(totalBalance, BASE_CURRENCY),
                        money(monthlyIncome, BASE_CURRENCY),
                        money(monthlyExpenses, BASE_CURRENCY),
                        pendingTransactions,
                        unreadNotifications
                ),
                new TenantCustomerDashboardResponse.AccountsSection(accountItems),
                new TenantCustomerDashboardResponse.BalancesSection(balancesByCurrency),
                new TenantCustomerDashboardResponse.TransactionsSection(
                        monthlyVolume,
                        byType,
                        new TenantCustomerDashboardResponse.SectionBucket(recentItems.size(), recentItems),
                        new TenantCustomerDashboardResponse.SectionBucket(pendingItems.size(), pendingItems)
                ),
                new TenantCustomerDashboardResponse.LimitsSection(
                        limits.transfer(),
                        limits.withdrawal(),
                        limits.activeRules()
                ),
                new TenantCustomerDashboardResponse.NotificationsSection(
                        unreadNotifications,
                        notificationItems
                ),
                alerts,
                insights
        );
    }

    private String requireTenantSlug() {
        String tenantSlug = securityContextFacade.getCurrentTenantSlug();
        if (tenantSlug == null || tenantSlug.isBlank()) {
            return TenantContextHolder.getRequired().tenantSlug();
        }
        return tenantSlug;
    }

    private String resolveDisplayName(TenantUser tenantUser) {
        StringBuilder builder = new StringBuilder();
        if (tenantUser.firstName() != null && !tenantUser.firstName().isBlank()) {
            builder.append(tenantUser.firstName().trim());
        }
        if (tenantUser.lastName() != null && !tenantUser.lastName().isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(tenantUser.lastName().trim());
        }
        String value = builder.toString().trim();
        if (!value.isBlank()) {
            return value;
        }
        if (tenantUser.email() != null && !tenantUser.email().isBlank()) {
            return tenantUser.email().trim();
        }
        String subject = securityContextFacade.getCurrentSubject();
        return subject != null ? subject : "unknown";
    }

    private List<Transaction> loadUserTransactions(List<AccountOwnerView> accounts) {
        Map<UUID, Transaction> uniqueTransactions = new LinkedHashMap<>();
        for (AccountOwnerView account : accounts) {
            for (Transaction transaction : transactionRepository.findAllByAccountId(account.id())) {
                uniqueTransactions.putIfAbsent(transaction.id(), transaction);
            }
        }

        return uniqueTransactions.values().stream()
                .sorted(Comparator.comparing(this::effectiveTimestamp).reversed())
                .toList();
    }

    private BigDecimal sumAccountBalances(
            List<AccountOwnerView> accounts,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        BigDecimal total = BigDecimal.ZERO;
        for (AccountOwnerView account : accounts) {
            total = total.add(convertToBaseCurrency(
                    rawBalance(account),
                    account.currency() != null ? account.currency().name() : baseCurrency,
                    baseCurrency,
                    fxRates,
                    partialData
            ));
        }
        return total;
    }

    private List<TenantCustomerDashboardResponse.AccountItem> buildAccountItems(List<AccountOwnerView> accounts) {
        return accounts.stream()
                .sorted(Comparator.comparing(AccountOwnerView::createdAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(account -> new TenantCustomerDashboardResponse.AccountItem(
                        account.id().toString(),
                        account.accountNumber(),
                        accountLabel(account),
                        safeEnumName(account.accountType()),
                        safeEnumName(account.currency()),
                        safeEnumName(account.status()),
                        money(rawBalance(account), safeEnumName(account.currency())),
                        toDashboardTime(account.createdAt())
                ))
                .toList();
    }

    private List<TenantCustomerDashboardResponse.CurrencyBalanceItem> buildBalancesByCurrency(List<AccountOwnerView> accounts) {
        Map<String, BigDecimal> balancesByCurrency = new LinkedHashMap<>();
        for (AccountOwnerView account : accounts) {
            String currency = safeEnumName(account.currency());
            balancesByCurrency.merge(currency, rawBalance(account), BigDecimal::add);
        }

        return balancesByCurrency.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TenantCustomerDashboardResponse.CurrencyBalanceItem(
                        entry.getKey(),
                        money(entry.getValue(), entry.getKey())
                ))
                .toList();
    }

    private List<TenantCustomerDashboardResponse.DailyMoneyPoint> buildMonthlyVolume(
            List<Transaction> transactions,
            PeriodWindow currentMonth,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        Map<LocalDate, BigDecimal> volumes = new LinkedHashMap<>();
        for (Transaction transaction : transactions) {
            if (!isInWindow(effectiveTimestamp(transaction), currentMonth)) {
                continue;
            }
            LocalDate date = effectiveTimestamp(transaction).atZone(DASHBOARD_ZONE).toLocalDate();
            BigDecimal amount = convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData);
            volumes.merge(date, amount, BigDecimal::add);
        }

        return eachDate(currentMonth.from().toLocalDate(), currentMonth.to().toLocalDate()).stream()
                .map(date -> new TenantCustomerDashboardResponse.DailyMoneyPoint(
                        date,
                        money(volumes.getOrDefault(date, BigDecimal.ZERO), baseCurrency)
                ))
                .toList();
    }

    private List<TenantCustomerDashboardResponse.TransactionAggregateItem> buildTransactionByType(
            List<Transaction> transactions,
            Set<UUID> accountIds,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        Map<String, AggregateBucket> buckets = new LinkedHashMap<>();
        for (Transaction transaction : transactions) {
            if (!isRelevantToUser(transaction, accountIds)) {
                continue;
            }
            String type = safeEnumName(transaction.type());
            AggregateBucket bucket = buckets.computeIfAbsent(type, key -> new AggregateBucket());
            bucket.total += 1;
            bucket.amount = bucket.amount.add(convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData));
        }

        return buckets.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TenantCustomerDashboardResponse.TransactionAggregateItem(
                        entry.getKey(),
                        entry.getValue().total,
                        money(entry.getValue().amount, baseCurrency)
                ))
                .toList();
    }

    private List<TenantCustomerDashboardResponse.TransactionItem> buildRecentTransactions(
            List<Transaction> transactions,
            List<AccountOwnerView> accounts,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData,
            Set<UUID> accountIds
    ) {
        Map<UUID, AccountOwnerView> accountsById = accounts.stream().collect(Collectors.toMap(AccountOwnerView::id, Function.identity()));
        return transactions.stream()
                .filter(transaction -> isRelevantToUser(transaction, accountIds))
                .limit(MAX_RECENT_ITEMS)
                .map(transaction -> toTransactionItem(transaction, accountsById, baseCurrency, fxRates, partialData))
                .toList();
    }

    private List<TenantCustomerDashboardResponse.TransactionItem> buildPendingTransactions(
            List<Transaction> transactions,
            List<AccountOwnerView> accounts,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData,
            Set<UUID> accountIds
    ) {
        Map<UUID, AccountOwnerView> accountsById = accounts.stream().collect(Collectors.toMap(AccountOwnerView::id, Function.identity()));
        return transactions.stream()
                .filter(transaction -> isRelevantToUser(transaction, accountIds))
                .filter(this::isPending)
                .sorted(Comparator.comparing(this::effectiveTimestamp).reversed())
                .map(transaction -> toTransactionItem(transaction, accountsById, baseCurrency, fxRates, partialData))
                .toList();
    }

    private TenantCustomerDashboardResponse.TransactionItem toTransactionItem(
            Transaction transaction,
            Map<UUID, AccountOwnerView> accountsById,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        AccountOwnerView source = transaction.sourceAccountId() != null ? accountsById.get(transaction.sourceAccountId()) : null;
        AccountOwnerView target = transaction.targetAccountId() != null ? accountsById.get(transaction.targetAccountId()) : null;
        return new TenantCustomerDashboardResponse.TransactionItem(
                transaction.id().toString(),
                safeText(transaction.externalReference()),
                safeEnumName(transaction.type()),
                safeEnumName(transaction.status()),
                money(convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData), baseCurrency),
                safeText(transaction.description()),
                source != null ? source.accountNumber() : null,
                target != null ? target.accountNumber() : null,
                toDashboardTime(effectiveTimestamp(transaction))
        );
    }

    private LimitDashboard buildLimits(
            List<Transaction> transactions,
            PeriodWindow currentMonth,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData,
            Set<UUID> accountIds
    ) {
        List<LimitRule> activeRules = limitRuleRepository.findAllActive();
        List<TenantCustomerDashboardResponse.LimitRuleItem> activeRuleItems = activeRules.stream()
                .limit(10)
                .map(rule -> new TenantCustomerDashboardResponse.LimitRuleItem(
                        rule.code(),
                        rule.name(),
                        safeEnumName(rule.limitType()),
                        safeEnumName(rule.scopeType()),
                        safeEnumName(rule.period()),
                        rule.transactionType() != null ? rule.transactionType().name() : null,
                        rule.currency() != null ? rule.currency().name() : null,
                        rule.minAmount(),
                        rule.maxAmount(),
                        rule.maxCount(),
                        rule.requireReviewExceed(),
                        rule.active()
                ))
                .toList();

        List<Transaction> outgoingTransferTransactions = transactions.stream()
                .filter(transaction -> isOutgoingTransfer(transaction, accountIds))
                .toList();
        List<Transaction> outgoingWithdrawalTransactions = transactions.stream()
                .filter(transaction -> isOutgoingWithdrawal(transaction, accountIds))
                .toList();

        TenantCustomerDashboardResponse.LimitWindowUsage transferDaily = buildLimitWindow(
                "DAILY",
                outgoingTransferTransactions,
                currentDayWindow(),
                baseCurrency,
                fxRates,
                partialData,
                activeRules,
                TransactionType.TRANSFER
        );
        TenantCustomerDashboardResponse.LimitWindowUsage transferMonthly = buildLimitWindow(
                "MONTHLY",
                outgoingTransferTransactions,
                currentMonth,
                baseCurrency,
                fxRates,
                partialData,
                activeRules,
                TransactionType.TRANSFER
        );
        TenantCustomerDashboardResponse.LimitWindowUsage withdrawalDaily = buildLimitWindow(
                "DAILY",
                outgoingWithdrawalTransactions,
                currentDayWindow(),
                baseCurrency,
                fxRates,
                partialData,
                activeRules,
                TransactionType.WITHDRAWAL
        );

        return new LimitDashboard(
                new TenantCustomerDashboardResponse.TransferLimits(transferDaily, transferMonthly),
                new TenantCustomerDashboardResponse.WithdrawalLimits(withdrawalDaily),
                activeRuleItems
        );
    }

    private TenantCustomerDashboardResponse.LimitWindowUsage buildLimitWindow(
            String periodName,
            List<Transaction> transactions,
            PeriodWindow window,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData,
            List<LimitRule> activeRules,
            TransactionType type
    ) {
        List<LimitRule> matchingRules = activeRules.stream()
                .filter(rule -> rule.active())
                .filter(rule -> rule.transactionType() == type)
                .filter(rule -> rule.period() != null && rule.period().name().equalsIgnoreCase(periodName))
                .toList();

        BigDecimal usedAmount = BigDecimal.ZERO;
        long usedCount = 0;
        for (Transaction transaction : transactions) {
            if (!isInWindow(effectiveTimestamp(transaction), window)) {
                continue;
            }
            usedCount++;
            usedAmount = usedAmount.add(convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData));
        }

        BigDecimal limitAmount = matchingRules.stream()
                .map(LimitRule::maxAmount)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
        Long limitCount = matchingRules.stream()
                .map(LimitRule::maxCount)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
        boolean requiresReview = matchingRules.stream().anyMatch(LimitRule::requireReviewExceed);

        return new TenantCustomerDashboardResponse.LimitWindowUsage(
                periodName,
                money(usedAmount, baseCurrency),
                limitAmount != null ? money(limitAmount, baseCurrency) : null,
                usedCount,
                limitCount,
                matchingRules.size(),
                requiresReview,
                !matchingRules.isEmpty()
        );
    }

    private List<TenantCustomerDashboardResponse.NotificationItem> buildNotifications(UUID userId) {
        return notificationRepository.findInboxByUserIdOrderByCreatedAtDesc(userId).stream()
                .limit(MAX_NOTIFICATION_ITEMS)
                .map(notification -> new TenantCustomerDashboardResponse.NotificationItem(
                        notification.id().toString(),
                        safeEnumName(notification.type()),
                        safeText(notification.title()),
                        notification.readAt() == null ? "UNREAD" : "READ",
                        toDashboardTime(notification.createdAt())
                ))
                .toList();
    }

    private List<TenantCustomerDashboardResponse.AlertItem> buildAlerts(
            List<AccountOwnerView> accounts,
            long pendingTransactions,
            long unreadNotifications,
            boolean partialData,
            BigDecimal totalBalance,
            BigDecimal monthlyIncome,
            BigDecimal monthlyExpenses
    ) {
        List<TenantCustomerDashboardResponse.AlertItem> alerts = new ArrayList<>();
        if (accounts.isEmpty()) {
            alerts.add(new TenantCustomerDashboardResponse.AlertItem(
                    "NO_ACCOUNTS",
                    "WARN",
                    "Sin cuentas",
                    "Aún no tienes cuentas activas para mostrar en tu dashboard.",
                    "Abrir creación de cuenta"
            ));
        }
        if (pendingTransactions > 0) {
            alerts.add(new TenantCustomerDashboardResponse.AlertItem(
                    "PENDING_TRANSACTIONS",
                    "INFO",
                    "Movimientos pendientes",
                    "Tienes " + pendingTransactions + " movimientos pendientes de confirmación o proceso.",
                    "Revisar movimientos"
            ));
        }
        if (unreadNotifications > 0) {
            alerts.add(new TenantCustomerDashboardResponse.AlertItem(
                    "UNREAD_NOTIFICATIONS",
                    "INFO",
                    "Notificaciones sin leer",
                    "Tienes " + unreadNotifications + " notificaciones sin revisar.",
                    "Abrir notificaciones"
            ));
        }
        if (partialData) {
            alerts.add(new TenantCustomerDashboardResponse.AlertItem(
                    "PARTIAL_FX_DATA",
                    "WARN",
                    "Datos parciales",
                    "No se pudieron convertir todos los montos a la moneda base por falta de tipos de cambio.",
                    "Revisar FX"
            ));
        }
        if (monthlyExpenses.compareTo(monthlyIncome) > 0) {
            alerts.add(new TenantCustomerDashboardResponse.AlertItem(
                    "EXPENSES_HIGHER_THAN_INCOME",
                    "WARN",
                    "Gastos por encima de ingresos",
                    "En el período actual tus gastos superan tus ingresos.",
                    "Revisar gastos"
            ));
        }
        if (totalBalance.compareTo(BigDecimal.valueOf(500)) < 0 && !accounts.isEmpty()) {
            alerts.add(new TenantCustomerDashboardResponse.AlertItem(
                    "LOW_BALANCE",
                    "MEDIUM",
                    "Saldo bajo",
                    "Tu patrimonio total está por debajo de BOB 500.",
                    "Revisar cuentas"
            ));
        }
        return alerts;
    }

    private List<TenantCustomerDashboardResponse.InsightItem> buildInsights(
            List<AccountOwnerView> accounts,
            BigDecimal monthlyIncome,
            BigDecimal monthlyExpenses,
            long pendingTransactions,
            BigDecimal totalBalance
    ) {
        List<TenantCustomerDashboardResponse.InsightItem> insights = new ArrayList<>();
        accounts.stream()
                .max(Comparator.comparing(this::rawBalance))
                .ifPresent(account -> insights.add(new TenantCustomerDashboardResponse.InsightItem(
                        "TOP_ACCOUNT",
                        "INFO",
                        "Cuenta con mayor saldo",
                        accountLabel(account) + " es tu cuenta con mayor saldo actual.",
                        account.accountNumber()
                )));
        BigDecimal net = monthlyIncome.subtract(monthlyExpenses);
        insights.add(new TenantCustomerDashboardResponse.InsightItem(
                "MONTHLY_NET",
                net.compareTo(BigDecimal.ZERO) >= 0 ? "INFO" : "WARN",
                "Resultado mensual",
                net.compareTo(BigDecimal.ZERO) >= 0 ? "Tus ingresos superan tus gastos en el período actual." : "Tus gastos superan tus ingresos en el período actual.",
                net.toPlainString() + " " + BASE_CURRENCY
        ));
        if (pendingTransactions == 0) {
            insights.add(new TenantCustomerDashboardResponse.InsightItem(
                    "NO_PENDING_TRANSACTIONS",
                    "INFO",
                    "Sin pendientes",
                    "No tienes movimientos pendientes por procesar.",
                    "0"
            ));
        }
        if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
            insights.add(new TenantCustomerDashboardResponse.InsightItem(
                    "HEALTHY_BALANCE",
                    "INFO",
                    "Patrimonio positivo",
                    "Tu saldo total consolidado es positivo.",
                    totalBalance.toPlainString() + " " + BASE_CURRENCY
            ));
        }
        return insights;
    }

    private BigDecimal sumIncomingAmount(
            List<Transaction> transactions,
            Set<UUID> accountIds,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (isIncomingForUser(transaction, accountIds)) {
                total = total.add(convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData));
            }
        }
        return total;
    }

    private BigDecimal sumOutgoingAmount(
            List<Transaction> transactions,
            Set<UUID> accountIds,
            String baseCurrency,
            Map<String, BigDecimal> fxRates,
            AtomicBoolean partialData
    ) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (isOutgoingForUser(transaction, accountIds)) {
                total = total.add(convertToBaseCurrency(transaction.amount(), transaction.currency(), baseCurrency, fxRates, partialData));
            }
        }
        return total;
    }

    private long countPendingTransactions(List<Transaction> transactions) {
        return transactions.stream().filter(this::isPending).count();
    }

    private Map<String, BigDecimal> buildFxRates(List<FxExchangeRate> rates) {
        Map<String, BigDecimal> fxRates = new HashMap<>();
        for (FxExchangeRate rate : rates) {
            if (rate == null || !rate.active() || rate.sourceCurrency() == null || rate.targetCurrency() == null || rate.rate() == null) {
                continue;
            }
            fxRates.put(rateKey(rate.sourceCurrency().name(), rate.targetCurrency().name()), rate.rate());
            if (rate.rate().compareTo(BigDecimal.ZERO) > 0) {
                fxRates.putIfAbsent(
                        rateKey(rate.targetCurrency().name(), rate.sourceCurrency().name()),
                        BigDecimal.ONE.divide(rate.rate(), 12, RoundingMode.HALF_UP)
                );
            }
        }
        return fxRates;
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
        if (rate == null) {
            partialData.set(true);
            return BigDecimal.ZERO;
        }
        return amount.multiply(rate);
    }

    private boolean isRelevantToUser(Transaction transaction, Set<UUID> accountIds) {
        return (transaction.sourceAccountId() != null && accountIds.contains(transaction.sourceAccountId()))
                || (transaction.targetAccountId() != null && accountIds.contains(transaction.targetAccountId()));
    }

    private boolean isIncomingForUser(Transaction transaction, Set<UUID> accountIds) {
        return transaction.targetAccountId() != null
                && accountIds.contains(transaction.targetAccountId())
                && (transaction.sourceAccountId() == null || !accountIds.contains(transaction.sourceAccountId()));
    }

    private boolean isOutgoingForUser(Transaction transaction, Set<UUID> accountIds) {
        return transaction.sourceAccountId() != null
                && accountIds.contains(transaction.sourceAccountId())
                && (transaction.targetAccountId() == null || !accountIds.contains(transaction.targetAccountId()));
    }

    private boolean isOutgoingTransfer(Transaction transaction, Set<UUID> accountIds) {
        return transaction.type() == TransactionType.TRANSFER && isOutgoingForUser(transaction, accountIds);
    }

    private boolean isOutgoingWithdrawal(Transaction transaction, Set<UUID> accountIds) {
        return transaction.type() == TransactionType.WITHDRAWAL && isOutgoingForUser(transaction, accountIds);
    }

    private boolean isPending(Transaction transaction) {
        return transaction.status() == TransactionStatus.PENDING
                || transaction.status() == TransactionStatus.PENDING_REVIEW
                || transaction.status() == TransactionStatus.PROCESSING
                || transaction.status() == TransactionStatus.AUTHORIZED;
    }

    private Instant effectiveTimestamp(Transaction transaction) {
        return transaction.processedAt() != null ? transaction.processedAt() : transaction.createdAt();
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

    private PeriodWindow currentMonthWindow(OffsetDateTime now) {
        OffsetDateTime from = now.toLocalDate().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay().atZone(DASHBOARD_ZONE).toOffsetDateTime();
        return new PeriodWindow(from, now);
    }

    private PeriodWindow currentDayWindow() {
        OffsetDateTime now = OffsetDateTime.now(DASHBOARD_ZONE);
        OffsetDateTime from = now.toLocalDate().atStartOfDay().atZone(DASHBOARD_ZONE).toOffsetDateTime();
        return new PeriodWindow(from, now);
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

    private BigDecimal rawBalance(AccountOwnerView account) {
        BigDecimal available = account.availableBalance() != null ? account.availableBalance() : BigDecimal.ZERO;
        BigDecimal held = account.heldBalance() != null ? account.heldBalance() : BigDecimal.ZERO;
        return available.add(held);
    }

    private String accountLabel(AccountOwnerView account) {
        if (account.customAlias() != null && !account.customAlias().isBlank()) {
            return account.customAlias().trim();
        }
        if (account.accountName() != null) {
            return account.accountName().name();
        }
        return account.accountNumber();
    }

    private String safeEnumName(Enum<?> value) {
        return value != null ? value.name() : "UNKNOWN";
    }

    private TenantCustomerDashboardResponse.Money money(BigDecimal amount, String currency) {
        return new TenantCustomerDashboardResponse.Money(
                amount == null ? BigDecimal.ZERO : amount.setScale(2, RoundingMode.HALF_UP),
                currency
        );
    }

    private String rateKey(String sourceCurrency, String targetCurrency) {
        return sourceCurrency + "->" + targetCurrency;
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private record PeriodWindow(OffsetDateTime from, OffsetDateTime to) {
    }

    private record LimitDashboard(
            TenantCustomerDashboardResponse.TransferLimits transfer,
            TenantCustomerDashboardResponse.WithdrawalLimits withdrawal,
            List<TenantCustomerDashboardResponse.LimitRuleItem> activeRules
    ) {
    }

    private static final class AggregateBucket {
        private long total = 0;
        private BigDecimal amount = BigDecimal.ZERO;
    }
}
