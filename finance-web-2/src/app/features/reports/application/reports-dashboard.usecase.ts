import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ReportsService } from '../../../entities/reports/api/reports.service';
import {
  ReportCatalogItemResponse,
  ReportCatalogResponse,
  ReportExecutionDetailResponse,
  ReportExecutionSummaryResponse,
  PageResponse,
  ReportMode,
  ReportResultResponse,
  ReportSchemaResponse
} from '../../../entities/reports/model/reports.model';

type LoadState = 'idle' | 'loading' | 'success' | 'error';

interface ReportsDashboardState {
  status: LoadState;
  error: string | null;
  analyticCatalog: ReportCatalogResponse | null;
  managerialCatalog: ReportCatalogResponse | null;
  selectedMode: ReportMode | null;
  selectedReport: ReportCatalogItemResponse | null;
  selectedSchema: ReportSchemaResponse | null;
  schemaStatus: LoadState;
  schemaError: string | null;
  executionsPage: PageResponse<ReportExecutionSummaryResponse> | null;
  executionsStatus: LoadState;
  executionsError: string | null;
  selectedExecution: ReportExecutionDetailResponse | null;
  executionDetailStatus: LoadState;
  executionDetailError: string | null;
  executionPage: number;
  executionModeFilter: ReportMode | null;
  executionReportTypeFilter: string | null;
  lastRunResult: ReportResultResponse | null;
  runStatus: LoadState;
  runError: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class ReportsDashboardUseCase {
  private readonly reportsService = inject(ReportsService);

  private readonly state = signal<ReportsDashboardState>({
    status: 'idle',
    error: null,
    analyticCatalog: null,
    managerialCatalog: null,
    selectedMode: null,
    selectedReport: null,
    selectedSchema: null,
    schemaStatus: 'idle',
    schemaError: null,
    executionsPage: null,
    executionsStatus: 'idle',
    executionsError: null,
    selectedExecution: null,
    executionDetailStatus: 'idle',
    executionDetailError: null,
    executionPage: 0,
    executionModeFilter: null,
    executionReportTypeFilter: null,
    lastRunResult: null,
    runStatus: 'idle',
    runError: null
  });

  readonly status = computed(() => this.state().status);
  readonly error = computed(() => this.state().error);
  readonly analyticCatalog = computed(() => this.state().analyticCatalog);
  readonly managerialCatalog = computed(() => this.state().managerialCatalog);
  readonly selectedMode = computed(() => this.state().selectedMode);
  readonly selectedReport = computed(() => this.state().selectedReport);
  readonly selectedSchema = computed(() => this.state().selectedSchema);
  readonly schemaStatus = computed(() => this.state().schemaStatus);
  readonly schemaError = computed(() => this.state().schemaError);
  readonly executionsPage = computed(() => this.state().executionsPage);
  readonly executionsStatus = computed(() => this.state().executionsStatus);
  readonly executionsError = computed(() => this.state().executionsError);
  readonly selectedExecution = computed(() => this.state().selectedExecution);
  readonly executionDetailStatus = computed(() => this.state().executionDetailStatus);
  readonly executionDetailError = computed(() => this.state().executionDetailError);
  readonly executionPage = computed(() => this.state().executionPage);
  readonly executionModeFilter = computed(() => this.state().executionModeFilter);
  readonly executionReportTypeFilter = computed(() => this.state().executionReportTypeFilter);
  readonly lastRunResult = computed(() => this.state().lastRunResult);
  readonly runStatus = computed(() => this.state().runStatus);
  readonly runError = computed(() => this.state().runError);

  async loadCatalogs(): Promise<void> {
    this.state.update((current) => ({ ...current, status: 'loading', error: null }));

    try {
      const [analytic, managerial] = await Promise.all([
        firstValueFrom(this.reportsService.getAnalyticCatalog()),
        firstValueFrom(this.reportsService.getManagerialCatalog())
      ]);

      if (!analytic.success || !managerial.success) {
        this.state.update((current) => ({
          ...current,
          status: 'error',
          error: analytic.message || managerial.message || 'No se pudieron cargar los reportes'
        }));
        return;
      }

      this.state.update((current) => ({
        ...current,
        status: 'success',
        error: null,
        analyticCatalog: analytic.data ?? null,
        managerialCatalog: managerial.data ?? null
      }));
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.update((current) => ({
        ...current,
        status: 'error',
        error: errorMsg
      }));
    }
  }

  async selectReport(mode: ReportMode, report: ReportCatalogItemResponse): Promise<void> {
    this.state.update((current) => ({
      ...current,
      selectedMode: mode,
      selectedReport: report,
      selectedSchema: null,
      schemaStatus: 'loading',
      schemaError: null
    }));

    try {
      const response = await firstValueFrom(this.reportsService.getSchema(mode, report.key));
      if (response.success && response.data) {
        this.state.update((current) => ({
          ...current,
          selectedSchema: response.data,
          schemaStatus: 'success',
          schemaError: null
        }));
        return;
      }

      this.state.update((current) => ({
        ...current,
        schemaStatus: 'error',
        schemaError: response.message || 'No se pudo cargar el schema'
      }));
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al cargar el schema';
      this.state.update((current) => ({
        ...current,
        schemaStatus: 'error',
        schemaError: errorMsg
      }));
    }
  }

  clearSelectedReport(): void {
    this.state.update((current) => ({
      ...current,
      selectedMode: null,
      selectedReport: null,
      selectedSchema: null,
      schemaStatus: 'idle',
      schemaError: null,
      lastRunResult: null,
      runStatus: 'idle',
      runError: null
    }));
  }

  resetExecutionFilters(): void {
    this.state.update((current) => ({
      ...current,
      executionModeFilter: null,
      executionReportTypeFilter: null,
      executionPage: 0
    }));
  }

  async loadExecutions(page = 0): Promise<void> {
    this.state.update((current) => ({ ...current, executionsStatus: 'loading', executionsError: null, executionPage: page }));

    try {
      const current = this.state();
      const response = await firstValueFrom(this.reportsService.getExecutions({
        reportType: current.executionReportTypeFilter ?? undefined,
        mode: current.executionModeFilter ?? undefined,
        page,
        size: 8
      }));

      if (response.success && response.data) {
        this.state.update((state) => ({
          ...state,
          executionsStatus: 'success',
          executionsError: null,
          executionsPage: response.data
        }));
        return;
      }

      this.state.update((state) => ({
        ...state,
        executionsStatus: 'error',
        executionsError: response.message || 'No se pudo cargar el historial'
      }));
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al cargar el historial';
      this.state.update((state) => ({
        ...state,
        executionsStatus: 'error',
        executionsError: errorMsg
      }));
    }
  }

  async selectExecution(executionId: string): Promise<void> {
    this.state.update((current) => ({
      ...current,
      selectedExecution: null,
      executionDetailStatus: 'loading',
      executionDetailError: null
    }));

    try {
      const response = await firstValueFrom(this.reportsService.getExecutionById(executionId));
      if (response.success && response.data) {
        this.state.update((current) => ({
          ...current,
          selectedExecution: response.data,
          executionDetailStatus: 'success',
          executionDetailError: null
        }));
        return;
      }

      this.state.update((current) => ({
        ...current,
        executionDetailStatus: 'error',
        executionDetailError: response.message || 'No se pudo cargar el detalle'
      }));
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al cargar el detalle';
      this.state.update((current) => ({
        ...current,
        executionDetailStatus: 'error',
        executionDetailError: errorMsg
      }));
    }
  }

  setExecutionModeFilter(mode: ReportMode | null): void {
    this.state.update((current) => ({
      ...current,
      executionModeFilter: mode,
      executionPage: 0
    }));
  }

  setExecutionReportTypeFilter(reportType: string | null): void {
    this.state.update((current) => ({
      ...current,
      executionReportTypeFilter: reportType,
      executionPage: 0
    }));
  }

  clearSelectedExecution(): void {
    this.state.update((current) => ({
      ...current,
      selectedExecution: null,
      executionDetailStatus: 'idle',
      executionDetailError: null
    }));
  }

  async runReport(payload: Record<string, unknown>): Promise<void> {
    const current = this.state();
    if (!current.selectedSchema || !current.selectedMode || !current.selectedReport) {
      this.state.update((state) => ({
        ...state,
        runStatus: 'error',
        runError: 'Selecciona un reporte antes de ejecutar'
      }));
      return;
    }

    this.state.update((state) => ({
      ...state,
      runStatus: 'loading',
      runError: null,
      lastRunResult: null
    }));

    try {
      const response = await firstValueFrom(
        current.selectedMode === 'ANALYTIC'
          ? this.reportsService.runAnalyticReport(current.selectedReport.key, payload)
          : this.reportsService.runManagerialReport(current.selectedReport.key, payload)
      );

      if (response.success && response.data) {
        this.state.update((state) => ({
          ...state,
          runStatus: 'success',
          runError: null,
          lastRunResult: response.data
        }));
        return;
      }

      this.state.update((state) => ({
        ...state,
        runStatus: 'error',
        runError: response.message || 'No se pudo ejecutar el reporte'
      }));
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al ejecutar el reporte';
      this.state.update((state) => ({
        ...state,
        runStatus: 'error',
        runError: errorMsg
      }));
    }
  }
}
