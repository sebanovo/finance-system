import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { ReportsDashboardUseCase } from '../../features/reports/application/reports-dashboard.usecase';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import {
  ReportCatalogItemResponse,
  ReportFieldResponse,
  ReportMode,
  ReportOutput,
  ReportSchemaResponse
} from '../../entities/reports/model/reports.model';

@Component({
  selector: 'app-reports-page',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, PlatformPaginationComponent],
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
                  Revisa el catálogo analítico y gerencial, inspecciona el schema y prepara la ejecución con el contrato real del backend.
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

                <button
                  type="button"
                  (click)="reportsUseCase.clearSelectedReport(); resetComposer()"
                  class="cursor-pointer rounded-full border border-[#C8E6C9] px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                  Cerrar detalle
                </button>
              </div>
            } @else {
              <div class="rounded-2xl border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-6 text-sm text-[#5F6F5F]">
                Selecciona un reporte para ver su schema, filtros disponibles, salidas y límites.
              </div>
            }
          </aside>
        </section>

        @if (reportsUseCase.selectedSchema(); as schema) {
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
                  {{ reportsUseCase.runStatus() === 'loading' ? 'Ejecutando...' : 'Ejecutar' }}
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
        }

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
                      <p class="font-semibold text-[#1B5E20]">{{ file.fileName }}</p>
                      <p class="mt-1 text-xs text-[#6B7D6C]">{{ file.output }} · {{ file.contentType }}</p>
                      <p class="mt-1 text-xs text-[#6B7D6C]">{{ formatBytes(file.fileSizeBytes) }}</p>
                    </div>
                  }
                </div>
              </div>
            }
          </section>
        }

        <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
          <div class="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <h2 class="text-lg font-bold text-[#1B5E20]">Historial de ejecuciones</h2>
              <p class="text-sm text-[#6B7D6C]">Consulta las ejecuciones recientes de reportes</p>
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
                        <span class="text-xs font-semibold text-[#2E7D32]">{{ formatBytes(file.fileSizeBytes) }}</span>
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
      }
    </div>
  `
})
export class ReportsPageComponent implements OnInit {
  readonly reportsUseCase = inject(ReportsDashboardUseCase);
  readonly loadingSkeleton = [1, 2, 3, 4];
  readonly activeMode = signal<ReportMode>('ANALYTIC');
  readonly reportTypeFilterValue = signal<string>('');
  readonly selectedColumns = signal<string[]>([]);
  readonly selectedGroupBy = signal<string[]>([]);
  readonly selectedMetrics = signal<string[]>([]);
  readonly selectedVisualizations = signal<string[]>([]);
  readonly selectedOutputs = signal<ReportOutput[]>(['SCREEN']);
  readonly filters = signal<Array<{ field: string; operator: string; value: string; to: string }>>([]);
  readonly paginationLimit = signal<number>(20);
  readonly paginationOffset = signal<number>(0);

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
    this.reportsUseCase.loadCatalogs();
    this.reportsUseCase.loadExecutions(0);
  }

  reloadCatalogs(): void {
    this.reportsUseCase.loadCatalogs();
  }

  setActiveMode(mode: ReportMode): void {
    this.activeMode.set(mode);
    this.reportsUseCase.clearSelectedReport();
    this.resetComposer();
  }

  async openSchema(report: ReportCatalogItemResponse): Promise<void> {
    await this.reportsUseCase.selectReport(this.activeMode(), report);
    this.resetComposer();
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
    this.selectedVisualizations.set(defaults?.visualizations?.length ? defaults.visualizations.map(String) : currentSchema.visualizations.slice());
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
