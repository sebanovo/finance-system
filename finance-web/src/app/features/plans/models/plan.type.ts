export type CreatePlanRequest = {
  code: string;
  name: string;
  description: string;
  maxUsers: number;
  maxRoles: number;
  planType: string;
  trialDays: number;
};

export type UpdatePlanRequest = {
  code: string;
  name: string;
  description: string;
  maxUsers: number;
  maxRoles: number;
  planType: string;
  trialDays: number;
};

export type PlanFormValue = {
  code: string;
  name: string;
  description: string;
  maxUsers: number;
  maxRoles: number;
  planType: string;
  trialDays: number;
};

export type PlanUpsertFormValue = {
  code: string;
  name: string;
  description: string;
  maxUsers: number;
  maxRoles: number;
  planType: string;
  trialDays: number;
};

export type PlanDto = {
  id: string;
  code: string;
  name: string;
  description: string;
  maxUsers: number;
  maxRoles: number;
  planType: string;
  trialDays: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
};

export type Plan = {
  id: string;
  code: string;
  name: string;
  description: string;
  maxUsers: number;
  maxRoles: number;
  planType: string;
  trialDays: number;
  isActive: boolean;
  createdAt: string; // Date
  updatedAt: string; // Date
};

export type PlanResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};
