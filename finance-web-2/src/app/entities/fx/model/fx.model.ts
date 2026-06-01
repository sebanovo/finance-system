export interface FxExchangeRateResponse {
  id: string;
  sourceCurrency: string;
  targetCurrency: string;
  rate: number;
  active: boolean;
  description: string;
  createdAt: string;
  updatedAt: string;
}

export interface OperationFeeResponse {
  id: string;
  operationCode: string;
  feeType: string;
  feeValue: number;
  calculationMode: string;
  active: boolean;
  description: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFxExchangeRateRequest {
  sourceCurrency: string;
  targetCurrency: string;
  rate: number;
  active: boolean;
  description?: string;
}

export interface UpdateFxExchangeRateRequest {
  sourceCurrency: string;
  targetCurrency: string;
  rate: number;
  active: boolean;
  description?: string;
}

export interface CreateOperationFeeRequest {
  operationCode: string;
  feeType: string;
  feeValue: number;
  calculationMode: string;
  active: boolean;
  description?: string;
}

export interface UpdateOperationFeeRequest {
  operationCode: string;
  feeType: string;
  feeValue: number;
  calculationMode: string;
  active: boolean;
  description?: string;
}
