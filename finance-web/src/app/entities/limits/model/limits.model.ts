export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort?: unknown;
    offset?: number;
    unpaged?: boolean;
    paged?: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort?: unknown;
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}

export interface LimitRuleResponse {
  id: string;
  code: string;
  name: string;
  description: string | null;
  limitType: string;
  scopeType: string;
  period: string;
  transactionType: string | null;
  accountType: string | null;
  currency: string | null;
  minAmount: number | null;
  maxAmount: number | null;
  maxCount: number | null;
  active: boolean;
  requireReviewExceed: boolean;
  scopeDescription: string | null;
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
