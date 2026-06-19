// New reporting module (Fase 5) — DTOs matching finance-api /api/reports/** and
// /api/platform/reports/**. Kept separate from the legacy entities/reports model.

export type ReportingScope = 'tenant' | 'platform';

export type ReportDataType = 'TEXT' | 'NUMBER' | 'BOOLEAN' | 'DATE' | 'TIMESTAMP';

export interface ReportParam {
  name: string;
  type: ReportDataType;
  operator: string;
  required: boolean;
  options?: string[];
}

export interface ReportSort {
  field: string;
  direction: 'ASC' | 'DESC';
}

export interface ReportDefinition {
  key: string;
  title: string;
  description: string;
  scope: 'TENANT' | 'GLOBAL';
  params: ReportParam[];
  defaultSort: ReportSort | null;
}

export interface ReportColumn {
  name: string;
  type: ReportDataType;
}

export interface ReportResult {
  executionId: string;
  kind: 'CONTROLLED' | 'AI';
  columns: ReportColumn[];
  rows: unknown[][];
  rowCount: number;
  truncated: boolean;
  explanation: string | null;
  transcript: string | null;
  schemaUsed: string;
  limitKind: 'USER' | 'SYSTEM';
}

export interface ReportExecutionSummary {
  id: string;
  kind: 'CONTROLLED' | 'AI';
  definitionKey: string | null;
  status: 'PENDING' | 'SUCCESS' | 'FAILED';
  scope: 'TENANT' | 'PLATFORM';
  tenantSlug: string | null;
  rowCount: number | null;
  truncated: boolean;
  createdAt: string;
  executedAt: string | null;
}

export interface ReportExecutionDetail {
  summary: ReportExecutionSummary;
  columns: ReportColumn[];
  rows: unknown[][];
  sql: string | null;
  referencedViews: string[];
  prompt: string | null;
  transcript: string | null;
}

export interface ReportExport {
  id: string;
  executionId: string;
  format: 'PDF' | 'XLSX';
  mode: 'SNAPSHOT' | 'FULL';
  fileName: string;
  contentType: string;
  fileSizeBytes: number | null;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}
