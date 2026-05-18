export interface AccountingPeriodResponse {
  id: string;
  periodCode: string;
  periodType: string;
  status: string;
  startDate: string;
  endDate: string;
  closedAt: string | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateAccountingPeriodRequest {
  periodCode: string;
  periodType: string;
  startDate: string;
  endDate: string;
  description?: string;
}

export interface CloseAccountingPeriodRequest {
  reason?: string;
}

export interface JournalLineResponse {
  id: string;
  lineNo: number;
  accountCode: string;
  accountName: string;
  lineType: string;
  amount: number;
  currency: string;
  description: string | null;
  createdAt: string;
}

export interface JournalEntryResponse {
  id: string;
  entryNumber: string;
  sourceTransactionId: string | null;
  entryType: string;
  status: string;
  description: string | null;
  reference: string | null;
  totalDebits: number;
  totalCredits: number;
  balanced: boolean;
  postedAt: string | null;
  createdAt: string;
  updatedAt: string;
  lines: JournalLineResponse[];
}
