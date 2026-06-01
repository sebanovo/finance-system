export interface LimitRuleResponse {
  id: string;
  code: string;
  name: string;
  description: string;
  limitType: string;
  scopeType: string;
  period: string;
  transactionType: string;
  accountType: string;
  currency: string;
  minAmount: number;
  maxAmount: number;
  maxCount: number;
  active: boolean;
  requireReviewExceed: boolean;
  scopeDescription: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateLimitRuleRequest {
  code: string;
  name: string;
  description?: string;
  limitType: string;
  scopeType: string;
  period: string;
  transactionType?: string;
  accountType?: string;
  currency?: string;
  minAmount?: number;
  maxAmount?: number;
  maxCount?: number;
  active: boolean;
  requireReviewExceed: boolean;
}

export interface UpdateLimitRuleRequest {
  name?: string;
  description?: string;
  minAmount?: number;
  maxAmount?: number;
  maxCount?: number;
  active?: boolean;
  requireReviewExceed?: boolean;
}

export interface LimitEvaluationRequest {
  accountId: string;
  transactionType: string;
  amount: number;
  currency: string;
}

export interface LimitEvaluationResponse {
  evaluationId: string;
  accountId: string;
  transactionType: string;
  amount: number;
  currency: string;
  allowed: boolean;
  reason: string;
  requiresReview: boolean;
  ruleChecks: any[];
}
