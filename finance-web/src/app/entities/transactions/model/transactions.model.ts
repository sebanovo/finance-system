export interface TransactionFxDetailResponse {
  sourceCurrency: string;
  targetCurrency: string;
  exchangeRate: number;
  originalAmount: number;
  convertedAmount: number;
}

export type TransactionMethod = 'MANUAL' | 'QR' | 'API' | 'ADMIN' | 'CASHBOX' | 'SCHEDULED' | 'EXTERNAL_BANK' | 'SYSTEM' | string;
export type AdjustmentDirection = 'CREDIT' | 'DEBIT' | string;
export type TransactionOperationType =
  | 'deposit'
  | 'withdrawal'
  | 'transfer'
  | 'payment'
  | 'fee'
  | 'hold'
  | 'release'
  | 'adjustment'
  | 'qr-intent'
  | 'qr-confirm';

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

export interface TransactionMovementResponse {
  accountId: string;
  accountNumber: string;
  accountDisplayName: string;
  movementType: string;
  amount: number;
  currency: string;
  balanceAfter: number;
}

export interface TransactionResponse {
  id: string;
  type: string;
  status: string;
  channel: string;
  amount: number;
  currency: string;
  sourceAccountId: string;
  sourceAccountNumber: string;
  sourceAccountDisplayName: string;
  targetAccountId: string;
  targetAccountNumber: string;
  targetAccountDisplayName: string;
  externalReference: string;
  idempotencyKey: string;
  description: string;
  failureReason: string;
  requestedByUserId: string;
  approvedByUserId: string;
  processedAt: string;
  createdAt: string;
  updatedAt: string;
  fxDetail?: TransactionFxDetailResponse;
  movements?: TransactionMovementResponse[];
}

export interface QrTransactionIntentResponse {
  id: string;
  status: string;
  channel: string;
  targetAccountId: string;
  amount: number;
  currency: string;
  externalReference: string;
  description: string;
  idempotencyKey: string;
  confirmedTransactionId: string | null;
  expiresAt: string | null;
  confirmedAt: string | null;
  cancelledAt: string | null;
  cancelledByUserId: string | null;
  payerAccountId: string | null;
  paidAmount: number | null;
  paidCurrency: string | null;
  qrPayload: string;
  qrSignature: string | null;
  requestedByUserId: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateDepositTransactionRequest {
  targetAccountId: string;
  amount: number;
  currency: string;
  method: TransactionMethod;
  description?: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateFeeTransactionRequest {
  accountId: string;
  amount: number;
  currency: string;
  description?: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateHoldTransactionRequest {
  accountId: string;
  amount: number;
  currency: string;
  description?: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateQrTransactionIntentRequest {
  targetAccountId: string;
  amount: number;
  currency: string;
  description?: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CancelQrTransactionIntentRequest {
  idempotencyKey?: string;
}

export interface ConfirmQrTransactionRequest {
  sourceAccountId: string;
  idempotencyKey: string;
}

export interface CreatePaymentTransactionRequest {
  sourceAccountId: string;
  amount: number;
  currency: string;
  method: TransactionMethod;
  description?: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateAdjustmentTransactionRequest {
  accountId: string;
  amount: number;
  currency: string;
  direction: AdjustmentDirection;
  reason: string;
  externalReference?: string;
  idempotencyKey: string;
}

export interface CreateWithdrawalTransactionRequest {
  sourceAccountId: string;
  amount: number;
  currency: string;
  method: TransactionMethod;
  description?: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateReleaseTransactionRequest {
  accountId: string;
  amount: number;
  currency: string;
  description?: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateTransferTransactionRequest {
  sourceAccountId: string;
  targetAccountId: string;
  amount: number;
  currency: string;
  description?: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateReversalTransactionRequest {
  reason: string;
  idempotencyKey: string;
}

export interface CreateRefundTransactionRequest {
  amount: number;
  reason: string;
  idempotencyKey: string;
}
