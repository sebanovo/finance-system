import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FxRatesListUseCase, FxRateFormComponent } from '../../features/fx-management';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { FxExchangeRateResponse } from '../../entities/fx';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-fx-rates-page',
  standalone: true,
  imports: [CommonModule, FxRateFormComponent, HasPermissionPipe, LucideAngularModule, PlatformPaginationComponent],
  providers: [DatePipe],
  template: `
    <div class="space-y-6">
      <section class="rounded-[2rem] border border-[#CDE5C8] bg-gradient-to-br from-[#F4FAF1] via-[#EDF7E9] to-[#F8FCF6] px-6 py-7 shadow-[0_18px_60px_rgba(27,94,32,0.08)] sm:px-8">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="max-w-2xl space-y-3">
            <p class="text-sm font-semibold uppercase tracking-[0.3em] text-[#4E7B54]">Divisas</p>
            <div class="space-y-2">
              <h2 class="text-3xl font-black tracking-tight text-[#16441A] sm:text-4xl">Tipos de Cambio</h2>
              <p class="max-w-2xl text-sm leading-6 text-[#58705C] sm:text-base">
                Mantén actualizadas las tasas de cambio que usa el sistema para conversiones y cálculos financieros.
              </p>
            </div>
          </div>

          <button
            *ngIf="'fx.rates.create' | hasPermission"
            type="button"
            (click)="openCreateForm()"
            class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2.5 text-sm font-semibold text-[#2E7D32] shadow-sm transition-colors hover:bg-[#F1F8E9]">
            <lucide-icon name="plus" [size]="16"></lucide-icon>
            Nueva Tasa
          </button>
        </div>
      </section>

      <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <article class="rounded-2xl border border-[#DDEED8] bg-white p-5 shadow-sm">
          <p class="text-xs font-semibold uppercase tracking-[0.25em] text-[#72916F]">Registros</p>
          <p class="mt-3 text-3xl font-black text-[#16441A]">{{ totalRates() }}</p>
          <p class="mt-1 text-sm text-[#5F765B]">Tipos de cambio en la página actual.</p>
        </article>
        <article class="rounded-2xl border border-[#DDEED8] bg-white p-5 shadow-sm">
          <p class="text-xs font-semibold uppercase tracking-[0.25em] text-[#72916F]">Activas</p>
          <p class="mt-3 text-3xl font-black text-[#16441A]">{{ activeRates() }}</p>
          <p class="mt-1 text-sm text-[#5F765B]">Tasas visibles y disponibles para uso.</p>
        </article>
        <article class="rounded-2xl border border-[#DDEED8] bg-white p-5 shadow-sm">
          <p class="text-xs font-semibold uppercase tracking-[0.25em] text-[#72916F]">Inactivas</p>
          <p class="mt-3 text-3xl font-black text-[#16441A]">{{ inactiveRates() }}</p>
          <p class="mt-1 text-sm text-[#5F765B]">Tasas deshabilitadas temporalmente.</p>
        </article>
        <article class="rounded-2xl border border-[#DDEED8] bg-white p-5 shadow-sm">
          <p class="text-xs font-semibold uppercase tracking-[0.25em] text-[#72916F]">Pares</p>
          <p class="mt-3 text-3xl font-black text-[#16441A]">{{ uniquePairs() }}</p>
          <p class="mt-1 text-sm text-[#5F765B]">Combinaciones distintas origen-destino.</p>
        </article>
      </section>

      @if (useCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-5 text-red-900 shadow-sm">
          <div class="flex items-start gap-3">
            <div class="mt-0.5 rounded-full bg-red-100 p-2 text-red-600">
              <lucide-icon name="alert-triangle" [size]="18"></lucide-icon>
            </div>
            <div>
              <h3 class="font-semibold">Error al cargar divisas</h3>
              <p class="mt-1 text-sm text-red-700">{{ useCase.error() }}</p>
              <button type="button" (click)="loadRates(currentPage)" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 underline underline-offset-4">
                Intentar nuevamente
              </button>
            </div>
          </div>
        </div>
      }

      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center gap-3 rounded-3xl border border-[#DDEED8] bg-white px-6 py-16 text-center shadow-sm">
          <svg class="h-9 w-9 animate-spin text-[#2E7D32]" viewBox="0 0 24 24">
            <circle class="opacity-20" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-80" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="text-sm font-medium text-[#58705C]">Cargando tipos de cambio...</p>
        </div>
      }

      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <section class="rounded-[2rem] border border-[#DDEED8] bg-white shadow-sm">
          <div class="flex flex-col gap-3 border-b border-[#E5EEDD] px-5 py-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h3 class="text-lg font-bold text-[#16441A]">Listado de tipos de cambio</h3>
              <p class="text-sm text-[#5F765B]">Página {{ currentPage + 1 }} de {{ totalPagesLabel() }}.</p>
            </div>
            <p class="text-sm text-[#5F765B]">
              {{ totalElementsLabel() }} registro(s) en total
            </p>
          </div>

          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-[#E6EFE1] text-left text-sm">
              <thead class="bg-[#F7FBF5] text-xs uppercase tracking-wide text-[#6C8468]">
                <tr>
                  <th class="px-5 py-4 font-semibold">Par</th>
                  <th class="px-5 py-4 font-semibold">Tasa</th>
                  <th class="px-5 py-4 font-semibold">Estado</th>
                  <th class="px-5 py-4 font-semibold">Actualizado</th>
                  <th class="px-5 py-4 text-right font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-[#EEF4EA]">
                @for (rate of useCase.data(); track rate.id) {
                  <tr class="transition-colors hover:bg-[#FAFDF8]">
                    <td class="px-5 py-4">
                      <div class="space-y-1">
                        <p class="font-semibold text-[#18471C]">{{ currencyLabel(rate.sourceCurrency) }} → {{ currencyLabel(rate.targetCurrency) }}</p>
                        <p class="text-xs text-[#6B8168]">{{ rate.description || 'Sin descripción' }}</p>
                      </div>
                    </td>
                    <td class="px-5 py-4 font-semibold text-[#18471C]">{{ rate.rate | number:'1.2-6' }}</td>
                    <td class="px-5 py-4">
                      <span class="inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold"
                        [ngClass]="rate.active ? 'bg-[#E8F5E9] text-[#2E7D32]' : 'bg-[#F1F4F1] text-[#6B7D6D]'">
                        {{ rate.active ? 'Activa' : 'Inactiva' }}
                      </span>
                    </td>
                    <td class="px-5 py-4 text-[#58705C]">{{ rate.updatedAt | date:'mediumDate' }}</td>
                    <td class="px-5 py-4">
                      <div class="flex justify-end gap-3">
                        <button
                          *ngIf="'fx.rates.update' | hasPermission"
                          type="button"
                          (click)="openEditForm(rate)"
                          class="cursor-pointer text-sm font-semibold text-[#2E7D32] hover:underline">
                          Editar
                        </button>
                        <button
                          *ngIf="'fx.rates.delete' | hasPermission"
                          type="button"
                          (click)="deleteRate(rate.id)"
                          class="cursor-pointer text-sm font-semibold text-red-600 hover:underline">
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="5" class="px-5 py-16 text-center text-[#6B8168]">
                      No hay tipos de cambio registrados.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <div class="border-t border-[#E5EEDD] px-5 py-4">
            <app-platform-pagination
              [currentPage]="currentPage"
              [totalPages]="useCase.page()?.totalPages ?? 0"
              [totalElements]="useCase.page()?.totalElements ?? 0"
              [isLoading]="useCase.status() === 'loading'"
              (pageChange)="loadRates($event)"
            ></app-platform-pagination>
          </div>
        </section>
      }

      @if (isFormOpen) {
        <app-fx-rate-form
          [rate]="selectedRate"
          [loading]="isSubmitting"
          (formSubmit)="onFormSubmit($event)"
          (cancel)="closeForm()"
        ></app-fx-rate-form>
      }
    </div>
  `
})
export class FxRatesPageComponent implements OnInit {
  public readonly useCase = inject(FxRatesListUseCase);
  private readonly toastService = inject(ToastService);

  isFormOpen = false;
  selectedRate: FxExchangeRateResponse | null = null;
  isSubmitting = false;
  currentPage = 0;

  ngOnInit(): void {
    this.loadRates(0);
  }

  loadRates(page = 0): void {
    this.currentPage = page;
    void this.useCase.loadRates(page, 20);
  }

  totalRates(): number {
    return this.useCase.page()?.totalElements ?? 0;
  }

  activeRates(): number {
    return this.useCase.data().filter(rate => rate.active).length;
  }

  inactiveRates(): number {
    return this.useCase.data().filter(rate => !rate.active).length;
  }

  uniquePairs(): number {
    const pairs = new Set(this.useCase.data().map(rate => `${rate.sourceCurrency}-${rate.targetCurrency}`));
    return pairs.size;
  }

  totalPagesLabel(): number {
    return this.useCase.page()?.totalPages ?? 0;
  }

  totalElementsLabel(): number {
    return this.useCase.page()?.totalElements ?? 0;
  }

  currencyLabel(code: string): string {
    switch (code) {
      case 'BOB': return 'BOB';
      case 'USD': return 'USD';
      case 'EUR': return 'EUR';
      case 'USDT': return 'USDT';
      default: return code;
    }
  }

  openCreateForm(): void {
    this.selectedRate = null;
    this.isFormOpen = true;
  }

  openEditForm(rate: FxExchangeRateResponse): void {
    this.selectedRate = rate;
    this.isFormOpen = true;
  }

  closeForm(): void {
    this.isFormOpen = false;
    this.selectedRate = null;
  }

  async onFormSubmit(event: { type: 'create' | 'update'; data: any }): Promise<void> {
    this.isSubmitting = true;
    try {
      if (event.type === 'create') {
        await this.useCase.createRate(event.data);
        this.toastService.success('Tasa de cambio creada exitosamente');
      } else {
        await this.useCase.updateRate(this.selectedRate!.id, event.data);
        this.toastService.success('Tasa de cambio actualizada exitosamente');
      }
      this.closeForm();
    } catch (error: any) {
      let msg = error.error?.message || error.message || 'Error al procesar la solicitud';

      if (msg.includes('already exists for pair')) {
        msg = 'Ya existe una tasa de cambio activa para este par de divisas.';
      } else if (msg.includes('already exists')) {
        msg = 'Esta tasa de cambio ya está registrada en el sistema.';
      } else if (msg.includes('Self exchange rate must be 1')) {
        msg = 'La tasa de cambio para una misma moneda debe ser exactamente 1.';
      }

      this.toastService.error(msg, 'No se pudo guardar');
    } finally {
      this.isSubmitting = false;
    }
  }

  async deleteRate(id: string): Promise<void> {
    if (!confirm('¿Estás seguro de eliminar este tipo de cambio?')) {
      return;
    }

    const success = await this.useCase.deleteRate(id);
    if (success) {
      this.toastService.success('Tasa de cambio eliminada');
    } else {
      this.toastService.error('No se pudo eliminar la tasa de cambio');
    }
  }
}
