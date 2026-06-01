import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { firstValueFrom } from 'rxjs';
import { ReportsService } from '../../entities/reports/api/reports.service';
import { ReportsDashboardUseCase } from '../../features/reports/application/reports-dashboard.usecase';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { ReportMode } from '../../entities/reports/model/reports.model';

@Component({
  selector: 'app-reports-history-page',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, PlatformPaginationComponent],
  template: `
    <div class="space-y-6">
      <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="space-y-3">
            <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
              <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
              Reportes
            </div>
            <div>
              <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">Historial de ejecuciones</h1>
              <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                Consulta las ejecuciones recientes sin mezclar el historial con el explorador o el formulario de ejecución.
              </p>
            </div>
          </div>

          <button
            type="button"
            (click)="goToExplorer()"
            class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
            Volver al explorador
          </button>
        </div>
      </section>

      <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
        <div class="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <h2 class="text-lg font-bold text-[#1B5E20]">Filtros</h2>
            <p class="text-sm text-[#6B7D6C]">Filtra por modo o por tipo de reporte</p>
          </div>

          <div class="flex flex-wrap gap-2">
            <button
              type="button"
              (click)="setExecutionMode(null)"
              class="cursor-pointer rounded-full border border-[#C8E6C9] px-4 py-2 text-sm font-semibold transition-colors"
              [class.bg-[#2E7D32]]="reportsUseCase.executionModeFilter() === null"
              [class.text-white]="reportsUseCase.executionModeFilter() === null"
              [class.bg-white]="reportsUseCase.executionModeFilter() !== null"
              [class.text-[#2E7D32]]="reportsUseCase.executionModeFilter() !== null">
              Todos
            </button>
            <button
              type="button"
              (click)="setExecutionMode('ANALYTIC')"
              class="cursor-pointer rounded-full border border-[#C8E6C9] px-4 py-2 text-sm font-semibold transition-colors"
              [class.bg-[#2E7D32]]="reportsUseCase.executionModeFilter() === 'ANALYTIC'"
              [class.text-white]="reportsUseCase.executionModeFilter() === 'ANALYTIC'"
              [class.bg-white]="reportsUseCase.executionModeFilter() !== 'ANALYTIC'"
              [class.text-[#2E7D32]]="reportsUseCase.executionModeFilter() !== 'ANALYTIC'">
              Analíticos
            </button>
            <button
              type="button"
              (click)="setExecutionMode('MANAGERIAL')"
              class="cursor-pointer rounded-full border border-[#C8E6C9] px-4 py-2 text-sm font-semibold transition-colors"
              [class.bg-[#2E7D32]]="reportsUseCase.executionModeFilter() === 'MANAGERIAL'"
              [class.text-white]="reportsUseCase.executionModeFilter() === 'MANAGERIAL'"
              [class.bg-white]="reportsUseCase.executionModeFilter() !== 'MANAGERIAL'"
              [class.text-[#2E7D32]]="reportsUseCase.executionModeFilter() !== 'MANAGERIAL'">
              Gerenciales
            </button>
          </div>
        </div>

        <div class="mt-4 grid gap-4 lg:grid-cols-[1fr_auto]">
          <label class="block">
            <span class="mb-1 block text-sm font-semibold text-[#567157]">Filtrar por tipo de reporte</span>
            <input
              type="text"
              [value]="reportTypeFilterValue()"
              (input)="setExecutionReportType(($any($event.target)).value)"
              placeholder="Ej: ACCOUNTS"
              class="w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 py-3 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
          </label>

          <div class="flex items-end">
            <button
              type="button"
              (click)="reloadExecutions()"
              class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-3 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              Recargar historial
            </button>
          </div>
        </div>

        @if (reportsUseCase.executionsStatus() === 'loading') {
          <div class="mt-5 space-y-3">
            @for (item of loadingSkeleton; track item) {
              <div class="h-16 animate-pulse rounded-2xl bg-[#E8F5E9]"></div>
            }
          </div>
        } @else if (reportsUseCase.executionsStatus() === 'error') {
          <div class="mt-5 rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            {{ reportsUseCase.executionsError() }}
          </div>
        } @else if (reportsUseCase.executionsPage(); as executionPage) {
          <div class="mt-5 overflow-x-auto rounded-2xl border border-[#E8F2E2]">
            <table class="min-w-[980px] w-full divide-y divide-[#E8F2E2]">
              <thead class="bg-[#F7FBF3]">
                <tr class="text-left text-xs font-bold uppercase tracking-[0.12em] text-[#6B7D6C]">
                  <th class="px-4 py-3">Reporte</th>
                  <th class="px-4 py-3">Modo</th>
                  <th class="px-4 py-3">Salida</th>
                  <th class="px-4 py-3">Filas</th>
                  <th class="px-4 py-3">Estado</th>
                  <th class="px-4 py-3">Fecha</th>
                  <th class="px-4 py-3 text-right">Acción</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-[#EEF5EA] bg-white">
                @for (execution of executionPage.content; track execution.id) {
                  <tr>
                    <td class="px-4 py-3">
                      <p class="font-semibold text-[#1B5E20]">{{ execution.reportTitle }}</p>
                      <p class="mt-1 text-xs text-[#6B7D6C]">{{ execution.executionName }}</p>
                      <p class="mt-1 text-xs text-[#6B7D6C]">{{ execution.reportType }}</p>
                    </td>
                    <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ execution.mode }}</td>
                    <td class="px-4 py-3">
                      <div class="flex flex-wrap gap-2">
                        @for (output of execution.outputs; track output) {
                          <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-[11px] font-bold uppercase tracking-[0.12em] text-[#2E7D32]">{{ output }}</span>
                        }
                      </div>
                    </td>
                    <td class="px-4 py-3 text-sm font-semibold text-[#1B5E20]">{{ execution.rowCount }}</td>
                    <td class="px-4 py-3">
                      <span class="rounded-full px-3 py-1 text-[11px] font-bold uppercase tracking-[0.12em]"
                        [class.bg-green-100]="execution.status === 'COMPLETED'"
                        [class.text-green-700]="execution.status === 'COMPLETED'"
                        [class.bg-red-100]="execution.status === 'FAILED'"
                        [class.text-red-700]="execution.status === 'FAILED'">
                        {{ execution.status === 'COMPLETED' ? 'Completada' : execution.status === 'FAILED' ? 'Fallida' : execution.status }}
                      </span>
                    </td>
                    <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ formatDateTime(execution.createdAt) }}</td>
                    <td class="px-4 py-3 text-right">
                      <button
                        type="button"
                        (click)="openExecution(execution.id)"
                        class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                        Ver detalle
                      </button>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <div class="mt-4">
            <app-platform-pagination
              [currentPage]="executionPage.number"
              [totalPages]="executionPage.totalPages"
              [totalElements]="executionPage.totalElements"
              [isLoading]="reportsUseCase.executionsStatus() === 'loading'"
              (pageChange)="changeExecutionsPage($event)">
            </app-platform-pagination>
          </div>
        } @else {
          <div class="mt-5 rounded-2xl border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-6 text-sm text-[#5F6F5F]">
            No hay ejecuciones cargadas todavía.
          </div>
        }
      </section>

      @if (reportsUseCase.selectedExecution(); as execution) {
        <div class="app-modal-overlay">
          <div class="app-modal-panel app-modal-panel-sm">
            <div class="app-modal-header">
              <div>
                <h3 class="app-modal-title">{{ execution.reportTitle }}</h3>
                <p class="app-modal-subtitle">{{ execution.executionName }} · {{ execution.mode }} · {{ execution.status }}</p>
              </div>
              <button type="button" (click)="closeExecution()" class="cursor-pointer rounded-full border border-[#C8E6C9] p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
              </button>
            </div>

            <div class="app-modal-content-grid">
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Tipo</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ execution.reportType }}</p>
              </div>
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Filas</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ execution.rowCount }}</p>
              </div>
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Solicitado por</p>
                <p class="mt-1 break-words text-sm font-semibold text-[#1B5E20]">{{ execution.requestedBySubject }}</p>
              </div>
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Fecha</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ formatDateTime(execution.createdAt) }}</p>
              </div>
            </div>

            <div class="rounded-2xl border border-[#E8F2E2] bg-white p-4">
              <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Payload</p>
              <pre class="mt-2 max-h-72 overflow-auto rounded-xl bg-[#F7FBF3] p-3 text-xs leading-5 text-[#4F5D4F]">{{ prettyJson(execution.requestPayload) }}</pre>
            </div>

            @if (execution.exports.length > 0) {
              <div class="space-y-2">
                <h4 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">Exports</h4>
                @for (file of execution.exports; track file.fileName) {
                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4 text-sm text-[#4F5D4F]">
                    <div class="flex items-center justify-between gap-3">
                      <div>
                        <p class="font-semibold text-[#1B5E20]">{{ file.fileName }}</p>
                        <p class="text-xs text-[#6B7D6C]">{{ file.output }} · {{ file.contentType }}</p>
                      </div>
                      <div class="flex items-center gap-3">
                        <span class="text-xs font-semibold text-[#2E7D32]">{{ formatBytes(file.fileSizeBytes) }}</span>
                        <button
                          type="button"
                          (click)="downloadExport(file.id, file.fileName)"
                          class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-3 py-1.5 text-xs font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                          Descargar
                        </button>
                      </div>
                    </div>
                  </div>
                }
              </div>
            }

            <div class="app-modal-footer">
              <button type="button" (click)="closeExecution()" class="cursor-pointer rounded-full border border-[#C8E6C9] px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                Cerrar
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class ReportsHistoryPageComponent implements OnInit {
  readonly reportsUseCase = inject(ReportsDashboardUseCase);
  private readonly reportsService = inject(ReportsService);
  private readonly router = inject(Router);

  readonly loadingSkeleton = [1, 2, 3, 4];
  readonly reportTypeFilterValue = signal<string>('');

  ngOnInit(): void {
    this.reportsUseCase.clearSelectedExecution();
    this.reportsUseCase.resetExecutionFilters();
    void this.reportsUseCase.loadExecutions(0);
  }

  goToExplorer(): void {
    void this.router.navigate(['/dashboard/reports']);
  }

  reloadExecutions(): void {
    void this.reportsUseCase.loadExecutions(this.reportsUseCase.executionPage());
  }

  setExecutionMode(mode: ReportMode | null): void {
    this.reportsUseCase.setExecutionModeFilter(mode);
    void this.reportsUseCase.loadExecutions(0);
  }

  setExecutionReportType(value: string): void {
    const normalized = value.trim().toUpperCase();
    this.reportTypeFilterValue.set(normalized);
    this.reportsUseCase.setExecutionReportTypeFilter(normalized || null);
    void this.reportsUseCase.loadExecutions(0);
  }

  changeExecutionsPage(page: number): void {
    void this.reportsUseCase.loadExecutions(page);
  }

  async openExecution(executionId: string): Promise<void> {
    await this.reportsUseCase.selectExecution(executionId);
  }

  closeExecution(): void {
    this.reportsUseCase.clearSelectedExecution();
  }

  async downloadExport(exportId: string, fileName: string): Promise<void> {
    const blob = await firstValueFrom(this.reportsService.downloadExport(exportId));
    const url = window.URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName || 'report-export';
    anchor.click();
    window.URL.revokeObjectURL(url);
  }

  prettyJson(value: string): string {
    try {
      const parsed = JSON.parse(value);
      return JSON.stringify(parsed, null, 2);
    } catch {
      return value || 'Sin payload';
    }
  }

  formatDateTime(value: string | null | undefined): string {
    if (!value) {
      return 'Sin fecha';
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return 'Fecha inválida';
    }

    return new Intl.DateTimeFormat('es-BO', {
      timeZone: 'America/La_Paz',
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(date);
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
}
