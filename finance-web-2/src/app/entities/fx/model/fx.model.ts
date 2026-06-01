export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export type CurrencyCode = 'BOB' | 'USD' | 'EUR' | 'USDT';
export type FxOperationCode = 'TRANSFER' | 'CONVERSION' | 'DEPOSIT' | 'WITHDRAWAL' | 'PAYMENT';
export type FeeType = 'NONE' | 'FIXED' | 'PERCENTAGE';
export type FeeCalculationMode = 'SEPARATE' | 'INCLUDED';

export interface FxExchangeRateResponse {
  id: string;
  sourceCurrency: CurrencyCode;
  targetCurrency: CurrencyCode;
  rate: number;
  active: boolean;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface OperationFeeResponse {
  id: string;
  operationCode: FxOperationCode;
  feeType: FeeType;
  feeValue: number;
  calculationMode: FeeCalculationMode;
  active: boolean;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFxExchangeRateRequest {
  sourceCurrency: CurrencyCode;
  targetCurrency: CurrencyCode;
  rate: number;
  active: boolean;
  description?: string | null;
}

export interface UpdateFxExchangeRateRequest {
  sourceCurrency: CurrencyCode;
  targetCurrency: CurrencyCode;
  rate: number;
  active: boolean;
  description?: string | null;
}

export interface CreateOperationFeeRequest {
  operationCode: FxOperationCode;
  feeType: FeeType;
  feeValue: number;
  calculationMode: FeeCalculationMode;
  active: boolean;
  description?: string | null;
}

export interface UpdateOperationFeeRequest {
  operationCode: FxOperationCode;
  feeType: FeeType;
  feeValue: number;
  calculationMode: FeeCalculationMode;
  active: boolean;
  description?: string | null;
}
