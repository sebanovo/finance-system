export type AssignSubscriptionRequest = {
  tenantId: string;
  planCode: string;
  overrideTrialDays: number | null;
};

export type AssignedSubscriptionDto = {
  id: string;
  tenantId: string;
  tenantName: string;
  tenantSlug: string;
  planId: string;
  planCode: string;
  planName: string;
  planType: string;
  maxUsers: number;
  maxRoles: number;
  status: string;
  trial: boolean;
  currentSubscription: boolean;
  startedAt: string;
  expiresAt: string;
  remainingDays: number;
  createdAt: string;
  updatedAt: string;
};

export type AssignedSubscription = {
  id: string;
  tenantId: string;
  tenantName: string;
  tenantSlug: string;
  planId: string;
  planCode: string;
  planName: string;
  planType: string;
  maxUsers: number;
  maxRoles: number;
  status: string;
  isTrial: boolean;
  isCurrentSubscription: boolean;
  startedAt: string; // Date
  expiresAt: string; // Date
  remainingDays: number;
  createdAt: string; // Date
  updatedAt: string; // Date
};

export type AssignSubscriptionFormValue = {
  planCode: string;
  overrideTrialDays: number | null;
};

export type AssignSubscriptionTenantContext = {
  id: string;
  name: string;
  slug: string;
};

export type AssignSubscriptionResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};
