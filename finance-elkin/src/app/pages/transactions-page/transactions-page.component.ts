import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { TransactionsListUseCase } from '../../features/transactions-management';

@Component({
  selector: 'app-transactions-page',
  standalone: true,
  imports: [CommonModule],
  providers: [DatePipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Transacciones</h2>
          <p class="text-muted-foreground">Historial completo de movimientos, transferencias, pagos y retiros en el tenant.</p>
        </div>
        
        <!-- Dropdown / Botón Placeholder -->
        <div class="relative group">
          <button 
            type="button"
            class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 5v14"/><path d="M5 12h14"/></svg>
            Nueva Transacción
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ml-1 opacity-70"><path d="m6 9 6 6 6-6"/></svg>
          </button>
          
          <div class="absolute right-0 top-full mt-2 w-48 bg-card border border-border rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-10 overflow-hidden">
            <div class="py-1">
              <button class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Depósito</button>
              <button class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Transferencia</button>
              <button class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Retiro</button>
              <button class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Pago</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar transacciones</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadTransactions()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
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
          <p>Cargando historial...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo y Canal</th>
                  <th scope="col" class="px-6 py-4 font-medium">Cuentas Involucradas</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Monto</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Fecha</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (tx of useCase.data(); track tx.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4">
                      <div class="font-bold text-foreground flex items-center gap-2">
                        <span>{{ tx.type }}</span>
                      </div>
                      <div class="text-xs text-muted-foreground mt-0.5">Canal: {{ tx.channel }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="flex flex-col gap-1">
                        @if (tx.sourceAccountId) {
                          <div class="text-xs flex items-center gap-1">
                            <span class="text-muted-foreground">Origen:</span> 
                            <span class="font-medium text-foreground" [title]="tx.sourceAccountId">{{ tx.sourceAccountDisplayName || tx.sourceAccountNumber }}</span>
                          </div>
                        }
                        @if (tx.targetAccountId) {
                          <div class="text-xs flex items-center gap-1">
                            <span class="text-muted-foreground">Destino:</span> 
                            <span class="font-medium text-foreground" [title]="tx.targetAccountId">{{ tx.targetAccountDisplayName || tx.targetAccountNumber }}</span>
                          </div>
                        }
                        @if (!tx.sourceAccountId && !tx.targetAccountId) {
                          <span class="text-xs text-muted-foreground italic">No aplica</span>
                        }
                      </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="font-bold text-foreground">
                        {{ tx.amount | number:'1.2-2' }} <span class="text-xs ml-0.5">{{ tx.currency }}</span>
                      </div>
                      @if (tx.fxDetail) {
                        <div class="text-[10px] text-muted-foreground mt-0.5" title="Exchange Rate">
                          Rate: {{ tx.fxDetail.exchangeRate | number:'1.6-6' }}
                        </div>
                      }
                    </td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-[10px] font-bold rounded-full uppercase tracking-wider inline-flex items-center gap-1"
                            [ngClass]="{
                              'bg-green-500/10 text-green-600': tx.status === 'COMPLETED',
                              'bg-amber-500/10 text-amber-600': tx.status === 'PENDING' || tx.status === 'HELD',
                              'bg-red-500/10 text-red-600': tx.status === 'FAILED' || tx.status === 'REJECTED' || tx.status === 'REVERSED',
                              'bg-gray-500/10 text-gray-600': tx.status !== 'COMPLETED' && tx.status !== 'PENDING' && tx.status !== 'HELD' && tx.status !== 'FAILED' && tx.status !== 'REJECTED' && tx.status !== 'REVERSED'
                            }">
                        @if (tx.status === 'COMPLETED') {
                          <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                        } @else if (tx.status === 'FAILED') {
                          <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                        }
                        {{ tx.status }}
                      </span>
                      @if (tx.failureReason) {
                        <div class="text-[9px] text-destructive mt-1 max-w-[120px] truncate mx-auto" [title]="tx.failureReason">
                          {{ tx.failureReason }}
                        </div>
                      }
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="text-sm text-foreground">{{ (tx.processedAt || tx.createdAt) | date:'MMM d, y' }}</div>
                      <div class="text-xs text-muted-foreground">{{ (tx.processedAt || tx.createdAt) | date:'shortTime' }}</div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="5" class="px-6 py-12 text-center text-muted-foreground">
                      No hay transacciones registradas.
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
export class TransactionsPageComponent implements OnInit {
  public readonly useCase = inject(TransactionsListUseCase);

  ngOnInit() {
    this.loadTransactions();
  }

  loadTransactions() {
    this.useCase.loadTransactions();
  }
}
