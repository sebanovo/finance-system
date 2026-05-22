import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, PercentPipe, CurrencyPipe } from '@angular/common';
import { FxFeesListUseCase } from '../../features/fx-management';

@Component({
  selector: 'app-fx-fees-page',
  standalone: true,
  imports: [CommonModule],
  providers: [DatePipe, PercentPipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Comisiones Operativas</h2>
          <p class="text-muted-foreground">Gestiona las tarifas cobradas por operaciones en la plataforma.</p>
        </div>
        
        <button 
          type="button"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg>
          Nueva Comisión
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar comisiones</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadFees()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
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
          <p>Cargando tarifas...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Código Operación</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo Comisión</th>
                  <th scope="col" class="px-6 py-4 font-medium">Modo Cálculo</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Valor</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (fee of useCase.data(); track fee.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4 font-medium text-foreground">{{ fee.operationCode }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ fee.feeType }}</td>
                    <td class="px-6 py-4 text-muted-foreground">
                      <span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-secondary text-secondary-foreground">
                        {{ fee.calculationMode }}
                      </span>
                    </td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">
                      @if (fee.calculationMode === 'PERCENTAGE') {
                        {{ fee.feeValue / 100 | percent:'1.2-2' }}
                      } @else {
                        {{ fee.feeValue | currency:'USD' }}
                      }
                    </td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="fee.active ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-gray-600'">
                        {{ fee.active ? 'ACTIVA' : 'INACTIVA' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right space-x-3">
                      <button class="text-primary hover:underline text-xs font-medium">Editar</button>
                      <button (click)="deleteFee(fee.id)" class="text-destructive hover:underline text-xs font-medium">Eliminar</button>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-muted-foreground">
                      No hay comisiones registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>
  `
})
export class FxFeesPageComponent implements OnInit {
  public readonly useCase = inject(FxFeesListUseCase);

  ngOnInit() {
    this.loadFees();
  }

  loadFees() {
    this.useCase.loadFees();
  }

  deleteFee(id: string) {
    if (confirm('¿Estás seguro de eliminar esta comisión?')) {
      this.useCase.deleteFee(id);
    }
  }
}
