import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { ReportsDashboardUseCase } from '../../features/reports/application/reports-dashboard.usecase';
import {
  ReportCatalogItemResponse,
  ReportMode
} from '../../entities/reports/model/reports.model';

@Component({
  selector: 'app-reports-explorer-page',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      @if (reportsUseCase.status() === 'loading') {
        <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          @for (item of loadingSkeleton; track item) {
            <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
              <div class="h-4 w-28 animate-pulse rounded bg-[#E8F5E9]"></div>
              <div class="mt-4 h-7 w-20 animate-pulse rounded bg-[#E8F5E9]"></div>
              <div class="mt-3 h-3 w-40 animate-pulse rounded bg-[#E8F5E9]"></div>
            </div>
          }
        </div>
      }

      @if (reportsUseCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6">
          <div class="flex items-start gap-4">
            <lucide-icon name="alert-circle" class="mt-0.5 h-5 w-5 text-red-700"></lucide-icon>
            <div>
              <h3 class="font-semibold text-red-700">No se pudieron cargar los reportes</h3>
              <p class="mt-1 text-sm text-red-700/80">{{ reportsUseCase.error() }}</p>
              <button (click)="reloadCatalogs()" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 hover:underline">Intentar nuevamente</button>
            </div>
          </div>
        </div>
      }

      @if (reportsUseCase.status() === 'success') {
        <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
          <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
            <div class="space-y-3">
              <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
                <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
                Reportes
              </div>
              <div>
                <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">Explorador de reportes</h1>
                <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                  Revisa el catálogo analítico y gerencial, inspecciona el esquema y abre la ejecución en una pantalla dedicada.
                </p>
              </div>
            </div>
            <div class="grid gap-3 sm:grid-cols-3">
              <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
                <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Analíticos</p>
                <p class="mt-1 text-2xl font-black text-[#1B5E20]">{{ reportsUseCase.analyticCatalog()?.reports?.length ?? 0 }}</p>
              </div>
              <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
                <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Gerenciales</p>
                <p class="mt-1 text-2xl font-black text-[#1B5E20]">{{ reportsUseCase.managerialCatalog()?.reports?.length ?? 0 }}</p>
              </div>
              <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
                <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Schema</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ reportsUseCase.selectedSchema()?.title || 'Selecciona un reporte' }}</p>
              </div>
            </div>
          </div>
        </section>

        <section class="grid gap-4 xl:grid-cols-[1.1fr_0.9fr]">
          <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
            <div class="flex flex-wrap items-center justify-between gap-3">
              <div>
                <h2 class="text-lg font-bold text-[#1B5E20]">Catálogo</h2>
                <p class="text-sm text-[#6B7D6C]">Analíticos y gerenciales disponibles</p>
              </div>
              <div class="flex items-center gap-2 rounded-full bg-[#F1F8E9] p-1 text-sm font-semibold text-[#2E7D32]">
                @for (tab of tabs; track tab.mode) {
                  <button
                    type="button"
                    (click)="setActiveMode(tab.mode)"
                    class="cursor-pointer rounded-full px-4 py-2 transition-colors"
                    [class.bg-white]="activeMode() === tab.mode"
                    [class.shadow-sm]="activeMode() === tab.mode">
                    {{ tab.label }}
                  </button>
                }
              </div>
            </div>

            <div class="mt-5 grid gap-4 md:grid-cols-2">
              @for (report of visibleReports(); track report.key) {
                <article class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-5 shadow-sm transition-shadow hover:shadow-md">
                  <div class="flex items-start justify-between gap-3">
                    <div class="min-w-0">
                      <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">{{ report.key }}</p>
                      <h3 class="mt-1 text-base font-bold text-[#1B5E20]">{{ report.label }}</h3>
                    </div>
                    <span class="rounded-full bg-white px-3 py-1 text-[11px] font-bold uppercase tracking-[0.12em] text-[#2E7D32]">
                      {{ activeMode() }}
                    </span>
                  </div>
                  <p class="mt-3 text-sm leading-6 text-[#4F5D4F]">{{ report.description }}</p>

                  <div class="mt-4 flex flex-wrap gap-2">
                    <button
                      type="button"
                      (click)="openSchema(report)"
                      class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                      Ver esquema
                    </button>
                    <button
                      type="button"
                      (click)="openRunPage(report)"
                      class="cursor-pointer rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
                      Ejecutar reporte
                    </button>
                  </div>
                </article>
              }
            </div>
          </div>

          <aside class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
            @if (reportsUseCase.schemaStatus() === 'loading') {
              <div class="space-y-3">
                <div class="h-5 w-40 animate-pulse rounded bg-[#E8F5E9]"></div>
                <div class="h-4 w-64 animate-pulse rounded bg-[#E8F5E9]"></div>
                <div class="h-4 w-full animate-pulse rounded bg-[#E8F5E9]"></div>
                <div class="h-4 w-5/6 animate-pulse rounded bg-[#E8F5E9]"></div>
              </div>
            } @else if (reportsUseCase.schemaStatus() === 'error') {
              <div class="rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
                {{ reportsUseCase.schemaError() }}
              </div>
            } @else if (reportsUseCase.selectedSchema(); as schema) {
              <div class="space-y-5">
                <div>
                  <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">
                    {{ schema.mode }} · {{ schema.reportType }}
                  </p>
                  <h2 class="mt-1 text-xl font-black text-[#1B5E20]">{{ schema.title }}</h2>
                  <p class="mt-2 text-sm leading-6 text-[#4F5D4F]">{{ schema.description }}</p>
                </div>

                <div class="grid gap-3 sm:grid-cols-2">
                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Outputs</p>
                    <div class="mt-2 flex flex-wrap gap-2">
                      @for (output of schema.outputs; track output) {
                        <span class="rounded-full bg-white px-3 py-1 text-xs font-semibold text-[#2E7D32]">{{ output }}</span>
                      }
                    </div>
                  </div>
                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Límites</p>
                    <p class="mt-2 text-sm text-[#4F5D4F]">Filas máx. {{ schema.limits.maxRows }} · Columnas máx. {{ schema.limits.maxColumns }}</p>
                    <p class="text-sm text-[#4F5D4F]">GroupBy máx. {{ schema.limits.maxGroupBy }} · Métricas máx. {{ schema.limits.maxMetrics }}</p>
                  </div>
                </div>

                <div class="space-y-3">
                  <h3 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">Filtros</h3>
                  <div class="space-y-2">
                    @for (field of schema.filters.slice(0, 6); track field.key) {
                      <div class="rounded-2xl border border-[#E8F2E2] bg-white px-4 py-3">
                        <p class="text-sm font-semibold text-[#1B5E20]">{{ field.label }}</p>
                        <p class="mt-1 text-xs text-[#6B7D6C]">{{ field.key }} · {{ field.type }} · {{ field.operators.join(', ') }}</p>
                      </div>
                    }
                  </div>
                </div>

                <div class="grid gap-3 sm:grid-cols-2">
                  <div class="space-y-2">
                    <h3 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">Columnas</h3>
                    @for (field of schema.columns.slice(0, 5); track field.key) {
                      <div class="rounded-xl border border-[#E8F2E2] px-3 py-2 text-sm text-[#4F5D4F]">{{ field.label }}</div>
                    }
                  </div>
                  <div class="space-y-2">
                    <h3 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">GroupBy / Métricas</h3>
                    @for (field of schema.groupBy.slice(0, 4); track field.key) {
                      <div class="rounded-xl border border-[#E8F2E2] px-3 py-2 text-sm text-[#4F5D4F]">{{ field.label }}</div>
                    }
                    @for (metric of schema.metrics.slice(0, 4); track metric.key) {
                      <div class="rounded-xl border border-[#E8F2E2] px-3 py-2 text-sm text-[#4F5D4F]">{{ metric.label }}</div>
                    }
                  </div>
                </div>

                <div class="flex flex-wrap gap-2">
                  <button
                    type="button"
                    (click)="openRunPage(reportsUseCase.selectedReport())"
                    class="cursor-pointer rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
                    Ejecutar reporte
                  </button>
                  <button
                    type="button"
                    (click)="closeSchema()"
                    class="cursor-pointer rounded-full border border-[#C8E6C9] px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                    Cerrar detalle
                  </button>
                </div>
              </div>
            } @else {
              <div class="rounded-2xl border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-6 text-sm text-[#5F6F5F]">
                Selecciona un reporte para ver su schema, filtros disponibles, salidas y límites.
              </div>
            }
          </aside>
        </section>
      }
    </div>
  `
})
export class ReportsExplorerPageComponent implements OnInit {
  readonly reportsUseCase = inject(ReportsDashboardUseCase);
  private readonly router = inject(Router);

  readonly loadingSkeleton = [1, 2, 3, 4];
  readonly activeMode = signal<ReportMode>('ANALYTIC');

  readonly tabs = [
    { mode: 'ANALYTIC' as ReportMode, label: 'Analíticos' },
    { mode: 'MANAGERIAL' as ReportMode, label: 'Gerenciales' }
  ];

  readonly visibleReports = computed(() => {
    const mode = this.activeMode();
    const catalog = mode === 'ANALYTIC'
      ? this.reportsUseCase.analyticCatalog()
      : this.reportsUseCase.managerialCatalog();

    return catalog?.reports ?? [];
  });

  ngOnInit(): void {
    this.reportsUseCase.clearSelectedReport();
    this.reportsUseCase.clearSelectedExecution();
    this.reportsUseCase.loadCatalogs();
  }

  reloadCatalogs(): void {
    this.reportsUseCase.loadCatalogs();
  }

  setActiveMode(mode: ReportMode): void {
    this.activeMode.set(mode);
    this.reportsUseCase.clearSelectedReport();
    this.reportsUseCase.clearSelectedExecution();
  }

  async openSchema(report: ReportCatalogItemResponse): Promise<void> {
    await this.reportsUseCase.selectReport(this.activeMode(), report);
  }

  closeSchema(): void {
    this.reportsUseCase.clearSelectedReport();
  }

  openRunPage(report: ReportCatalogItemResponse | null): void {
    const selected = report ?? this.reportsUseCase.selectedReport();
    if (!selected) {
      return;
    }

    void this.router.navigate([
      '/dashboard/reports/run',
      this.activeMode().toLowerCase(),
      selected.key
    ]);
  }
}
