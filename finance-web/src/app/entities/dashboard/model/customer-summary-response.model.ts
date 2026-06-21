export interface CustomerSummaryResponse {
  metadata: CustomerDashboardMetadata;
  summary: CustomerDashboardSummary;
  accounts: CustomerDashboardAccountsSection;
  balances: CustomerDashboardBalancesSection;
  transactions: CustomerDashboardTransactionsSection;
  limits: CustomerDashboardLimitsSection;
  notifications: CustomerDashboardNotificationsSection;
  alerts: CustomerDashboardAlertItem[];
  insights: CustomerDashboardInsightItem[];
}

export interface CustomerDashboardMetadata {
  generatedAt: string;
  timezone: string;
  tenantSlug: string;
  generatedBy: string;
  baseCurrency: string;
  dataCompleteness: string;
}

export interface CustomerDashboardMoney {
  amount: number;
  currency: string;
}

export interface CustomerDashboardSummary {
  accounts: number;
  totalBalance: CustomerDashboardMoney;
  monthlyIncome: CustomerDashboardMoney;
  monthlyExpenses: CustomerDashboardMoney;
  pendingTransactions: number;
  unreadNotifications: number;
}

export interface CustomerDashboardAccountsSection {
  items: CustomerDashboardAccountItem[];
}

export interface CustomerDashboardAccountItem {
  id: string;
  accountNumber: string;
  label: string;
  type: string;
  currency: string;
  status: string;
  balance: CustomerDashboardMoney;
  createdAt: string | null;
}

export interface CustomerDashboardBalancesSection {
  byCurrency: CustomerDashboardCurrencyBalanceItem[];
}

export interface CustomerDashboardCurrencyBalanceItem {
  currency: string;
  balance: CustomerDashboardMoney;
}

export interface CustomerDashboardTransactionsSection {
  monthlyVolume: CustomerDashboardDailyMoneyPoint[];
  byType: CustomerDashboardTransactionAggregateItem[];
  recent: CustomerDashboardSectionBucket<CustomerDashboardTransactionItem>;
  pending: CustomerDashboardSectionBucket<CustomerDashboardTransactionItem>;
}

export interface CustomerDashboardDailyMoneyPoint {
  date: string;
  amount: CustomerDashboardMoney;
}

export interface CustomerDashboardTransactionAggregateItem {
  type: string;
  total: number;
  amount: CustomerDashboardMoney;
}

export interface CustomerDashboardSectionBucket<T> {
  total: number;
  items: T[];
}

export interface CustomerDashboardTransactionItem {
  id: string;
  reference: string;
  type: string;
  status: string;
  amount: CustomerDashboardMoney;
  description: string | null;
  sourceAccountNumber: string | null;
  targetAccountNumber: string | null;
  createdAt: string | null;
}

export interface CustomerDashboardLimitsSection {
  transfer: CustomerDashboardTransferLimits;
  withdrawal: CustomerDashboardWithdrawalLimits;
  activeRules: CustomerDashboardLimitRuleItem[];
}

export interface CustomerDashboardTransferLimits {
  daily: CustomerDashboardLimitWindowUsage;
  monthly: CustomerDashboardLimitWindowUsage;
}

export interface CustomerDashboardWithdrawalLimits {
  daily: CustomerDashboardLimitWindowUsage;
}

export interface CustomerDashboardLimitWindowUsage {
  period: string;
  used: CustomerDashboardMoney;
  limit: CustomerDashboardMoney;
  usedCount: number;
  limitCount: number | null;
  activeRules: number;
  requiresReview: boolean;
  applicable: boolean;
}

export interface CustomerDashboardLimitRuleItem {
  code: string;
  name: string;
  limitType: string;
  scopeType: string;
  period: string | null;
  transactionType: string | null;
  currency: string | null;
  minAmount: number | null;
  maxAmount: number | null;
  maxCount: number | null;
  requireReviewExceed: boolean;
  active: boolean;
}

export interface CustomerDashboardNotificationsSection {
  unread: number;
  items: CustomerDashboardNotificationItem[];
}

export interface CustomerDashboardNotificationItem {
  id: string;
  type: string;
  title: string;
  status: string;
  createdAt: string | null;
}

export interface CustomerDashboardAlertItem {
  code: string;
  severity: string;
  title: string;
  message: string;
  count: number;
  action: string;
}

export interface CustomerDashboardInsightItem {
  code: string;
  severity: string;
  title: string;
  message: string;
  trend: string;
  value: string;
}
