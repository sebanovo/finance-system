import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionsListUseCase, TransactionSlideOverComponent, TransactionActionType } from '../../features/transactions-management';
import { TransactionResponse } from '../../entities/transactions';
import { LucideAngularModule, Plus, ChevronDown, MoreHorizontal, RotateCcw, Reply, CheckCircle, XCircle, AlertTriangle, CornerDownLeft, X } from 'lucide-angular';
import { HasPermissionPipe, HasAnyPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-transactions-page',
  standalone: true,
  imports: [CommonModule, FormsModule, TransactionSlideOverComponent, LucideAngularModule, HasPermissionPipe, HasAnyPermissionPipe],
  providers: [DatePipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Transacciones</h2>
          <p class="text-muted-foreground">Historial completo de movimientos, transferencias, pagos y retiros en el tenant.</p>
        </div>
        
        <!-- Dropdown Nueva Transacción -->
        <div class="relative group">
          <button 
            *ngIf="['transactions.create.deposit', 'transactions.create.withdrawal', 'transactions.create.transfer', 'transactions.create.payment'] | hasAnyPermission"
            type="button"
            class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
            <lucide-icon name="plus" [size]="16"></lucide-icon>
            Nueva Transacción
            <lucide-icon name="chevron-down" [size]="14" class="opacity-70 ml-1"></lucide-icon>
          </button>
          
          <div class="absolute right-0 top-full mt-2 w-48 bg-card border border-border rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-10 overflow-hidden">
            <div class="py-1">
              <button *ngIf="'transactions.create.deposit' | hasPermission" (click)="openSlideOver('deposit')" class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Depósito</button>
              <button *ngIf="'transactions.create.withdrawal' | hasPermission" (click)="openSlideOver('withdrawal')" class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Retiro</button>
              <button *ngIf="'transactions.create.transfer' | hasPermission" (click)="openSlideOver('transfer')" class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Transferencia</button>
              <button *ngIf="'transactions.create.payment' | hasPermission" (click)="openSlideOver('payment')" class="w-full text-left px-4 py-2 text-sm text-foreground hover:bg-muted transition-colors">Crear Pago</button>
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
        <div class="bg-card border border-border rounded-xl shadow-sm">
          <div class="overflow-x-auto min-h-[300px]">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo y Canal</th>
                  <th scope="col" class="px-6 py-4 font-medium">Cuentas Involucradas</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Monto</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Fecha</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center w-16">Acciones</th>
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
                    <td class="px-6 py-4 text-center">
                      <div class="relative group inline-block text-left">
                        <button class="p-2 rounded-md hover:bg-muted text-muted-foreground transition-colors focus:outline-none">
                          <lucide-icon name="more-horizontal" [size]="16"></lucide-icon>
                        </button>
                        <div class="absolute right-0 mt-1 w-48 bg-card border border-border rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-50 overflow-hidden">
                          <div class="py-1">
                            <!-- Acciones (ej. Reverse, Refund) -->
                            <button *ngIf="('transactions.reverse' | hasPermission) && tx.status === 'COMPLETED'" (click)="reverseTransaction(tx.id)" class="w-full text-left px-4 py-2 text-xs text-orange-600 hover:bg-orange-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="rotate-ccw" [size]="14"></lucide-icon> Revertir
                            </button>
                            <button *ngIf="('transactions.refund' | hasPermission) && tx.status === 'COMPLETED'" (click)="refundTransaction(tx.id)" class="w-full text-left px-4 py-2 text-xs text-blue-600 hover:bg-blue-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="reply" [size]="14"></lucide-icon> Reembolsar
                            </button>
                            <!-- Placeholder si no hay acciones -->
                            <div *ngIf="!('transactions.reverse' | hasPermission) && !('transactions.refund' | hasPermission)" class="px-4 py-2 text-xs text-muted-foreground italic text-center">
                              Sin acciones
                            </div>
                          </div>
                        </div>
                      </div>
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

    <!-- Formulario Lateral -->
    <app-transaction-slide-over
      [isOpen]="isSlideOverOpen"
      [transactionType]="selectedTransactionType"
      (closed)="isSlideOverOpen = false"
      (saved)="onTransactionSaved($event)">
    </app-transaction-slide-over>

    <!-- Modal de Acción (Revertir/Reembolsar) -->
    <div *ngIf="actionModalOpen" class="fixed inset-0 z-[100] flex items-start justify-center p-4 sm:items-center">
      <!-- Overlay blur -->
      <div 
        class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity"
        (click)="closeActionModal()">
      </div>
      
      <!-- Contenido del modal -->
      <div class="relative w-full max-w-md max-h-[calc(100vh-2rem)] overflow-y-auto overscroll-contain bg-card rounded-2xl shadow-2xl border border-border p-6 transform transition-all">
        <!-- Cerrar -->
        <button 
          (click)="closeActionModal()"
          class="absolute right-4 top-4 p-2 rounded-full text-muted-foreground hover:bg-muted hover:text-foreground transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>

        <div class="flex flex-col items-center text-center mt-2">
          <div 
            class="h-16 w-16 rounded-full flex items-center justify-center mb-4"
            [ngClass]="actionModalType === 'reverse' ? 'bg-orange-500/10 text-orange-600' : 'bg-blue-500/10 text-blue-600'">
            <lucide-icon [name]="actionModalType === 'reverse' ? 'rotate-ccw' : 'corner-down-left'" [size]="32"></lucide-icon>
          </div>
          <h3 class="text-xl font-bold text-foreground">
            {{ actionModalType === 'reverse' ? 'Revertir Transacción' : 'Reembolsar Transacción' }}
          </h3>
          <p class="text-sm text-muted-foreground mt-1">
            Por favor, ingresa los detalles para procesar esta solicitud.
          </p>
        </div>

        <div class="mt-6 space-y-4">
          <!-- Motivo (Requerido para ambos) -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">Motivo</label>
            <input 
              type="text" 
              [(ngModel)]="actionReason"
              placeholder="Ej. Error de sistema, solicitud del cliente"
              class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2" />
          </div>

          <!-- Monto (Solo para Reembolso) -->
          <div *ngIf="actionModalType === 'refund'" class="space-y-2">
            <label class="text-sm font-medium text-foreground">Monto a reembolsar</label>
            <div class="relative">
              <span class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground font-medium text-sm">
                {{ selectedTransactionForAction?.currency }}
              </span>
              <input 
                type="number" 
                [(ngModel)]="actionAmount"
                min="0.01" step="0.01"
                class="flex h-10 w-full rounded-md border border-input bg-background pl-12 pr-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2" />
            </div>
            <p class="text-xs text-muted-foreground">Original: {{ selectedTransactionForAction?.amount | currency:selectedTransactionForAction?.currency }}</p>
          </div>
          
          <!-- Mensaje de Error (Si lo hay) -->
          <div *ngIf="actionError" class="p-3 bg-red-500/10 border border-red-500/20 text-red-600 rounded-md text-xs font-medium">
            {{ actionError }}
          </div>
        </div>

        <div class="mt-8 flex gap-3">
          <button 
            (click)="closeActionModal()"
            class="flex-1 inline-flex items-center justify-center rounded-xl text-sm font-medium border border-input bg-background hover:bg-muted h-10 transition-colors">
            Cancelar
          </button>
          <button 
            (click)="confirmAction()"
            [disabled]="!actionReason || (actionModalType === 'refund' && (!actionAmount || actionAmount <= 0)) || isProcessingAction"
            class="flex-1 inline-flex items-center justify-center rounded-xl text-sm font-medium h-10 transition-colors disabled:opacity-50"
            [ngClass]="actionModalType === 'reverse' ? 'bg-orange-600 hover:bg-orange-700 text-white' : 'bg-blue-600 hover:bg-blue-700 text-white'">
            <lucide-icon *ngIf="!isProcessingAction" [name]="actionModalType === 'reverse' ? 'rotate-ccw' : 'check'" [size]="16" class="mr-2"></lucide-icon>
            <svg *ngIf="isProcessingAction" class="animate-spin h-4 w-4 mr-2" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ actionModalType === 'reverse' ? 'Confirmar Reversión' : 'Confirmar Reembolso' }}
          </button>
        </div>
      </div>
    </div>
  `
})
export class TransactionsPageComponent implements OnInit {
  public readonly useCase = inject(TransactionsListUseCase);
  private readonly toastService = inject(ToastService);

  isSlideOverOpen = false;
  selectedTransactionType: TransactionActionType = 'deposit';

  // Action Modal State (Reverse / Refund)
  actionModalOpen = false;
  actionModalType: 'reverse' | 'refund' | null = null;
  selectedTransactionForAction: TransactionResponse | null = null;
  actionReason = '';
  actionAmount: number | null = null;
  actionError: string | null = null;
  isProcessingAction = false;

  ngOnInit() {
    this.loadTransactions();
  }

  loadTransactions() {
    this.useCase.loadTransactions();
  }

  openSlideOver(type: TransactionActionType) {
    this.selectedTransactionType = type;
    this.isSlideOverOpen = true;
  }

  async onTransactionSaved(event: { type: TransactionActionType, request: any }) {
    try {
      await this.useCase.executeTransaction(event.type, event.request);
      this.toastService.success('Transacción procesada con éxito.');
      this.isSlideOverOpen = false;
    } catch (error) {
      this.toastService.error('Error al procesar la transacción: ' + error);
    }
  }

  reverseTransaction(id: string) {
    const tx = this.useCase.data().find(t => t.id === id);
    if (!tx) return;
    this.openActionModal('reverse', tx);
  }

  refundTransaction(id: string) {
    const tx = this.useCase.data().find(t => t.id === id);
    if (!tx) return;
    this.openActionModal('refund', tx);
  }

  openActionModal(type: 'reverse' | 'refund', transaction: TransactionResponse) {
    this.actionModalType = type;
    this.selectedTransactionForAction = transaction;
    this.actionReason = '';
    this.actionAmount = transaction.amount; // default to full refund
    this.actionError = null;
    this.isProcessingAction = false;
    this.actionModalOpen = true;
  }

  closeActionModal() {
    this.actionModalOpen = false;
    this.selectedTransactionForAction = null;
    this.actionModalType = null;
  }

  async confirmAction() {
    if (!this.selectedTransactionForAction || !this.actionReason || !this.actionModalType) return;
    if (this.actionModalType === 'refund' && (!this.actionAmount || this.actionAmount <= 0)) return;

    this.isProcessingAction = true;
    this.actionError = null;

    try {
      if (this.actionModalType === 'reverse') {
        await this.useCase.reverseTransaction(this.selectedTransactionForAction.id, this.actionReason);
      } else if (this.actionModalType === 'refund') {
        await this.useCase.refundTransaction(this.selectedTransactionForAction.id, this.actionReason, this.actionAmount!);
      }
      this.closeActionModal();
    } catch (error: any) {
      this.actionError = error.message || 'Error al procesar la operación';
    } finally {
      this.isProcessingAction = false;
    }
  }
}
