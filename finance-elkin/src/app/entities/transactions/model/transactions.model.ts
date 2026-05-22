export interface TransactionFxDetailResponse {
  sourceCurrency: string;
  targetCurrency: string;
  exchangeRate: number;
  originalAmount: number;
  convertedAmount: number;
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
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  qrPayload: string;
  expiresAt: string;
}

export interface CreateDepositTransactionRequest {
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  channel: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateFeeTransactionRequest {
  sourceAccountId: string;
  amount: number;
  currency: string;
  description: string;
  feeType: string;
  externalReference?: string;
}

export interface CreateHoldTransactionRequest {
  sourceAccountId: string;
  amount: number;
  currency: string;
  description: string;
  expiresAt?: string;
  externalReference?: string;
}

export interface CreateQrTransactionIntentRequest {
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  expiresInMinutes?: number;
}

export interface CreatePaymentTransactionRequest {
  sourceAccountId: string;
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  idempotencyKey: string;
  paymentReference?: string;
  externalReference?: string;
}

export interface CreateAdjustmentTransactionRequest {
  accountId: string;
  amount: number;
  currency: string;
  description: string;
  adjustmentType: string; // CREDIT or DEBIT
  externalReference?: string;
}

export interface CreateWithdrawalTransactionRequest {
  sourceAccountId: string;
  amount: number;
  currency: string;
  description: string;
  channel: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateReleaseTransactionRequest {
  sourceAccountId: string;
  holdTransactionId: string;
  description: string;
  externalReference?: string;
}

export interface CreateTransferTransactionRequest {
  sourceAccountId: string;
  targetAccountId: string;
  amount: number;
  currency: string;
  description: string;
  idempotencyKey: string;
  externalReference?: string;
}

export interface CreateReversalTransactionRequest {
  description: string;
  reason: string;
  idempotencyKey: string;
}

export interface CreateRefundTransactionRequest {
  amount: number;
  description: string;
  reason: string;
  idempotencyKey: string;
}
