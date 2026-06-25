export type LoanStatus =
  | 'REQUESTED'
  | 'APPROVED'
  | 'REJECTED'
  | 'DISBURSED'
  | 'PAID_OFF'
  | 'CANCELLED';

export type LoanInstallmentStatus = 'PENDING' | 'PARTIAL' | 'PAID' | 'OVERDUE';

export type InterestMethod = 'FLAT' | 'FRENCH';

export interface LoanResponse {
  id: string;
  userId: string;
  accountId: string;
  principal: number;
  currency: string;
  annualInterestRate: number;
  termMonths: number;
  interestMethod: InterestMethod;
  status: LoanStatus;
  purpose: string | null;
  statusReason: string | null;
  totalDue: number;
  totalPaid: number;
  outstandingBalance: number;
  disbursedAt: string | null;
  closedAt: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface LoanInstallmentResponse {
  id: string;
  number: number;
  dueDate: string;
  principalDue: number;
  interestDue: number;
  totalDue: number;
  paidAmount: number;
  status: LoanInstallmentStatus;
  paidAt: string | null;
}

export interface LoanDetailResponse {
  loan: LoanResponse;
  installments: LoanInstallmentResponse[];
}

export interface CreateLoanRequest {
  userId: string;
  accountId: string;
  principal: number;
  annualInterestRate: number;
  termMonths: number;
  interestMethod?: InterestMethod;
  purpose?: string | null;
}

export interface CreateMyLoanRequest {
  accountId: string;
  principal: number;
  annualInterestRate: number;
  termMonths: number;
  interestMethod?: InterestMethod;
  purpose?: string | null;
}

export interface RecordLoanPaymentRequest {
  amount: number;
  idempotencyKey?: string;
}
