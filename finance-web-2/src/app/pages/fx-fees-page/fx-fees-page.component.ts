import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FxFeeFormComponent, FxFeesListUseCase } from '../../features/fx-management';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { OperationFeeResponse } from '../../entities/fx';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-fx-fees-page',
  standalone: true,
  imports: [CommonModule, FxFeeFormComponent, HasPermissionPipe, LucideAngularModule, PlatformPaginationComponent],
  providers: [DatePipe],
  template: `
    <div class="space-y-6">
      <section class="rounded-[2rem] border border-[#CDE5C8] bg-gradient-to-br from-[#F4FAF1] via-[#EDF7E9] to-[#F8FCF6] px-6 py-7 shadow-[0_18px_60px_rgba(27,94,32,0.08)] sm:px-8">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="max-w-2xl space-y-3">
            <p class="text-sm font-semibold uppercase tracking-[0.3em] text-[#4E7B54]">Tarifas</p>
            <div class="space-y-2">
              <h2 class="text-3xl font-black tracking-tight text-[#16441A] sm:text-4xl">Comisiones Operativas</h2>
              <p class="max-w-2xl text-sm leading-6 text-[#58705C] sm:text-base">
                Define las tarifas que se aplican a las operaciones del sistema con un catálogo claro y consistente.
              </p>
            </div>
          </div>

          <button
            *ngIf="'fx.fees.create' | hasPermission"
            type="button"
            (click)="openCreateForm()"
            class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2.5 text-sm font-semibold text-[#2E7D32] shadow-sm transition-colors hover:bg-[#F1F8E9]">
            <lucide-icon name="plus" [size]="16"></lucide-icon>
            Nueva Comisión
          </button>
        </div>
      </section>

      <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <article class="rounded-2xl border border-[#DDEED8] bg-white p-5 shadow-sm">
          <p class="text-xs font-semibold uppercase tracking-[0.25em] text-[#72916F]">Registros</p>
          <p class="mt-3 text-3xl font-black text-[#16441A]">{{ totalFees() }}</p>
          <p class="mt-1 text-sm text-[#5F765B]">Comisiones operativas en la página actual.</p>
        </article>
        <article class="rounded-2xl border border-[#DDEED8] bg-white p-5 shadow-sm">
          <p class="text-xs font-semibold uppercase tracking-[0.25em] text-[#72916F]">Activas</p>
          <p class="mt-3 text-3xl font-black text-[#16441A]">{{ activeFees() }}</p>
          <p class="mt-1 text-sm text-[#5F765B]">Comisiones habilitadas para cobro.</p>
        </article>
        <article class="rounded-2xl border border-[#DDEED8] bg-white p-5 shadow-sm">
          <p class="text-xs font-semibold uppercase tracking-[0.25em] text-[#72916F]">Porcentaje</p>
          <p class="mt-3 text-3xl font-black text-[#16441A]">{{ percentageFees() }}</p>
          <p class="mt-1 text-sm text-[#5F765B]">Tarifas calculadas como porcentaje.</p>
        </article>
        <article class="rounded-2xl border border-[#DDEED8] bg-white p-5 shadow-sm">
          <p class="text-xs font-semibold uppercase tracking-[0.25em] text-[#72916F]">Fijas</p>
          <p class="mt-3 text-3xl font-black text-[#16441A]">{{ fixedFees() }}</p>
          <p class="mt-1 text-sm text-[#5F765B]">Tarifas calculadas por monto exacto.</p>
        </article>
      </section>

      @if (useCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-5 text-red-900 shadow-sm">
          <div class="flex items-start gap-3">
            <div class="mt-0.5 rounded-full bg-red-100 p-2 text-red-600">
              <lucide-icon name="alert-triangle" [size]="18"></lucide-icon>
            </div>
            <div>
              <h3 class="font-semibold">Error al cargar tarifas</h3>
              <p class="mt-1 text-sm text-red-700">{{ useCase.error() }}</p>
              <button type="button" (click)="loadFees(currentPage)" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 underline underline-offset-4">
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
          <p class="text-sm font-medium text-[#58705C]">Cargando comisiones...</p>
        </div>
      }

      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <section class="rounded-[2rem] border border-[#DDEED8] bg-white shadow-sm">
          <div class="flex flex-col gap-3 border-b border-[#E5EEDD] px-5 py-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h3 class="text-lg font-bold text-[#16441A]">Listado de comisiones</h3>
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
                  <th class="px-5 py-4 font-semibold">Operación</th>
                  <th class="px-5 py-4 font-semibold">Tipo</th>
                  <th class="px-5 py-4 font-semibold">Modo</th>
                  <th class="px-5 py-4 font-semibold">Valor</th>
                  <th class="px-5 py-4 font-semibold">Estado</th>
                  <th class="px-5 py-4 text-right font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-[#EEF4EA]">
                @for (fee of useCase.data(); track fee.id) {
                  <tr class="transition-colors hover:bg-[#FAFDF8]">
                    <td class="px-5 py-4">
                      <div class="space-y-1">
                        <p class="font-semibold text-[#18471C]">{{ operationLabel(fee.operationCode) }}</p>
                        <p class="text-xs text-[#6B8168]">{{ fee.description || 'Sin descripción' }}</p>
                      </div>
                    </td>
                    <td class="px-5 py-4">
                      <span class="inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold"
                        [ngClass]="fee.feeType === 'NONE' ? 'bg-[#EEF2EE] text-[#67796B]' : fee.feeType === 'FIXED' ? 'bg-[#F1F8E9] text-[#2E7D32]' : 'bg-[#E8F0FB] text-[#2F5D9A]'">
                        {{ feeTypeLabel(fee.feeType) }}
                      </span>
                    </td>
                    <td class="px-5 py-4 text-[#58705C]">{{ calculationModeLabel(fee.calculationMode) }}</td>
                    <td class="px-5 py-4 font-semibold text-[#18471C]">{{ feeValueLabel(fee) }}</td>
                    <td class="px-5 py-4">
                      <span class="inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold"
                        [ngClass]="fee.active ? 'bg-[#E8F5E9] text-[#2E7D32]' : 'bg-[#F1F4F1] text-[#6B7D6D]'">
                        {{ fee.active ? 'Activa' : 'Inactiva' }}
                      </span>
                    </td>
                    <td class="px-5 py-4">
                      <div class="flex justify-end gap-3">
                        <button
                          *ngIf="'fx.fees.update' | hasPermission"
                          type="button"
                          (click)="openEditForm(fee)"
                          class="cursor-pointer text-sm font-semibold text-[#2E7D32] hover:underline">
                          Editar
                        </button>
                        <button
                          *ngIf="'fx.fees.delete' | hasPermission"
                          type="button"
                          (click)="deleteFee(fee.id)"
                          class="cursor-pointer text-sm font-semibold text-red-600 hover:underline">
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-5 py-16 text-center text-[#6B8168]">
                      No hay comisiones registradas.
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
              (pageChange)="loadFees($event)"
            ></app-platform-pagination>
          </div>
        </section>
      }

      @if (isFormOpen) {
        <app-fx-fee-form
          [fee]="selectedFee"
          [loading]="isSubmitting"
          (formSubmit)="onFormSubmit($event)"
          (cancel)="closeForm()"
        ></app-fx-fee-form>
      }
    </div>
  `
})
export class FxFeesPageComponent implements OnInit {
  public readonly useCase = inject(FxFeesListUseCase);
  private readonly toastService = inject(ToastService);

  isFormOpen = false;
  selectedFee: OperationFeeResponse | null = null;
  isSubmitting = false;
  currentPage = 0;

  ngOnInit(): void {
    this.loadFees(0);
  }

  loadFees(page = 0): void {
    this.currentPage = page;
    void this.useCase.loadFees(page, 20);
  }

  totalFees(): number {
    return this.useCase.page()?.totalElements ?? 0;
  }

  activeFees(): number {
    return this.useCase.data().filter(fee => fee.active).length;
  }

  percentageFees(): number {
    return this.useCase.data().filter(fee => fee.feeType === 'PERCENTAGE').length;
  }

  fixedFees(): number {
    return this.useCase.data().filter(fee => fee.feeType === 'FIXED').length;
  }

  totalPagesLabel(): number {
    return this.useCase.page()?.totalPages ?? 0;
  }

  totalElementsLabel(): number {
    return this.useCase.page()?.totalElements ?? 0;
  }

  operationLabel(code: string): string {
    switch (code) {
      case 'TRANSFER': return 'Transferencia';
      case 'CONVERSION': return 'Conversión';
      case 'DEPOSIT': return 'Depósito';
      case 'WITHDRAWAL': return 'Retiro';
      case 'PAYMENT': return 'Pago';
      default: return code;
    }
  }

  feeTypeLabel(code: string): string {
    switch (code) {
      case 'NONE': return 'Sin comisión';
      case 'FIXED': return 'Fija';
      case 'PERCENTAGE': return 'Porcentaje';
      default: return code;
    }
  }

  calculationModeLabel(code: string): string {
    switch (code) {
      case 'SEPARATE': return 'Separado';
      case 'INCLUDED': return 'Incluido';
      default: return code;
    }
  }

  feeValueLabel(fee: OperationFeeResponse): string {
    if (fee.feeType === 'PERCENTAGE') {
      return `${fee.feeValue.toFixed(2)}%`;
    }
    return fee.feeValue.toFixed(2);
  }

  openCreateForm(): void {
    this.selectedFee = null;
    this.isFormOpen = true;
  }

  openEditForm(fee: OperationFeeResponse): void {
    this.selectedFee = fee;
    this.isFormOpen = true;
  }

  closeForm(): void {
    this.isFormOpen = false;
    this.selectedFee = null;
  }

  async onFormSubmit(event: { type: 'create' | 'update'; data: any }): Promise<void> {
    this.isSubmitting = true;
    try {
      if (event.type === 'create') {
        await this.useCase.createFee(event.data);
        this.toastService.success('Comisión creada exitosamente');
      } else {
        await this.useCase.updateFee(this.selectedFee!.id, event.data);
        this.toastService.success('Comisión actualizada exitosamente');
      }
      this.closeForm();
    } catch (error: any) {
      let msg = error.error?.message || error.message || 'Error al procesar la solicitud';

      if (msg.includes('already exists')) {
        msg = 'Ya existe una comisión operativa registrada con ese código.';
      } else if (msg.includes('Fee value must be zero')) {
        msg = 'Si el tipo de comisión es "Sin Comisión", el valor debe ser exactamente 0.';
      }

      this.toastService.error(msg, 'No se pudo guardar');
    } finally {
      this.isSubmitting = false;
    }
  }

  async deleteFee(id: string): Promise<void> {
    if (!confirm('¿Estás seguro de eliminar esta comisión?')) {
      return;
    }

    const success = await this.useCase.deleteFee(id);
    if (success) {
      this.toastService.success('Comisión eliminada');
    } else {
      this.toastService.error('No se pudo eliminar la comisión');
    }
  }
}
