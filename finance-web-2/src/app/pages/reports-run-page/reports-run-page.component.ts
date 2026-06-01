import { CommonModule } from '@angular/common';
import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { LucideAngularModule } from 'lucide-angular';
import { ReportsDashboardUseCase } from '../../features/reports/application/reports-dashboard.usecase';
import {
  ReportFieldResponse,
  ReportMode,
  ReportOutput,
  ReportGeneratedFileResponse,
  ReportSchemaResponse
} from '../../entities/reports/model/reports.model';

@Component({
  selector: 'app-reports-run-page',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      @if (reportsUseCase.schemaStatus() === 'loading') {
        <section class="rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
          <div class="space-y-3">
            <div class="h-5 w-40 animate-pulse rounded bg-[#E8F5E9]"></div>
            <div class="h-8 w-96 animate-pulse rounded bg-[#E8F5E9]"></div>
            <div class="h-4 w-80 animate-pulse rounded bg-[#E8F5E9]"></div>
          </div>
        </section>
      } @else if (reportsUseCase.schemaStatus() === 'error') {
        <section class="rounded-[24px] border border-red-200 bg-red-50 p-6 text-sm text-red-700">
          {{ reportsUseCase.schemaError() }}
        </section>
      } @else if (reportsUseCase.selectedSchema(); as schema) {
        <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
          <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
            <div class="space-y-3">
              <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
                <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
                Ejecución
              </div>
              <div>
                <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">{{ schema.title }}</h1>
                <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                  {{ schema.description }}
                </p>
              </div>
            </div>

            <div class="flex flex-wrap gap-2">
              <button
                type="button"
                (click)="goToExplorer()"
                class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                Explorador
              </button>
              <button
                type="button"
                (click)="goToHistory()"
                class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                Historial
              </button>
            </div>
          </div>

          <div class="mt-5 grid gap-3 sm:grid-cols-3">
            <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
              <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Modo</p>
              <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ schema.mode }}</p>
            </div>
            <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
              <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Tipo</p>
              <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ schema.reportType }}</p>
            </div>
            <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
              <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Outputs</p>
              <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ schema.outputs.join(', ') }}</p>
            </div>
          </div>
        </section>

        <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
          <div class="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <h2 class="text-lg font-bold text-[#1B5E20]">Ejecutar reporte</h2>
              <p class="text-sm text-[#6B7D6C]">Construye el payload desde el schema y envíalo al backend</p>
            </div>
            <div class="flex flex-wrap gap-2">
              <button
                type="button"
                (click)="resetComposer(schema)"
                class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                Reiniciar
              </button>
              <button
                type="button"
                (click)="runSelectedReport(schema)"
                [disabled]="reportsUseCase.runStatus() === 'loading'"
                class="cursor-pointer rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:cursor-not-allowed disabled:opacity-50">
                {{ reportsUseCase.runStatus() === 'loading' ? 'Ejecutando...' : 'Ejecutar reporte' }}
              </button>
            </div>
          </div>

          @if (reportsUseCase.runStatus() === 'error') {
            <div class="mt-4 rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
              {{ reportsUseCase.runError() }}
            </div>
          }

          <div class="mt-5 grid gap-4 xl:grid-cols-[1fr_0.95fr]">
            <div class="space-y-4">
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <div class="flex items-center justify-between gap-3">
                  <p class="text-sm font-semibold text-[#567157]">Outputs</p>
                  <span class="text-xs text-[#6B7D6C]">Requerido</span>
                </div>
                <div class="mt-3 flex flex-wrap gap-2">
                  @for (output of schema.outputs; track output) {
                    <button
                      type="button"
                      (click)="toggleOutput(output)"
                      class="cursor-pointer rounded-full border px-3 py-2 text-sm font-semibold transition-colors"
                      [class.border-[#2E7D32]]="selectedOutputs().includes(output)"
                      [class.bg-[#2E7D32]]="selectedOutputs().includes(output)"
                      [class.text-white]="selectedOutputs().includes(output)"
                      [class.border-[#C8E6C9]]="!selectedOutputs().includes(output)"
                      [class.bg-white]="!selectedOutputs().includes(output)"
                      [class.text-[#2E7D32]]="!selectedOutputs().includes(output)">
                      {{ output }}
                    </button>
                  }
                </div>
              </div>

              @if (schema.mode === 'ANALYTIC') {
                <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                  <p class="text-sm font-semibold text-[#567157]">Columnas</p>
                  <div class="mt-3 grid gap-2 md:grid-cols-2">
                    @for (field of schema.columns; track field.key) {
                      <label class="flex cursor-pointer items-center gap-3 rounded-xl border border-[#E8F2E2] bg-white px-3 py-2 text-sm text-[#4F5D4F]">
                        <input type="checkbox" [checked]="selectedColumns().includes(field.key)" (change)="toggleSelection(selectedColumns, field.key)" class="h-4 w-4 accent-[#2E7D32]">
                        <span class="min-w-0 break-words">{{ field.label }}</span>
                      </label>
                    }
                  </div>
                </div>
              } @else {
                <div class="space-y-4">
                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <p class="text-sm font-semibold text-[#567157]">GroupBy</p>
                    <div class="mt-3 grid gap-2 md:grid-cols-2">
                      @for (field of schema.groupBy; track field.key) {
                        <label class="flex cursor-pointer items-center gap-3 rounded-xl border border-[#E8F2E2] bg-white px-3 py-2 text-sm text-[#4F5D4F]">
                          <input type="checkbox" [checked]="selectedGroupBy().includes(field.key)" (change)="toggleSelection(selectedGroupBy, field.key)" class="h-4 w-4 accent-[#2E7D32]">
                          <span class="min-w-0 break-words">{{ field.label }}</span>
                        </label>
                      }
                    </div>
                  </div>
                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <p class="text-sm font-semibold text-[#567157]">Métricas</p>
                    <div class="mt-3 grid gap-2 md:grid-cols-2">
                      @for (metric of schema.metrics; track metric.key) {
                        <label class="flex cursor-pointer items-center gap-3 rounded-xl border border-[#E8F2E2] bg-white px-3 py-2 text-sm text-[#4F5D4F]">
                          <input type="checkbox" [checked]="selectedMetrics().includes(metric.key)" (change)="toggleSelection(selectedMetrics, metric.key)" class="h-4 w-4 accent-[#2E7D32]">
                          <span class="min-w-0 break-words">{{ metric.label }}</span>
                        </label>
                      }
                    </div>
                  </div>
                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <p class="text-sm font-semibold text-[#567157]">Visualizaciones</p>
                    <div class="mt-3 flex flex-wrap gap-2">
                      @for (visualization of schema.visualizations; track visualization) {
                        <button
                          type="button"
                          (click)="toggleVisualization(visualization)"
                          class="cursor-pointer rounded-full border px-3 py-2 text-sm font-semibold transition-colors"
                          [class.border-[#2E7D32]]="selectedVisualizations().includes(visualization)"
                          [class.bg-[#2E7D32]]="selectedVisualizations().includes(visualization)"
                          [class.text-white]="selectedVisualizations().includes(visualization)"
                          [class.border-[#C8E6C9]]="!selectedVisualizations().includes(visualization)"
                          [class.bg-white]="!selectedVisualizations().includes(visualization)"
                          [class.text-[#2E7D32]]="!selectedVisualizations().includes(visualization)">
                          {{ visualization }}
                        </button>
                      }
                    </div>
                  </div>
                </div>
              }
            </div>

            <div class="space-y-4">
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <div class="flex items-center justify-between gap-3">
                  <p class="text-sm font-semibold text-[#567157]">Filtros</p>
                  <button type="button" (click)="addFilter(schema.filters)" class="cursor-pointer text-sm font-semibold text-[#2E7D32] hover:underline">Agregar</button>
                </div>
                <div class="mt-3 space-y-3">
                  @for (filter of filters(); track $index) {
                    <div class="grid gap-2 rounded-2xl border border-[#E8F2E2] bg-white p-3">
                      <div class="grid gap-2 md:grid-cols-2">
                        <select class="rounded-xl border border-[#DDEED8] px-3 py-2 text-sm" [value]="filter.field" (change)="updateFilterField($index, ($any($event.target)).value, schema.filters)">
                          <option value="">Campo</option>
                          @for (field of schema.filters; track field.key) {
                            <option [value]="field.key">{{ field.label }}</option>
                          }
                        </select>
                        <select class="rounded-xl border border-[#DDEED8] px-3 py-2 text-sm" [value]="filter.operator" (change)="updateFilterOperator($index, ($any($event.target)).value)">
                          <option value="">Operador</option>
                          @for (operator of operatorsForFilter(filter.field, schema.filters); track operator) {
                            <option [value]="operator">{{ operator }}</option>
                          }
                        </select>
                      </div>
                      <div class="grid gap-2 md:grid-cols-2">
                        <input class="rounded-xl border border-[#DDEED8] px-3 py-2 text-sm" [value]="filter.value" (input)="updateFilterValue($index, ($any($event.target)).value)" placeholder="Valor">
                        <input class="rounded-xl border border-[#DDEED8] px-3 py-2 text-sm" [value]="filter.to" (input)="updateFilterTo($index, ($any($event.target)).value)" placeholder="Hasta">
                      </div>
                      <div class="flex justify-end">
                        <button type="button" (click)="removeFilter($index)" class="cursor-pointer text-sm font-semibold text-red-600 hover:underline">Quitar</button>
                      </div>
                    </div>
                  }
                </div>
              </div>

              <div class="grid gap-3 sm:grid-cols-2">
                <label class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                  <span class="block text-sm font-semibold text-[#567157]">Limit</span>
                  <input type="number" min="1" [value]="paginationLimit()" (input)="setPaginationLimit(($any($event.target)).value)" class="mt-2 w-full rounded-xl border border-[#DDEED8] px-3 py-2 text-sm">
                </label>
                <label class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                  <span class="block text-sm font-semibold text-[#567157]">Offset</span>
                  <input type="number" min="0" [value]="paginationOffset()" (input)="setPaginationOffset(($any($event.target)).value)" class="mt-2 w-full rounded-xl border border-[#DDEED8] px-3 py-2 text-sm">
                </label>
              </div>

              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <p class="text-sm font-semibold text-[#567157]">Preview payload</p>
                <pre class="mt-3 max-h-96 overflow-auto rounded-xl bg-white p-3 text-xs leading-5 text-[#4F5D4F]">{{ buildPreviewPayload(schema) | json }}</pre>
              </div>
            </div>
          </div>
        </section>

        @if (reportsUseCase.lastRunResult(); as runResult) {
          <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
            <div class="flex items-center justify-between gap-3">
              <div>
                <h2 class="text-lg font-bold text-[#1B5E20]">Última ejecución</h2>
                <p class="text-sm text-[#6B7D6C]">{{ runResult.header.title }} · {{ runResult.header.mode }} · {{ runResult.metadata.rowCount }} filas</p>
              </div>
              <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ runResult.outputs.join(', ') }}</span>
            </div>

            <div class="mt-4 overflow-x-auto rounded-2xl border border-[#E8F2E2]">
              <table class="min-w-[840px] w-full divide-y divide-[#E8F2E2]">
                <thead class="bg-[#F7FBF3]">
                  <tr class="text-left text-xs font-bold uppercase tracking-[0.12em] text-[#6B7D6C]">
                    @for (column of runResult.columns; track column.key) {
                      <th class="px-4 py-3">{{ column.label }}</th>
                    }
                  </tr>
                </thead>
                <tbody class="divide-y divide-[#EEF5EA] bg-white">
                  @for (row of runResult.rows.slice(0, 10); track $index) {
                    <tr>
                      @for (column of runResult.columns; track column.key) {
                        <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ renderRowValue(row, column.key) }}</td>
                      }
                    </tr>
                  }
                </tbody>
              </table>
            </div>

            @if (runResult.files.length > 0) {
              <div class="mt-4 space-y-2">
                <h3 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">Archivos generados</h3>
                <div class="grid gap-3 md:grid-cols-2">
                  @for (file of runResult.files; track file.fileName) {
                    <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                      <div class="flex items-start justify-between gap-3">
                        <div>
                          <p class="font-semibold text-[#1B5E20]">{{ file.fileName }}</p>
                          <p class="mt-1 text-xs text-[#6B7D6C]">{{ file.output }} · {{ file.contentType }}</p>
                          <p class="mt-1 text-xs text-[#6B7D6C]">{{ formatBytes(file.fileSizeBytes) }}</p>
                        </div>
                        <button
                          type="button"
                          (click)="downloadGeneratedFile(file)"
                          class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-3 py-1.5 text-xs font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                          Descargar
                        </button>
                      </div>
                    </div>
                  }
                </div>
              </div>
            }
          </section>
        }
      } @else {
        <section class="rounded-[24px] border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-6 text-sm text-[#5F6F5F]">
          Cargando el schema del reporte...
        </section>
      }
    </div>
  `
})
export class ReportsRunPageComponent implements OnInit {
  readonly reportsUseCase = inject(ReportsDashboardUseCase);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly selectedColumns = signal<string[]>([]);
  readonly selectedGroupBy = signal<string[]>([]);
  readonly selectedMetrics = signal<string[]>([]);
  readonly selectedVisualizations = signal<string[]>([]);
  readonly selectedOutputs = signal<ReportOutput[]>(['SCREEN']);
  readonly filters = signal<Array<{ field: string; operator: string; value: string; to: string }>>([]);
  readonly paginationLimit = signal<number>(20);
  readonly paginationOffset = signal<number>(0);

  readonly currentMode = signal<ReportMode>('ANALYTIC');
  readonly currentReportType = signal<string>('');

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const modeParam = String(params.get('mode') ?? '').toLowerCase();
      const reportType = String(params.get('reportType') ?? '').trim().toUpperCase();

      if (!modeParam || !reportType || (modeParam !== 'analytic' && modeParam !== 'managerial')) {
        void this.router.navigate(['/dashboard/reports']);
        return;
      }

      const mode = modeParam === 'analytic' ? 'ANALYTIC' : 'MANAGERIAL';
      this.currentMode.set(mode);
      this.currentReportType.set(reportType);
      void this.loadReport(mode, reportType);
    });
  }

  private async loadReport(mode: ReportMode, reportType: string): Promise<void> {
    this.reportsUseCase.clearSelectedExecution();
    await this.reportsUseCase.selectReport(mode, {
      key: reportType,
      label: reportType,
      description: ''
    });

    const schema = this.reportsUseCase.selectedSchema();
    if (schema) {
      this.resetComposer(schema);
    }
  }

  goToExplorer(): void {
    void this.router.navigate(['/dashboard/reports']);
  }

  goToHistory(): void {
    void this.router.navigate(['/dashboard/reports/history']);
  }

  resetComposer(schema?: ReportSchemaResponse | null): void {
    const currentSchema = schema ?? this.reportsUseCase.selectedSchema();
    if (!currentSchema) {
      return;
    }

    const defaults = currentSchema.defaults?.[0];
    this.selectedColumns.set(defaults?.columns?.length ? defaults.columns.map(String) : currentSchema.columns.map((item) => item.key));
    this.selectedGroupBy.set(defaults?.groupBy?.length ? defaults.groupBy.map(String) : currentSchema.groupBy.map((item) => item.key));
    this.selectedMetrics.set(defaults?.metrics?.length ? defaults.metrics.map(String) : currentSchema.metrics.map((item) => item.key));
    this.selectedVisualizations.set(defaults?.visualizations?.length ? defaults.visualizations.slice() : currentSchema.visualizations.slice());
    this.selectedOutputs.set(defaults?.outputs?.length ? defaults.outputs.slice() : [currentSchema.outputs[0] ?? 'SCREEN']);
    this.filters.set([]);
    this.paginationLimit.set(20);
    this.paginationOffset.set(0);
  }

  toggleOutput(output: ReportOutput): void {
    const current = this.selectedOutputs();
    this.selectedOutputs.set(current.includes(output) ? current.filter((item) => item !== output) : [...current, output]);
  }

  toggleVisualization(visualization: string): void {
    const current = this.selectedVisualizations();
    this.selectedVisualizations.set(current.includes(visualization) ? current.filter((item) => item !== visualization) : [...current, visualization]);
  }

  toggleSelection(signalRef: { (): string[]; set: (value: string[]) => void }, value: string): void {
    const current = signalRef();
    signalRef.set(current.includes(value) ? current.filter((item) => item !== value) : [...current, value]);
  }

  addFilter(fields: ReportFieldResponse[]): void {
    this.filters.set([...this.filters(), { field: fields[0]?.key ?? '', operator: '', value: '', to: '' }]);
  }

  updateFilterField(index: number, field: string, schemaFields: ReportFieldResponse[]): void {
    const current = [...this.filters()];
    const selectedField = schemaFields.find((item) => item.key === field);
    current[index] = {
      ...current[index],
      field,
      operator: selectedField?.operators?.[0] ?? '',
      value: '',
      to: ''
    };
    this.filters.set(current);
  }

  updateFilterOperator(index: number, operator: string): void {
    const current = [...this.filters()];
    current[index] = { ...current[index], operator };
    this.filters.set(current);
  }

  updateFilterValue(index: number, value: string): void {
    const current = [...this.filters()];
    current[index] = { ...current[index], value };
    this.filters.set(current);
  }

  updateFilterTo(index: number, value: string): void {
    const current = [...this.filters()];
    current[index] = { ...current[index], to: value };
    this.filters.set(current);
  }

  removeFilter(index: number): void {
    this.filters.set(this.filters().filter((_, currentIndex) => currentIndex !== index));
  }

  setPaginationLimit(value: string): void {
    const parsed = Number(value);
    this.paginationLimit.set(Number.isFinite(parsed) && parsed > 0 ? Math.floor(parsed) : 20);
  }

  setPaginationOffset(value: string): void {
    const parsed = Number(value);
    this.paginationOffset.set(Number.isFinite(parsed) && parsed >= 0 ? Math.floor(parsed) : 0);
  }

  operatorsForFilter(fieldKey: string, fields: ReportFieldResponse[]): string[] {
    return fields.find((item) => item.key === fieldKey)?.operators ?? [];
  }

  buildPreviewPayload(schema: ReportSchemaResponse): Record<string, unknown> {
    const payload: Record<string, unknown> = {
      reportType: schema.reportType,
      outputs: this.selectedOutputs(),
      pagination: {
        limit: this.paginationLimit(),
        offset: this.paginationOffset()
      }
    };

    if (schema.mode === 'ANALYTIC') {
      return {
        ...payload,
        columns: this.selectedColumns(),
        filters: this.normalizeFilters(),
        sort: []
      };
    }

    return {
      ...payload,
      groupBy: this.selectedGroupBy(),
      metrics: this.selectedMetrics(),
      filters: this.normalizeFilters(),
      sort: [],
      visualizations: this.selectedVisualizations()
    };
  }

  async runSelectedReport(schema: ReportSchemaResponse): Promise<void> {
    if (this.selectedOutputs().length === 0) {
      this.selectedOutputs.set(['SCREEN']);
    }

    await this.reportsUseCase.runReport(this.buildPreviewPayload(schema));
  }

  private normalizeFilters(): Array<Record<string, unknown>> {
    return this.filters()
      .filter((filter) => !!filter.field && !!filter.operator)
      .map((filter) => {
        const payload: Record<string, unknown> = {
          field: filter.field,
          operator: filter.operator
        };

        if (filter.operator === 'BETWEEN') {
          payload['value'] = filter.value || null;
          payload['to'] = filter.to || null;
          return payload;
        }

        if (filter.operator === 'IN') {
          payload['value'] = filter.value
            .split(',')
            .map((item) => item.trim())
            .filter(Boolean);
          return payload;
        }

        payload['value'] = filter.value || null;
        return payload;
      });
  }

  prettyJson(value: string): string {
    try {
      const parsed = JSON.parse(value);
      return JSON.stringify(parsed, null, 2);
    } catch {
      return value || 'Sin payload';
    }
  }

  renderRowValue(row: Record<string, unknown>, key: string): string {
    const value = row[key];
    if (value === null || value === undefined) {
      return '-';
    }

    if (typeof value === 'object') {
      return JSON.stringify(value);
    }

    return String(value);
  }

  formatBytes(value: number): string {
    if (!Number.isFinite(value) || value <= 0) {
      return '0 B';
    }

    const units = ['B', 'KB', 'MB', 'GB'];
    let size = value;
    let unitIndex = 0;

    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024;
      unitIndex++;
    }

    return `${size.toFixed(size >= 10 || unitIndex === 0 ? 0 : 1)} ${units[unitIndex]}`;
  }

  downloadGeneratedFile(file: ReportGeneratedFileResponse): void {
    if (!file?.base64) {
      return;
    }

    const byteCharacters = atob(file.base64);
    const byteNumbers = Array.from(byteCharacters, (character) => character.charCodeAt(0));
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: file.contentType || 'application/octet-stream' });
    const url = window.URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = file.fileName || 'report-file';
    anchor.click();
    window.URL.revokeObjectURL(url);
  }
}
