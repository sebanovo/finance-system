export type DashboardMetric = {
  label: string;
  value: number | string;
  helper: string;
  tone: "default" | "success" | "warning";
  loading?: boolean;
  error?: string | null;
};

export type DashboardAlert = {
  id: string;
  title: string;
  description: string;
  tone: "info" | "warning" | "critical";
};

export type DashboardTenantOverview = {
  companyName: string;
  legalName: string;
  timezone: string;
  currency: string;
  contactEmail: string;
  contactPhone: string;
};

export type DashboardCurrentSubscriptionOverview = {
  planName: string;
  planCode: string;
  planType: string;
  status: string;
  isTrial: boolean;
  remainingDays: number;
  expiresAt: string;
  maxUsers: number;
  maxRoles: number;
};

export type DashboardSectionState<T> = {
  data: T | null;
  loading: boolean;
  error: string | null;
  isEmpty: boolean;
};
