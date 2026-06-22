import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { AccountingPeriodResponse, CreateAccountingPeriodRequest } from '../../entities/accounting';
import { PeriodListUseCase } from '../../features/accounting';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { HasPermissionPipe } from '../../shared/api';

@Component({
  selector: 'app-accounting-periods-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PlatformPaginationComponent, LucideAngularModule, HasPermissionPipe],
  providers: [DatePipe],
  template: `
    <div class="space-y-6 relative">
      <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="space-y-3">
            <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
              <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
              Contabilidad
            </div>
            <div>
              <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
                Períodos contables
              </h1>
              <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                Gestiona los períodos, crea nuevos ciclos y cierra los abiertos sin perder el contexto operativo.
              </p>
            </div>
          </div>

          <div class="flex flex-wrap gap-3">
            <button
              type="button"
              (click)="loadPeriods()"
              class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              <lucide-icon name="refresh-ccw" [size]="16"></lucide-icon>
              Recargar
            </button>
            <button
              *ngIf="'accounting.periods.create' | hasPermission"
              type="button"
              (click)="openCreateForm()"
              class="inline-flex cursor-pointer items-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-[#256428]">
              <lucide-icon name="plus" [size]="16"></lucide-icon>
              Nuevo período
            </button>
          </div>
        </div>
      </section>

      <section class="grid gap-4 md:grid-cols-3">
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">En página</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ currentPageCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Períodos visibles</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Abiertos</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ openCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Disponibles para cierre</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Cerrados</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ closedCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Ya contabilizados</p>
        </div>
      </section>

      <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-lg font-bold text-[#1B5E20]">Filtro rápido</h2>
            <p class="text-sm text-[#6B7D6C]">Busca por código, tipo, estado o descripción.</p>
          </div>
          <label class="relative w-full lg:max-w-md">
            <lucide-icon name="search" class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#6B7D6C]" [size]="16"></lucide-icon>
            <input
              type="text"
              [value]="searchTerm()"
              (input)="onSearch(($any($event.target).value || '').toString())"
              placeholder="Buscar período..."
              class="w-full rounded-full border border-[#C8E6C9] bg-[#FAFCF8] py-3 pl-10 pr-4 text-sm text-[#1B5E20] outline-none transition focus:border-[#2E7D32] focus:bg-white">
          </label>
        </div>
      </section>

      @if (useCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6 flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-red-700 mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-red-700">Error al cargar períodos</h3>
            <p class="mt-1 text-sm text-red-700/80">{{ useCase.error() }}</p>
            <button (click)="loadPeriods()" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center gap-4 p-12 text-[#6B7D6C]">
          <svg class="h-8 w-8 animate-spin text-[#2E7D32]" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando períodos...</p>
        </div>
      }

      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="border-b border-[#E8F2E2] bg-[#F7FBF3] text-xs uppercase tracking-[0.12em] text-[#6B7D6C]">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Código</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo</th>
                  <th scope="col" class="px-6 py-4 font-medium">Fechas</th>
                  <th scope="col" class="px-6 py-4 font-medium">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium">Descripción</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-[#EEF5EA] bg-white">
                @for (period of filteredPeriods(); track period.id) {
                  <tr class="transition-colors hover:bg-[#F7FBF3]">
                    <td class="px-6 py-4">
                      <div class="font-semibold text-[#1B5E20]">{{ period.periodCode }}</div>
                    </td>
                    <td class="px-6 py-4 text-[#4F5D4F]">{{ periodTypeLabel(period.periodType) }}</td>
                    <td class="px-6 py-4 text-[#4F5D4F]">
                      <div class="text-sm">{{ period.startDate | date:'mediumDate' }}</div>
                      <div class="text-xs text-[#6B7D6C]">a {{ period.endDate | date:'mediumDate' }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <span class="rounded-full px-2.5 py-1 text-xs font-semibold" [ngClass]="periodStatusClass(period.status)">
                        {{ periodStatusLabel(period.status) }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-[#4F5D4F]">
                      <div class="max-w-[24rem] break-words">{{ period.description || 'Sin descripción' }}</div>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex justify-end gap-2">
                        @if (period.status === 'OPEN' && ('accounting.periods.close' | hasPermission)) {
                          <button
                            type="button"
                            (click)="openCloseModal(period)"
                            class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] px-3 py-1.5 text-xs font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                            <lucide-icon name="lock" [size]="14"></lucide-icon>
                            Cerrar
                          </button>
                        }
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-[#6B7D6C]">
                      No hay períodos contables registrados.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <div class="mt-4">
            <app-platform-pagination
              [currentPage]="currentPage()"
              [totalPages]="totalPages()"
              [totalElements]="totalElements()"
              [isLoading]="useCase.status() === 'loading'"
              (pageChange)="onPageChange($event)">
            </app-platform-pagination>
          </div>
        </div>
      }

      @if (createOpen()) {
        <div class="app-modal-overlay">
          <div class="app-modal-panel app-modal-panel-sm">
            <div class="app-modal-header">
              <div>
                <h2 class="app-modal-title">Nuevo período</h2>
                <p class="app-modal-subtitle">Crea un ciclo contable con fechas válidas y tipo explícito.</p>
              </div>
              <button
                type="button"
                class="inline-flex cursor-pointer items-center justify-center rounded-full border border-[#C8E6C9] bg-white p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]"
                (click)="closeCreateModal()"
                [disabled]="submitting()">
                <lucide-icon name="x" [size]="18"></lucide-icon>
              </button>
            </div>

            <form class="space-y-4" [formGroup]="createForm" (ngSubmit)="submitCreate()">
              <div class="app-modal-content-grid">
                <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                  <label class="block text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Código</label>
                  <input
                    type="text"
                    formControlName="periodCode"
                    class="mt-2 w-full rounded-2xl border border-[#C8E6C9] bg-white px-4 py-3 text-sm text-[#1B5E20] outline-none transition focus:border-[#2E7D32]">
                </div>
                <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                  <label class="block text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Tipo</label>
                  <select
                    formControlName="periodType"
                    class="mt-2 w-full rounded-2xl border border-[#C8E6C9] bg-white px-4 py-3 text-sm text-[#1B5E20] outline-none transition focus:border-[#2E7D32]">
                    <option value="">Seleccione...</option>
                    <option value="DAILY">Diario</option>
                    <option value="MONTHLY">Mensual</option>
                    <option value="ANNUAL">Anual</option>
                    <option value="CUSTOM">Personalizado</option>
                  </select>
                </div>
                <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                  <label class="block text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Inicio</label>
                  <input
                    type="date"
                    formControlName="startDate"
                    class="mt-2 w-full rounded-2xl border border-[#C8E6C9] bg-white px-4 py-3 text-sm text-[#1B5E20] outline-none transition focus:border-[#2E7D32]">
                </div>
                <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                  <label class="block text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Fin</label>
                  <input
                    type="date"
                    formControlName="endDate"
                    class="mt-2 w-full rounded-2xl border border-[#C8E6C9] bg-white px-4 py-3 text-sm text-[#1B5E20] outline-none transition focus:border-[#2E7D32]">
                </div>
              </div>

              <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                <label class="block text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Descripción</label>
                <textarea
                  formControlName="description"
                  rows="3"
                  class="mt-2 w-full rounded-2xl border border-[#C8E6C9] bg-white px-4 py-3 text-sm text-[#1B5E20] outline-none transition focus:border-[#2E7D32]"></textarea>
              </div>

              @if (createError()) {
                <div class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
                  {{ createError() }}
                </div>
              }

              <div class="app-modal-footer">
                <button
                  type="button"
                  class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]"
                  (click)="closeCreateModal()"
                  [disabled]="submitting()">
                  Cancelar
                </button>
                <button
                  type="submit"
                  class="inline-flex cursor-pointer items-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-[#256428]"
                  [disabled]="createForm.invalid || submitting()">
                  @if (submitting()) {
                    <lucide-icon name="loader-2" [size]="16" class="animate-spin"></lucide-icon>
                    Guardando...
                  } @else {
                    <lucide-icon name="save" [size]="16"></lucide-icon>
                    Crear período
                  }
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      @if (closeTarget()) {
        <div class="app-modal-overlay">
          <div class="app-modal-panel app-modal-panel-sm">
            <div class="app-modal-header">
              <div>
                <h2 class="app-modal-title">Cerrar período</h2>
                <p class="app-modal-subtitle">Confirma el cierre del período seleccionado.</p>
              </div>
              <button
                type="button"
                class="inline-flex cursor-pointer items-center justify-center rounded-full border border-[#C8E6C9] bg-white p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]"
                (click)="closeCloseModal()"
                [disabled]="submitting()">
                <lucide-icon name="x" [size]="18"></lucide-icon>
              </button>
            </div>

            <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
              <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Período</p>
              <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ closeTarget()?.periodCode }}</p>
              <p class="mt-1 text-sm text-[#4F5D4F]">{{ closeTarget()?.periodType }}</p>
            </div>

            <form class="space-y-4" [formGroup]="closeForm" (ngSubmit)="submitClose()">
              <div class="rounded-2xl border border-[#E8F2E2] bg-white p-4">
                <label class="block text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Motivo opcional</label>
                <textarea
                  formControlName="reason"
                  rows="3"
                  placeholder="Motivo de cierre..."
                  class="mt-2 w-full rounded-2xl border border-[#C8E6C9] bg-[#FAFCF8] px-4 py-3 text-sm text-[#1B5E20] outline-none transition focus:border-[#2E7D32]"></textarea>
              </div>

              @if (closeError()) {
                <div class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
                  {{ closeError() }}
                </div>
              }

              <div class="app-modal-footer">
                <button
                  type="button"
                  class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]"
                  (click)="closeCloseModal()"
                  [disabled]="submitting()">
                  Cancelar
                </button>
                <button
                  type="submit"
                  class="inline-flex cursor-pointer items-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-[#256428]"
                  [disabled]="submitting()">
                  @if (submitting()) {
                    <lucide-icon name="loader-2" [size]="16" class="animate-spin"></lucide-icon>
                    Cerrando...
                  } @else {
                    <lucide-icon name="lock" [size]="16"></lucide-icon>
                    Cerrar período
                  }
                </button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `
})
export class AccountingPeriodsPageComponent implements OnInit {
  public readonly useCase = inject(PeriodListUseCase);
  private readonly fb = inject(FormBuilder);

  readonly pageSize = 20;
  readonly currentPage = computed(() => this.useCase.page()?.number ?? 0);
  readonly totalPages = computed(() => this.useCase.page()?.totalPages ?? 0);
  readonly totalElements = computed(() => this.useCase.page()?.totalElements ?? 0);
  readonly currentPageCount = computed(() => this.filteredPeriods().length);
  readonly openCount = computed(() => this.filteredPeriods().filter((period) => period.status === 'OPEN').length);
  readonly closedCount = computed(() => this.filteredPeriods().filter((period) => period.status !== 'OPEN').length);
  readonly searchTerm = signal('');
  readonly createOpen = signal(false);
  readonly closeTarget = signal<AccountingPeriodResponse | null>(null);
  readonly submitting = signal(false);
  readonly createError = signal('');
  readonly closeError = signal('');

  readonly filteredPeriods = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const periods = this.useCase.data();
    if (!term) {
      return periods;
    }

    return periods.filter((period) => {
      return [
        period.periodCode,
        period.periodType,
        period.status,
        period.description
      ]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(term));
    });
  });

  readonly createForm = this.fb.nonNullable.group({
    periodCode: ['', [Validators.required, Validators.maxLength(80)]],
    periodType: ['', [Validators.required]],
    startDate: [this.todayValue(), [Validators.required]],
    endDate: [this.todayValue(), [Validators.required]],
    description: ['']
  });

  readonly closeForm = this.fb.nonNullable.group({
    reason: ['']
  });

  ngOnInit(): void {
    this.loadPeriods();
  }

  loadPeriods(): void {
    this.useCase.loadPeriods(this.currentPage(), this.pageSize);
  }

  onPageChange(page: number): void {
    this.useCase.loadPeriods(page, this.pageSize);
  }

  onSearch(value: string): void {
    this.searchTerm.set(value);
  }

  openCreateForm(): void {
    this.createError.set('');
    this.createForm.reset({
      periodCode: '',
      periodType: '',
      startDate: this.todayValue(),
      endDate: this.todayValue(),
      description: ''
    });
    this.createOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.submitting()) {
      return;
    }
    this.createOpen.set(false);
    this.createError.set('');
  }

  openCloseModal(period: AccountingPeriodResponse): void {
    this.closeError.set('');
    this.closeForm.reset({ reason: '' });
    this.closeTarget.set(period);
  }

  closeCloseModal(): void {
    if (this.submitting()) {
      return;
    }
    this.closeTarget.set(null);
    this.closeError.set('');
  }

  async submitCreate(): Promise<void> {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }

    const value = this.createForm.getRawValue();
    if (value.startDate > value.endDate) {
      this.createError.set('La fecha de inicio no puede ser mayor que la fecha de fin.');
      return;
    }

    this.submitting.set(true);
    this.createError.set('');

    try {
      const payload: CreateAccountingPeriodRequest = {
        periodCode: value.periodCode.trim().toUpperCase(),
        periodType: value.periodType,
        startDate: value.startDate,
        endDate: value.endDate,
        description: value.description?.trim() || undefined
      };

      const created = await this.useCase.createPeriod(payload);
      if (created) {
        this.createOpen.set(false);
      } else {
        this.createError.set(this.useCase.error() || 'No se pudo crear el período contable');
      }
    } finally {
      this.submitting.set(false);
    }
  }

  async submitClose(): Promise<void> {
    const target = this.closeTarget();
    if (!target) {
      return;
    }

    this.submitting.set(true);
    this.closeError.set('');

    try {
      const closed = await this.useCase.closePeriod(target.id, {
        reason: this.closeForm.getRawValue().reason?.trim() || undefined
      });

      if (closed) {
        this.closeTarget.set(null);
      } else {
        this.closeError.set(this.useCase.error() || 'No se pudo cerrar el período contable');
      }
    } finally {
      this.submitting.set(false);
    }
  }

  periodStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      OPEN: 'Abierto',
      CLOSED: 'Cerrado',
      ARCHIVED: 'Archivado'
    };

    return labels[status] ?? status;
  }

  periodStatusClass(status: string): string {
    if (status === 'OPEN') {
      return 'bg-green-500/10 text-green-700';
    }
    if (status === 'CLOSED') {
      return 'bg-slate-500/10 text-slate-600';
    }
    if (status === 'ARCHIVED') {
      return 'bg-amber-500/10 text-amber-700';
    }

    return 'bg-slate-500/10 text-slate-600';
  }

  periodTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      DAILY: 'Diario',
      MONTHLY: 'Mensual',
      ANNUAL: 'Anual',
      CUSTOM: 'Personalizado'
    };

    return labels[type] ?? type;
  }

  private todayValue(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
