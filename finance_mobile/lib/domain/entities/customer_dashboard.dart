class CustomerDashboard {
  final CustomerDashboardMetadata metadata;
  final CustomerDashboardSummary summary;
  final CustomerDashboardAccountsSection accounts;
  final CustomerDashboardBalancesSection balances;
  final CustomerDashboardTransactionsSection transactions;
  final CustomerDashboardLimitsSection limits;
  final CustomerDashboardNotificationsSection notifications;
  final List<CustomerDashboardAlertItem> alerts;
  final List<CustomerDashboardInsightItem> insights;

  const CustomerDashboard({
    required this.metadata,
    required this.summary,
    required this.accounts,
    required this.balances,
    required this.transactions,
    required this.limits,
    required this.notifications,
    required this.alerts,
    required this.insights,
  });
}

class CustomerDashboardMetadata {
  final DateTime? generatedAt;
  final String timezone;
  final String tenantSlug;
  final String generatedBy;
  final String baseCurrency;
  final String dataCompleteness;

  const CustomerDashboardMetadata({
    required this.generatedAt,
    required this.timezone,
    required this.tenantSlug,
    required this.generatedBy,
    required this.baseCurrency,
    required this.dataCompleteness,
  });
}

class CustomerDashboardMoney {
  final double amount;
  final String currency;

  const CustomerDashboardMoney({
    required this.amount,
    required this.currency,
  });
}

class CustomerDashboardSummary {
  final int accounts;
  final CustomerDashboardMoney totalBalance;
  final CustomerDashboardMoney monthlyIncome;
  final CustomerDashboardMoney monthlyExpenses;
  final int pendingTransactions;
  final int unreadNotifications;

  const CustomerDashboardSummary({
    required this.accounts,
    required this.totalBalance,
    required this.monthlyIncome,
    required this.monthlyExpenses,
    required this.pendingTransactions,
    required this.unreadNotifications,
  });
}

class CustomerDashboardAccountsSection {
  final List<CustomerDashboardAccountItem> items;

  const CustomerDashboardAccountsSection({required this.items});
}

class CustomerDashboardAccountItem {
  final String id;
  final String accountNumber;
  final String label;
  final String type;
  final String currency;
  final String status;
  final CustomerDashboardMoney balance;
  final DateTime? createdAt;

  const CustomerDashboardAccountItem({
    required this.id,
    required this.accountNumber,
    required this.label,
    required this.type,
    required this.currency,
    required this.status,
    required this.balance,
    required this.createdAt,
  });
}

class CustomerDashboardBalancesSection {
  final List<CustomerDashboardCurrencyBalanceItem> byCurrency;

  const CustomerDashboardBalancesSection({required this.byCurrency});
}

class CustomerDashboardCurrencyBalanceItem {
  final String currency;
  final CustomerDashboardMoney balance;

  const CustomerDashboardCurrencyBalanceItem({
    required this.currency,
    required this.balance,
  });
}

class CustomerDashboardTransactionsSection {
  final List<CustomerDashboardDailyMoneyPoint> monthlyVolume;
  final List<CustomerDashboardTransactionAggregateItem> byType;
  final CustomerDashboardSectionBucket<CustomerDashboardTransactionItem> recent;
  final CustomerDashboardSectionBucket<CustomerDashboardTransactionItem> pending;

  const CustomerDashboardTransactionsSection({
    required this.monthlyVolume,
    required this.byType,
    required this.recent,
    required this.pending,
  });
}

class CustomerDashboardDailyMoneyPoint {
  final DateTime? date;
  final CustomerDashboardMoney amount;

  const CustomerDashboardDailyMoneyPoint({
    required this.date,
    required this.amount,
  });
}

class CustomerDashboardTransactionAggregateItem {
  final String type;
  final int total;
  final CustomerDashboardMoney amount;

  const CustomerDashboardTransactionAggregateItem({
    required this.type,
    required this.total,
    required this.amount,
  });
}

class CustomerDashboardSectionBucket<T> {
  final int total;
  final List<T> items;

  const CustomerDashboardSectionBucket({
    required this.total,
    required this.items,
  });
}

class CustomerDashboardTransactionItem {
  final String id;
  final String reference;
  final String type;
  final String status;
  final CustomerDashboardMoney amount;
  final String? description;
  final String? sourceAccountNumber;
  final String? targetAccountNumber;
  final DateTime? createdAt;

  const CustomerDashboardTransactionItem({
    required this.id,
    required this.reference,
    required this.type,
    required this.status,
    required this.amount,
    required this.description,
    required this.sourceAccountNumber,
    required this.targetAccountNumber,
    required this.createdAt,
  });
}

class CustomerDashboardLimitsSection {
  final CustomerDashboardTransferLimits transfer;
  final CustomerDashboardWithdrawalLimits withdrawal;
  final List<CustomerDashboardLimitRuleItem> activeRules;

  const CustomerDashboardLimitsSection({
    required this.transfer,
    required this.withdrawal,
    required this.activeRules,
  });
}

class CustomerDashboardTransferLimits {
  final CustomerDashboardLimitWindowUsage daily;
  final CustomerDashboardLimitWindowUsage monthly;

  const CustomerDashboardTransferLimits({
    required this.daily,
    required this.monthly,
  });
}

class CustomerDashboardWithdrawalLimits {
  final CustomerDashboardLimitWindowUsage daily;

  const CustomerDashboardWithdrawalLimits({required this.daily});
}

class CustomerDashboardLimitWindowUsage {
  final String period;
  final CustomerDashboardMoney used;
  final CustomerDashboardMoney limit;
  final int usedCount;
  final int? limitCount;
  final int activeRules;
  final bool requiresReview;
  final bool applicable;

  const CustomerDashboardLimitWindowUsage({
    required this.period,
    required this.used,
    required this.limit,
    required this.usedCount,
    required this.limitCount,
    required this.activeRules,
    required this.requiresReview,
    required this.applicable,
  });
}

class CustomerDashboardLimitRuleItem {
  final String code;
  final String name;
  final String limitType;
  final String scopeType;
  final String? period;
  final String? transactionType;
  final String? currency;
  final double? minAmount;
  final double? maxAmount;
  final int? maxCount;
  final bool requireReviewExceed;
  final bool active;

  const CustomerDashboardLimitRuleItem({
    required this.code,
    required this.name,
    required this.limitType,
    required this.scopeType,
    required this.period,
    required this.transactionType,
    required this.currency,
    required this.minAmount,
    required this.maxAmount,
    required this.maxCount,
    required this.requireReviewExceed,
    required this.active,
  });
}

class CustomerDashboardNotificationsSection {
  final int unread;
  final List<CustomerDashboardNotificationItem> items;

  const CustomerDashboardNotificationsSection({
    required this.unread,
    required this.items,
  });
}

class CustomerDashboardNotificationItem {
  final String id;
  final String type;
  final String title;
  final String status;
  final DateTime? createdAt;

  const CustomerDashboardNotificationItem({
    required this.id,
    required this.type,
    required this.title,
    required this.status,
    required this.createdAt,
  });
}

class CustomerDashboardAlertItem {
  final String code;
  final String severity;
  final String title;
  final String message;
  final int count;
  final String action;

  const CustomerDashboardAlertItem({
    required this.code,
    required this.severity,
    required this.title,
    required this.message,
    required this.count,
    required this.action,
  });
}

class CustomerDashboardInsightItem {
  final String code;
  final String severity;
  final String title;
  final String message;
  final String trend;
  final String value;

  const CustomerDashboardInsightItem({
    required this.code,
    required this.severity,
    required this.title,
    required this.message,
    required this.trend,
    required this.value,
  });
}
