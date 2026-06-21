export interface TenantSummaryResponse {
  metadata: TenantDashboardMetadata;
  filters: TenantDashboardFilters;
  comparisons: TenantDashboardComparisons;
  summary: TenantDashboardSummary;
  accounts: TenantDashboardAccountsSection;
  transactions: TenantDashboardTransactionsSection;
  limits: TenantDashboardLimitsSection;
  accounting: TenantDashboardAccountingSection;
  fx: TenantDashboardFxSection;
  alerts: TenantDashboardAlertItem[];
  insights: TenantDashboardInsightItem[];
  recentActivity: TenantDashboardActivityItem[];
}

export interface TenantDashboardMetadata {
  generatedAt: string;
  timezone: string;
  tenantSlug: string;
  generatedBy: string;
  baseCurrency: string;
  dataCompleteness: string;
}

export interface TenantDashboardFilters {
  period: string | null;
  year: number | null;
  month: number | null;
  date: string | null;
  from: string | null;
  to: string | null;
  currency: string | null;
}

export interface TenantDashboardComparisons {
  previousPeriod: TenantDashboardTimeRange;
  summary: TenantDashboardComparisonSummary;
}

export interface TenantDashboardTimeRange {
  from: string | null;
  to: string | null;
}

export interface TenantDashboardComparisonSummary {
  accountOpeningsChangePercent: number;
  transactionsCountChangePercent: number;
  transactionsAmountChangePercent: number;
  failedTransactionsChangePercent: number;
}

export interface TenantDashboardSummary {
  users: TenantDashboardUsersSummary;
  accounts: TenantDashboardAccountsSummary;
  transactions: TenantDashboardTransactionsSummary;
  limits: TenantDashboardLimitsSummary;
  accounting: TenantDashboardAccountingSummary;
  notifications: TenantDashboardNotificationsSummary;
  audit: TenantDashboardAuditSummary;
}

export interface TenantDashboardUsersSummary {
  total: number;
  active: number;
  inactive: number;
}

export interface TenantDashboardAccountsSummary {
  total: number;
  active: number;
  blocked: number;
  frozen: number;
  pendingApproval: number;
  totalBalance: TenantDashboardMoney;
}

export interface TenantDashboardTransactionsSummary {
  total: number;
  completed: number;
  pending: number;
  failed: number;
  reversed: number;
  totalAmount: TenantDashboardMoney;
}

export interface TenantDashboardLimitsSummary {
  total: number;
  active: number;
  reviewRequired: number;
}

export interface TenantDashboardAccountingSummary {
  openPeriods: number;
  closedPeriods: number;
  journalEntries: number;
}

export interface TenantDashboardNotificationsSummary {
  unread: number;
}

export interface TenantDashboardAuditSummary {
  events: number;
}

export interface TenantDashboardMoney {
  amount: number;
  currency: string;
}

export interface TenantDashboardCountItem {
  code: string;
  label: string;
  total: number;
}

export interface TenantDashboardBalanceItem {
  currency: string;
  label: string;
  accounts: number;
  balance: TenantDashboardMoney;
}

export interface TenantDashboardAccountsSection {
  byStatus: TenantDashboardCountItem[];
  byType: TenantDashboardCountItem[];
  byCurrency: TenantDashboardBalanceItem[];
  pendingApproval: TenantDashboardSectionBucket<TenantDashboardAccountItem>;
  blockedOrFrozen: TenantDashboardSectionBucket<TenantDashboardAccountItem>;
}

export interface TenantDashboardSectionBucket<T> {
  total: number;
  items: T[];
}

export interface TenantDashboardAccountItem {
  id: string;
  accountNumber: string;
  holderName: string;
  accountType: string;
  status: string;
  balance: TenantDashboardMoney;
  createdAt: string | null;
}

export interface TenantDashboardTransactionsSection {
  byStatus: TenantDashboardCountItem[];
  byType: TenantDashboardCountItem[];
  dailyVolume: TenantDashboardDailyCountPoint[];
  dailyAmount: TenantDashboardDailyMoneyPoint[];
  recent: TenantDashboardTransactionItem[];
}

export interface TenantDashboardDailyCountPoint {
  date: string;
  total: number;
}

export interface TenantDashboardDailyMoneyPoint {
  date: string;
  amount: TenantDashboardMoney;
}

export interface TenantDashboardTransactionItem {
  id: string;
  type: string;
  status: string;
  amount: TenantDashboardMoney;
  currency: string;
  sourceAccountNumber: string | null;
  targetAccountNumber: string | null;
  description: string | null;
  processedAt: string | null;
  createdAt: string | null;
}

export interface TenantDashboardLimitsSection {
  byType: TenantDashboardCountItem[];
  byScope: TenantDashboardCountItem[];
  activeRules: TenantDashboardLimitRuleItem[];
}

export interface TenantDashboardLimitRuleItem {
  code: string;
  name: string;
  limitType: string;
  scopeType: string;
  period: string | null;
  transactionType: string | null;
  accountType: string | null;
  currency: string | null;
  minAmount: number | null;
  maxAmount: number | null;
  maxCount: number | null;
  requireReviewExceed: boolean;
  active: boolean;
}

export interface TenantDashboardAccountingSection {
  periodsByStatus: TenantDashboardCountItem[];
  periodsByType: TenantDashboardCountItem[];
  journalEntriesByStatus: TenantDashboardCountItem[];
  journalEntriesByType: TenantDashboardCountItem[];
  recentEntries: TenantDashboardJournalEntryItem[];
}

export interface TenantDashboardJournalEntryItem {
  id: string;
  entryNumber: string;
  status: string;
  type: string;
  description: string;
  totalDebits: number;
  totalCredits: number;
  postedAt: string | null;
  createdAt: string | null;
}

export interface TenantDashboardFxSection {
  exchangeRates: TenantDashboardFxRateItem[];
  operationFees: TenantDashboardOperationFeeItem[];
}

export interface TenantDashboardFxRateItem {
  id: string;
  sourceCurrency: string;
  targetCurrency: string;
  rate: number;
  active: boolean;
  description: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface TenantDashboardOperationFeeItem {
  id: string;
  operationCode: string;
  feeType: string;
  feeValue: number;
  calculationMode: string;
  active: boolean;
  description: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface TenantDashboardAlertItem {
  code: string;
  severity: string;
  title: string;
  message: string;
  count: number;
  action: string;
}

export interface TenantDashboardInsightItem {
  code: string;
  severity: string;
  title: string;
  message: string;
  trend: string;
  value: string;
}

export interface TenantDashboardActivityItem {
  type: string;
  title: string;
  description: string;
  timestamp: string | null;
  source: string;
  referenceId: string;
  severity: string;
}
