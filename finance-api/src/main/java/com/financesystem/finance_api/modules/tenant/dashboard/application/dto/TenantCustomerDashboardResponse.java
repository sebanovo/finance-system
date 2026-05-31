package com.financesystem.finance_api.modules.tenant.dashboard.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record TenantCustomerDashboardResponse(
        Metadata metadata,
        Summary summary,
        AccountsSection accounts,
        BalancesSection balances,
        TransactionsSection transactions,
        LimitsSection limits,
        NotificationsSection notifications,
        List<AlertItem> alerts,
        List<InsightItem> insights
) {

    public record Metadata(
            OffsetDateTime generatedAt,
            String timezone,
            String tenantSlug,
            String generatedBy,
            String baseCurrency,
            String dataCompleteness
    ) {
    }

    public record Summary(
            long accounts,
            Money totalBalance,
            Money monthlyIncome,
            Money monthlyExpenses,
            long pendingTransactions,
            long unreadNotifications
    ) {
    }

    public record Money(
            BigDecimal amount,
            String currency
    ) {
    }

    public record AccountsSection(
            List<AccountItem> items
    ) {
    }

    public record AccountItem(
            String id,
            String accountNumber,
            String label,
            String type,
            String currency,
            String status,
            Money balance,
            OffsetDateTime createdAt
    ) {
    }

    public record BalancesSection(
            List<CurrencyBalanceItem> byCurrency
    ) {
    }

    public record CurrencyBalanceItem(
            String currency,
            Money balance
    ) {
    }

    public record TransactionsSection(
            List<DailyMoneyPoint> monthlyVolume,
            List<TransactionAggregateItem> byType,
            SectionBucket recent,
            SectionBucket pending
    ) {
    }

    public record DailyMoneyPoint(
            LocalDate date,
            Money amount
    ) {
    }

    public record TransactionAggregateItem(
            String type,
            long total,
            Money amount
    ) {
    }

    public record SectionBucket(
            long total,
            List<TransactionItem> items
    ) {
    }

    public record TransactionItem(
            String id,
            String reference,
            String type,
            String status,
            Money amount,
            String description,
            String sourceAccountNumber,
            String targetAccountNumber,
            OffsetDateTime createdAt
    ) {
    }

    public record LimitsSection(
            TransferLimits transfer,
            WithdrawalLimits withdrawal,
            List<LimitRuleItem> activeRules
    ) {
    }

    public record TransferLimits(
            LimitWindowUsage daily,
            LimitWindowUsage monthly
    ) {
    }

    public record WithdrawalLimits(
            LimitWindowUsage daily
    ) {
    }

    public record LimitWindowUsage(
            String period,
            Money used,
            Money limit,
            long usedCount,
            Long limitCount,
            long activeRules,
            boolean requiresReview,
            boolean applicable
    ) {
    }

    public record LimitRuleItem(
            String code,
            String name,
            String limitType,
            String scopeType,
            String period,
            String transactionType,
            String currency,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Long maxCount,
            boolean requireReviewExceed,
            boolean active
    ) {
    }

    public record NotificationsSection(
            long unread,
            List<NotificationItem> items
    ) {
    }

    public record NotificationItem(
            String id,
            String type,
            String title,
            String status,
            OffsetDateTime createdAt
    ) {
    }

    public record AlertItem(
            String code,
            String severity,
            String title,
            String description,
            String action
    ) {
    }

    public record InsightItem(
            String code,
            String severity,
            String title,
            String description,
            String value
    ) {
    }
}
