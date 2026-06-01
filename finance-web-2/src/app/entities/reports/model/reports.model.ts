export type ReportMode = 'ANALYTIC' | 'MANAGERIAL';
export type ReportOutput = 'SCREEN' | 'PDF' | 'XLSX';
export type ReportVisualizationType = string;
export type ReportFieldType = string;
export type ReportOperator = string;
export type ReportSortTargetType = string;
export type SortDirection = 'ASC' | 'DESC';

export interface ReportCatalogResponse {
  mode: ReportMode;
  reports: ReportCatalogItemResponse[];
}

export interface ReportCatalogItemResponse {
  key: string;
  label: string;
  description: string;
}

export interface ReportFieldResponse {
  key: string;
  label: string;
  type: ReportFieldType;
  operators: ReportOperator[];
  options: string[];
}

export interface ReportMetricResponse {
  key: string;
  label: string;
  type: ReportFieldType;
}

export interface ReportSortResponse {
  targetType: ReportSortTargetType;
  field: string | null;
  metric: string | null;
  direction: SortDirection;
}

export interface ReportModeDefaultsResponse {
  columns: string[];
  groupBy: string[];
  metrics: string[];
  visualizations: ReportVisualizationType[];
  sort: ReportSortResponse[];
  outputs: ReportOutput[];
}

export interface ReportExportOptionsResponse {
  pdfOrientation: string;
  pdfPageSize: string;
  pdfIncludeHeader: boolean;
  pdfIncludeAppliedFilters: boolean;
  xlsxIncludeHeader: boolean;
  xlsxIncludeAppliedFiltersSheet: boolean;
  xlsxFreezeHeader: boolean;
  xlsxAutoSizeColumns: boolean;
}

export interface ReportLimitsResponse {
  maxRows: number;
  maxColumns: number;
  maxGroupBy: number;
  maxMetrics: number;
}

export interface ReportSchemaResponse {
  reportType: string;
  mode: ReportMode;
  title: string;
  description: string;
  filters: ReportFieldResponse[];
  columns: ReportFieldResponse[];
  groupBy: ReportFieldResponse[];
  metrics: ReportMetricResponse[];
  visualizations: ReportVisualizationType[];
  defaults: ReportModeDefaultsResponse[];
  outputs: ReportOutput[];
  exportOptions: ReportExportOptionsResponse;
  limits: ReportLimitsResponse;
}

export interface ReportColumnResponse {
  key: string;
  label: string;
  type: ReportFieldType;
}

export interface ReportHeaderResponse {
  title: string;
  mode: ReportMode;
  modeLabel: string;
  generatedAt: string;
  generatedBy: string;
  appliedFilters: string[];
  selectedColumns: string[];
  selectedMetrics: string[];
  selectedGroupBy: string[];
}

export interface ReportGeneratedFileResponse {
  output: ReportOutput;
  fileName: string;
  contentType: string;
  base64: string;
  fileSizeBytes: number;
}

export interface ReportResultMetadataResponse {
  rowCount: number;
  limit: number | null;
  offset: number | null;
}

export interface ReportResultResponse {
  reportType: string;
  mode: ReportMode;
  outputs: ReportOutput[];
  header: ReportHeaderResponse;
  columns: ReportColumnResponse[];
  rows: Array<Record<string, unknown>>;
  files: ReportGeneratedFileResponse[];
  metadata: ReportResultMetadataResponse;
}

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

export interface ReportExecutionSummaryResponse {
  id: string;
  reportType: string;
  reportTitle: string;
  executionName: string;
  mode: ReportMode;
  requestedBySubject: string;
  outputs: ReportOutput[];
  rowCount: number;
  status: 'COMPLETED' | 'FAILED' | string;
  createdAt: string;
  rerun: boolean;
  sourceExecutionId: string | null;
}

export interface ReportExportSummaryResponse {
  id: string;
  output: ReportOutput;
  fileName: string;
  contentType: string;
  fileSizeBytes: number;
  createdAt: string;
}

export interface ReportExecutionDetailResponse {
  id: string;
  reportType: string;
  reportTitle: string;
  executionName: string;
  mode: ReportMode;
  requestedBySubject: string;
  requestPayload: string;
  outputs: ReportOutput[];
  rowCount: number;
  status: 'COMPLETED' | 'FAILED' | string;
  errorMessage: string | null;
  sourceExecutionId: string | null;
  exports: ReportExportSummaryResponse[];
  createdAt: string;
}
