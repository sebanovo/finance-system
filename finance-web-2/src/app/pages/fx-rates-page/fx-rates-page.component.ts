import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FxRatesListUseCase, FxRateFormComponent } from '../../features/fx-management';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { FxExchangeRateResponse } from '../../entities/fx';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-fx-rates-page',
  standalone: true,
  imports: [CommonModule, FxRateFormComponent, HasPermissionPipe, LucideAngularModule],
  providers: [DatePipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Tipos de Cambio</h2>
          <p class="text-muted-foreground">Gestiona las tasas de cambio de divisas del sistema.</p>
        </div>
        
        <button 
          *ngIf="'fx.rates.create' | hasPermission"
          (click)="openCreateForm()"
          type="button"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon name="plus" [size]="16"></lucide-icon>
          Nueva Tasa
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar tipos de cambio</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadRates()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando tasas...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Par de Divisas</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tasa (Rate)</th>
                  <th scope="col" class="px-6 py-4 font-medium">Descripción</th>
                  <th scope="col" class="px-6 py-4 font-medium">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium">Última Actualización</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (rate of useCase.data(); track rate.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-2">
                        <span class="font-bold text-foreground">{{ rate.sourceCurrency }}</span>
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-muted-foreground"><path d="m9 18 6-6-6-6"/></svg>
                        <span class="font-bold text-foreground">{{ rate.targetCurrency }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4 font-medium text-foreground">{{ rate.rate | number:'1.2-6' }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ rate.description || '-' }}</td>
                    <td class="px-6 py-4">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="rate.active ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-gray-600'">
                        {{ rate.active ? 'ACTIVA' : 'INACTIVA' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-muted-foreground">{{ rate.updatedAt | date:'short' }}</td>
                    <td class="px-6 py-4 text-right space-x-3">
                      <button *ngIf="'fx.rates.update' | hasPermission" (click)="openEditForm(rate)" class="text-primary hover:underline text-xs font-medium">Editar</button>
                      <button *ngIf="'fx.rates.delete' | hasPermission" (click)="deleteRate(rate.id)" class="text-destructive hover:underline text-xs font-medium">Eliminar</button>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-muted-foreground">
                      No hay tasas de cambio registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
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

  ngOnInit() {
    this.loadRates();
  }

  loadRates() {
    this.useCase.loadRates();
  }

  openCreateForm() {
    this.selectedRate = null;
    this.isFormOpen = true;
  }

  openEditForm(rate: FxExchangeRateResponse) {
    this.selectedRate = rate;
    this.isFormOpen = true;
  }

  closeForm() {
    this.isFormOpen = false;
    this.selectedRate = null;
  }

  async onFormSubmit(event: { type: 'create' | 'update', data: any }) {
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
      
      // Traducciones amigables para el usuario
      if (msg.includes('already exists for pair')) {
        msg = 'Ya existe una tasa de cambio activa para este par de divisas.';
      } else if (msg.includes('already exists')) {
        msg = 'Esta tasa de cambio ya está registrada en el sistema.';
      } else if (msg.includes('Self exchange rate must be 1')) {
        msg = 'La tasa de cambio para una misma moneda (ej. USD a USD) debe ser exactamente 1.';
      }
      
      this.toastService.error(msg, 'No se pudo guardar');
    } finally {
      this.isSubmitting = false;
    }
  }

  async deleteRate(id: string) {
    if (confirm('¿Estás seguro de eliminar este tipo de cambio?')) {
      const success = await this.useCase.deleteRate(id);
      if (success) {
        this.toastService.success('Tasa de cambio eliminada');
      } else {
        this.toastService.error('No se pudo eliminar la tasa de cambio');
      }
    }
  }
}
