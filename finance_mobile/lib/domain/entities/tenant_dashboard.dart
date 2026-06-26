class TenantDashboard {
  final TenantDashboardMetadata metadata;
  final TenantDashboardFilters filters;
  final TenantDashboardComparisons comparisons;
  final TenantDashboardSummary summary;
  final TenantDashboardAccountsSection accounts;
  final TenantDashboardTransactionsSection transactions;
  final TenantDashboardLimitsSection limits;
  final TenantDashboardAccountingSection accounting;
  final TenantDashboardFxSection fx;
  final List<TenantDashboardAlertItem> alerts;
  final List<TenantDashboardInsightItem> insights;
  final List<TenantDashboardActivityItem> recentActivity;

  const TenantDashboard({
    required this.metadata,
    required this.filters,
    required this.comparisons,
    required this.summary,
    required this.accounts,
    required this.transactions,
    required this.limits,
    required this.accounting,
    required this.fx,
    required this.alerts,
    required this.insights,
    required this.recentActivity,
  });

  factory TenantDashboard.placeholder() {
    return TenantDashboard(
      metadata: const TenantDashboardMetadata(
        generatedAt: null,
        timezone: '',
        tenantSlug: '',
        generatedBy: '',
        baseCurrency: '',
        dataCompleteness: '',
      ),
      filters: const TenantDashboardFilters(
        period: null,
        year: null,
        month: null,
        date: null,
        from: null,
        to: null,
        currency: null,
      ),
      comparisons: const TenantDashboardComparisons(
        previousPeriod: TenantDashboardTimeRange(from: null, to: null),
        summary: TenantDashboardComparisonSummary(
          accountOpeningsChangePercent: 0,
          transactionsCountChangePercent: 0,
          transactionsAmountChangePercent: 0,
          failedTransactionsChangePercent: 0,
        ),
      ),
      summary: const TenantDashboardSummary(
        users: TenantDashboardUsersSummary(total: 0, active: 0, inactive: 0),
        accounts: TenantDashboardAccountsSummary(
          total: 0,
          active: 0,
          blocked: 0,
          frozen: 0,
          pendingApproval: 0,
          totalBalance: TenantDashboardMoney(amount: 0, currency: ''),
        ),
        transactions: TenantDashboardTransactionsSummary(
          total: 0,
          completed: 0,
          pending: 0,
          failed: 0,
          reversed: 0,
          totalAmount: TenantDashboardMoney(amount: 0, currency: ''),
        ),
        limits: TenantDashboardLimitsSummary(total: 0, active: 0, reviewRequired: 0),
        accounting: TenantDashboardAccountingSummary(
          openPeriods: 0,
          closedPeriods: 0,
          journalEntries: 0,
        ),
        notifications: TenantDashboardNotificationsSummary(unread: 0),
        audit: TenantDashboardAuditSummary(events: 0),
      ),
      accounts: const TenantDashboardAccountsSection(
        byStatus: [],
        byType: [],
        byCurrency: [],
        pendingApproval: TenantDashboardSectionBucket<TenantDashboardAccountItem>(
          total: 0,
          items: [],
        ),
        blockedOrFrozen: TenantDashboardSectionBucket<TenantDashboardAccountItem>(
          total: 0,
          items: [],
        ),
      ),
      transactions: const TenantDashboardTransactionsSection(
        byStatus: [],
        byType: [],
        dailyVolume: [],
        dailyAmount: [],
        recent: [],
      ),
      limits: const TenantDashboardLimitsSection(
        byType: [],
        byScope: [],
        activeRules: [],
      ),
      accounting: const TenantDashboardAccountingSection(
        periodsByStatus: [],
        periodsByType: [],
        journalEntriesByStatus: [],
        journalEntriesByType: [],
        recentEntries: [],
      ),
      fx: const TenantDashboardFxSection(
        exchangeRates: [],
        operationFees: [],
      ),
      alerts: const [],
      insights: const [],
      recentActivity: const [],
    );
  }
}

class TenantDashboardMetadata {
  final DateTime? generatedAt;
  final String timezone;
  final String tenantSlug;
  final String generatedBy;
  final String baseCurrency;
  final String dataCompleteness;

  const TenantDashboardMetadata({
    required this.generatedAt,
    required this.timezone,
    required this.tenantSlug,
    required this.generatedBy,
    required this.baseCurrency,
    required this.dataCompleteness,
  });
}

class TenantDashboardFilters {
  final String? period;
  final int? year;
  final int? month;
  final String? date;
  final String? from;
  final String? to;
  final String? currency;

  const TenantDashboardFilters({
    required this.period,
    required this.year,
    required this.month,
    required this.date,
    required this.from,
    required this.to,
    required this.currency,
  });
}

class TenantDashboardComparisons {
  final TenantDashboardTimeRange previousPeriod;
  final TenantDashboardComparisonSummary summary;

  const TenantDashboardComparisons({
    required this.previousPeriod,
    required this.summary,
  });
}

class TenantDashboardTimeRange {
  final String? from;
  final String? to;

  const TenantDashboardTimeRange({
    required this.from,
    required this.to,
  });
}

class TenantDashboardComparisonSummary {
  final double accountOpeningsChangePercent;
  final double transactionsCountChangePercent;
  final double transactionsAmountChangePercent;
  final double failedTransactionsChangePercent;

  const TenantDashboardComparisonSummary({
    required this.accountOpeningsChangePercent,
    required this.transactionsCountChangePercent,
    required this.transactionsAmountChangePercent,
    required this.failedTransactionsChangePercent,
  });
}

class TenantDashboardSummary {
  final TenantDashboardUsersSummary users;
  final TenantDashboardAccountsSummary accounts;
  final TenantDashboardTransactionsSummary transactions;
  final TenantDashboardLimitsSummary limits;
  final TenantDashboardAccountingSummary accounting;
  final TenantDashboardNotificationsSummary notifications;
  final TenantDashboardAuditSummary audit;

  const TenantDashboardSummary({
    required this.users,
    required this.accounts,
    required this.transactions,
    required this.limits,
    required this.accounting,
    required this.notifications,
    required this.audit,
  });
}

class TenantDashboardUsersSummary {
  final int total;
  final int active;
  final int inactive;

  const TenantDashboardUsersSummary({
    required this.total,
    required this.active,
    required this.inactive,
  });
}

class TenantDashboardMoney {
  final double amount;
  final String currency;

  const TenantDashboardMoney({
    required this.amount,
    required this.currency,
  });
}

class TenantDashboardAccountsSummary {
  final int total;
  final int active;
  final int blocked;
  final int frozen;
  final int pendingApproval;
  final TenantDashboardMoney totalBalance;

  const TenantDashboardAccountsSummary({
    required this.total,
    required this.active,
    required this.blocked,
    required this.frozen,
    required this.pendingApproval,
    required this.totalBalance,
  });
}

class TenantDashboardTransactionsSummary {
  final int total;
  final int completed;
  final int pending;
  final int failed;
  final int reversed;
  final TenantDashboardMoney totalAmount;

  const TenantDashboardTransactionsSummary({
    required this.total,
    required this.completed,
    required this.pending,
    required this.failed,
    required this.reversed,
    required this.totalAmount,
  });
}

class TenantDashboardLimitsSummary {
  final int total;
  final int active;
  final int reviewRequired;

  const TenantDashboardLimitsSummary({
    required this.total,
    required this.active,
    required this.reviewRequired,
  });
}

class TenantDashboardAccountingSummary {
  final int openPeriods;
  final int closedPeriods;
  final int journalEntries;

  const TenantDashboardAccountingSummary({
    required this.openPeriods,
    required this.closedPeriods,
    required this.journalEntries,
  });
}

class TenantDashboardNotificationsSummary {
  final int unread;

  const TenantDashboardNotificationsSummary({required this.unread});
}

class TenantDashboardAuditSummary {
  final int events;

  const TenantDashboardAuditSummary({required this.events});
}

class TenantDashboardCountItem {
  final String code;
  final String label;
  final int total;

  const TenantDashboardCountItem({
    required this.code,
    required this.label,
    required this.total,
  });
}

class TenantDashboardBalanceItem {
  final String currency;
  final String label;
  final int accounts;
  final TenantDashboardMoney balance;

  const TenantDashboardBalanceItem({
    required this.currency,
    required this.label,
    required this.accounts,
    required this.balance,
  });
}

class TenantDashboardAccountsSection {
  final List<TenantDashboardCountItem> byStatus;
  final List<TenantDashboardCountItem> byType;
  final List<TenantDashboardBalanceItem> byCurrency;
  final TenantDashboardSectionBucket<TenantDashboardAccountItem> pendingApproval;
  final TenantDashboardSectionBucket<TenantDashboardAccountItem> blockedOrFrozen;

  const TenantDashboardAccountsSection({
    required this.byStatus,
    required this.byType,
    required this.byCurrency,
    required this.pendingApproval,
    required this.blockedOrFrozen,
  });
}

class TenantDashboardSectionBucket<T> {
  final int total;
  final List<T> items;

  const TenantDashboardSectionBucket({
    required this.total,
    required this.items,
  });
}

class TenantDashboardAccountItem {
  final String id;
  final String accountNumber;
  final String holderName;
  final String accountType;
  final String status;
  final TenantDashboardMoney balance;
  final DateTime? createdAt;

  const TenantDashboardAccountItem({
    required this.id,
    required this.accountNumber,
    required this.holderName,
    required this.accountType,
    required this.status,
    required this.balance,
    required this.createdAt,
  });
}

class TenantDashboardTransactionsSection {
  final List<TenantDashboardCountItem> byStatus;
  final List<TenantDashboardCountItem> byType;
  final List<TenantDashboardDailyCountPoint> dailyVolume;
  final List<TenantDashboardDailyMoneyPoint> dailyAmount;
  final List<TenantDashboardTransactionItem> recent;

  const TenantDashboardTransactionsSection({
    required this.byStatus,
    required this.byType,
    required this.dailyVolume,
    required this.dailyAmount,
    required this.recent,
  });
}

class TenantDashboardDailyCountPoint {
  final DateTime? date;
  final int total;

  const TenantDashboardDailyCountPoint({
    required this.date,
    required this.total,
  });
}

class TenantDashboardDailyMoneyPoint {
  final DateTime? date;
  final TenantDashboardMoney amount;

  const TenantDashboardDailyMoneyPoint({
    required this.date,
    required this.amount,
  });
}

class TenantDashboardTransactionItem {
  final String id;
  final String type;
  final String status;
  final TenantDashboardMoney amount;
  final String currency;
  final String? sourceAccountNumber;
  final String? targetAccountNumber;
  final String? description;
  final String? processedAt;
  final String? createdAt;

  const TenantDashboardTransactionItem({
    required this.id,
    required this.type,
    required this.status,
    required this.amount,
    required this.currency,
    required this.sourceAccountNumber,
    required this.targetAccountNumber,
    required this.description,
    required this.processedAt,
    required this.createdAt,
  });
}

class TenantDashboardLimitsSection {
  final List<TenantDashboardCountItem> byType;
  final List<TenantDashboardCountItem> byScope;
  final List<TenantDashboardLimitRuleItem> activeRules;

  const TenantDashboardLimitsSection({
    required this.byType,
    required this.byScope,
    required this.activeRules,
  });
}

class TenantDashboardLimitRuleItem {
  final String code;
  final String name;
  final String limitType;
  final String scopeType;
  final String? period;
  final String? transactionType;
  final String? accountType;
  final String? currency;
  final double? minAmount;
  final double? maxAmount;
  final int? maxCount;
  final bool requireReviewExceed;
  final bool active;

  const TenantDashboardLimitRuleItem({
    required this.code,
    required this.name,
    required this.limitType,
    required this.scopeType,
    required this.period,
    required this.transactionType,
    required this.accountType,
    required this.currency,
    required this.minAmount,
    required this.maxAmount,
    required this.maxCount,
    required this.requireReviewExceed,
    required this.active,
  });
}

class TenantDashboardAccountingSection {
  final List<TenantDashboardCountItem> periodsByStatus;
  final List<TenantDashboardCountItem> periodsByType;
  final List<TenantDashboardCountItem> journalEntriesByStatus;
  final List<TenantDashboardCountItem> journalEntriesByType;
  final List<TenantDashboardJournalEntryItem> recentEntries;

  const TenantDashboardAccountingSection({
    required this.periodsByStatus,
    required this.periodsByType,
    required this.journalEntriesByStatus,
    required this.journalEntriesByType,
    required this.recentEntries,
  });
}

class TenantDashboardJournalEntryItem {
  final String id;
  final String entryNumber;
  final String status;
  final String type;
  final String description;
  final double totalDebits;
  final double totalCredits;
  final String? postedAt;
  final String? createdAt;

  const TenantDashboardJournalEntryItem({
    required this.id,
    required this.entryNumber,
    required this.status,
    required this.type,
    required this.description,
    required this.totalDebits,
    required this.totalCredits,
    required this.postedAt,
    required this.createdAt,
  });
}

class TenantDashboardFxSection {
  final List<TenantDashboardFxRateItem> exchangeRates;
  final List<TenantDashboardOperationFeeItem> operationFees;

  const TenantDashboardFxSection({
    required this.exchangeRates,
    required this.operationFees,
  });
}

class TenantDashboardFxRateItem {
  final String id;
  final String sourceCurrency;
  final String targetCurrency;
  final double rate;
  final bool active;
  final String? description;
  final String? createdAt;
  final String? updatedAt;

  const TenantDashboardFxRateItem({
    required this.id,
    required this.sourceCurrency,
    required this.targetCurrency,
    required this.rate,
    required this.active,
    required this.description,
    required this.createdAt,
    required this.updatedAt,
  });
}

class TenantDashboardOperationFeeItem {
  final String id;
  final String operationCode;
  final String feeType;
  final double feeValue;
  final String calculationMode;
  final bool active;
  final String? description;
  final String? createdAt;
  final String? updatedAt;

  const TenantDashboardOperationFeeItem({
    required this.id,
    required this.operationCode,
    required this.feeType,
    required this.feeValue,
    required this.calculationMode,
    required this.active,
    required this.description,
    required this.createdAt,
    required this.updatedAt,
  });
}

class TenantDashboardAlertItem {
  final String code;
  final String severity;
  final String title;
  final String message;
  final int count;
  final String action;

  const TenantDashboardAlertItem({
    required this.code,
    required this.severity,
    required this.title,
    required this.message,
    required this.count,
    required this.action,
  });
}

class TenantDashboardInsightItem {
  final String code;
  final String severity;
  final String title;
  final String message;
  final String trend;
  final String value;

  const TenantDashboardInsightItem({
    required this.code,
    required this.severity,
    required this.title,
    required this.message,
    required this.trend,
    required this.value,
  });
}

class TenantDashboardActivityItem {
  final String type;
  final String title;
  final String description;
  final String? timestamp;
  final String source;
  final String referenceId;
  final String severity;

  const TenantDashboardActivityItem({
    required this.type,
    required this.title,
    required this.description,
    required this.timestamp,
    required this.source,
    required this.referenceId,
    required this.severity,
  });
}
