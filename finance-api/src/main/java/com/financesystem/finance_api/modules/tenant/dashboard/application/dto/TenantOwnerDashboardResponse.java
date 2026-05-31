package com.financesystem.finance_api.modules.tenant.dashboard.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record TenantOwnerDashboardResponse(
        Metadata metadata,
        Filters filters,
        Comparisons comparisons,
        Summary summary,
        AccountsSection accounts,
        TransactionsSection transactions,
        LimitsSection limits,
        AccountingSection accounting,
        FxSection fx,
        List<AlertItem> alerts,
        List<InsightItem> insights,
        List<ActivityItem> recentActivity
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

    public record Filters(
            String period,
            Integer year,
            Integer month,
            LocalDate date,
            OffsetDateTime from,
            OffsetDateTime to,
            String currency
    ) {
    }

    public record Comparisons(
            TimeRange previousPeriod,
            ComparisonSummary summary
    ) {
    }

    public record TimeRange(
            OffsetDateTime from,
            OffsetDateTime to
    ) {
    }

    public record ComparisonSummary(
            BigDecimal accountOpeningsChangePercent,
            BigDecimal transactionsCountChangePercent,
            BigDecimal transactionsAmountChangePercent,
            BigDecimal failedTransactionsChangePercent
    ) {
    }

    public record Summary(
            UsersSummary users,
            AccountsSummary accounts,
            TransactionsSummary transactions,
            LimitsSummary limits,
            AccountingSummary accounting,
            NotificationsSummary notifications,
            AuditSummary audit
    ) {
    }

    public record UsersSummary(
            long total,
            long active,
            long inactive
    ) {
    }

    public record AccountsSummary(
            long total,
            long active,
            long blocked,
            long frozen,
            long pendingApproval,
            Money totalBalance
    ) {
    }

    public record TransactionsSummary(
            long total,
            long completed,
            long pending,
            long failed,
            long reversed,
            Money totalAmount
    ) {
    }

    public record LimitsSummary(
            long total,
            long active,
            long reviewRequired
    ) {
    }

    public record AccountingSummary(
            long openPeriods,
            long closedPeriods,
            long journalEntries
    ) {
    }

    public record NotificationsSummary(
            long unread
    ) {
    }

    public record AuditSummary(
            long events
    ) {
    }

    public record Money(
            BigDecimal amount,
            String currency
    ) {
    }

    public record CountItem(
            String code,
            String label,
            long total
    ) {
    }

    public record BalanceItem(
            String currency,
            String label,
            long accounts,
            Money balance
    ) {
    }

    public record AccountsSection(
            List<CountItem> byStatus,
            List<CountItem> byType,
            List<BalanceItem> byCurrency,
            SectionBucket pendingApproval,
            SectionBucket blockedOrFrozen
    ) {
    }

    public record SectionBucket(
            long total,
            List<AccountItem> items
    ) {
    }

    public record AccountItem(
            String id,
            String accountNumber,
            String holderName,
            String accountType,
            String status,
            Money balance,
            OffsetDateTime createdAt
    ) {
    }

    public record TransactionsSection(
            List<CountItem> byStatus,
            List<CountItem> byType,
            List<DailyCountPoint> dailyVolume,
            List<DailyMoneyPoint> dailyAmount,
            List<TransactionItem> recent
    ) {
    }

    public record DailyCountPoint(
            LocalDate date,
            long total
    ) {
    }

    public record DailyMoneyPoint(
            LocalDate date,
            Money amount
    ) {
    }

    public record TransactionItem(
            String id,
            String type,
            String status,
            Money amount,
            String currency,
            String sourceAccountNumber,
            String targetAccountNumber,
            String description,
            OffsetDateTime processedAt,
            OffsetDateTime createdAt
    ) {
    }

    public record LimitsSection(
            List<CountItem> byType,
            List<CountItem> byScope,
            List<LimitRuleItem> activeRules
    ) {
    }

    public record LimitRuleItem(
            String code,
            String name,
            String limitType,
            String scopeType,
            String period,
            String transactionType,
            String accountType,
            String currency,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Long maxCount,
            boolean requireReviewExceed,
            boolean active
    ) {
    }

    public record AccountingSection(
            List<CountItem> periodsByStatus,
            List<CountItem> periodsByType,
            List<CountItem> journalEntriesByStatus,
            List<CountItem> journalEntriesByType,
            List<JournalEntryItem> recentEntries
    ) {
    }

    public record JournalEntryItem(
            String id,
            String entryNumber,
            String status,
            String type,
            String description,
            BigDecimal totalDebits,
            BigDecimal totalCredits,
            OffsetDateTime postedAt,
            OffsetDateTime createdAt
    ) {
    }

    public record FxSection(
            List<FxRateItem> exchangeRates,
            List<OperationFeeItem> operationFees
    ) {
    }

    public record FxRateItem(
            String id,
            String sourceCurrency,
            String targetCurrency,
            BigDecimal rate,
            boolean active,
            String description,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }

    public record OperationFeeItem(
            String id,
            String operationCode,
            String feeType,
            BigDecimal feeValue,
            String calculationMode,
            boolean active,
            String description,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }

    public record AlertItem(
            String code,
            String severity,
            String title,
            String message,
            long count,
            String action
    ) {
    }

    public record InsightItem(
            String code,
            String severity,
            String title,
            String message,
            String trend,
            String value
    ) {
    }

    public record ActivityItem(
            String type,
            String title,
            String description,
            OffsetDateTime timestamp,
            String source,
            String referenceId,
            String severity
    ) {
    }
}
