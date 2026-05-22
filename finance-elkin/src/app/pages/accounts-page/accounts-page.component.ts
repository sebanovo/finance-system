import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { AccountListUseCase, AccountFormComponent } from '../../features/account-management';
import { LucideAngularModule, Plus, MoreHorizontal, CheckCircle, Play, Ban, Snowflake, XCircle, Pencil, Wallet } from 'lucide-angular';
import { AccountOwnerResponse, CreateAccountRequest, UpdateAccountRequest } from '../../entities/accounts';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-accounts-page',
  standalone: true,
  imports: [CommonModule, AccountFormComponent, LucideAngularModule, HasPermissionPipe],
  providers: [CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Cuentas Bancarias</h2>
          <p class="text-muted-foreground">Administra las cuentas bancarias de los clientes del sistema.</p>
        </div>
        
        <button 
          *ngIf="'accounts.create' | hasPermission"
          (click)="openCreateForm()"
          type="button"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon name="plus" [size]="16"></lucide-icon>
          Nueva Cuenta
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar cuentas</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadAccounts()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
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
          <p>Cargando cuentas...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm">
          <div class="overflow-x-auto min-h-[300px]">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Cliente</th>
                  <th scope="col" class="px-6 py-4 font-medium">Nº Cuenta / Alias</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Saldo Disp.</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Saldo Reten.</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center w-16">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (account of useCase.data(); track account.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4">
                      <div class="font-medium text-foreground">{{ account.userFullName }}</div>
                      <div class="text-xs text-muted-foreground">{{ account.userEmail }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="font-medium text-foreground">{{ account.accountNumber }}</div>
                      <div class="text-xs text-muted-foreground">{{ account.customAlias || account.accountNameLabel }}</div>
                    </td>
                    <td class="px-6 py-4 text-muted-foreground">{{ account.accountType }} - {{ account.currency }}</td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">{{ account.availableBalance | currency:account.currency }}</td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">{{ account.heldBalance | currency:account.currency }}</td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="{
                              'bg-green-500/10 text-green-600': account.status === 'ACTIVE',
                              'bg-yellow-500/10 text-yellow-600': account.status === 'FROZEN' || account.status === 'PENDING_APPROVAL',
                              'bg-red-500/10 text-red-600': account.status === 'BLOCKED' || account.status === 'CLOSED'
                            }">
                        {{ account.status }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-center">
                      <div class="relative group inline-block text-left">
                        <button class="p-2 rounded-md hover:bg-muted text-muted-foreground transition-colors focus:outline-none">
                          <lucide-icon name="more-horizontal" [size]="16"></lucide-icon>
                        </button>
                        <div class="absolute right-0 mt-1 w-48 bg-card border border-border rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-50 overflow-hidden">
                          <div class="py-1">
                            <button *ngIf="'accounts.balance.read' | hasPermission" (click)="viewBalance(account)" class="w-full text-left px-4 py-2 text-xs text-foreground hover:bg-muted transition-colors flex items-center gap-2">
                              <lucide-icon name="wallet" [size]="14"></lucide-icon> Ver Saldo
                            </button>
                            <button *ngIf="'accounts.update' | hasPermission" (click)="openEditForm(account)" class="w-full text-left px-4 py-2 text-xs text-foreground hover:bg-muted transition-colors flex items-center gap-2">
                              <lucide-icon name="pencil" [size]="14"></lucide-icon> Editar
                            </button>
                            
                            <div class="h-px bg-border my-1"></div>
                            
                            <!-- Acciones de estado (condicionales al rol) -->
                            <button *ngIf="('accounts.approve' | hasPermission) && account.status === 'PENDING_APPROVAL'" (click)="changeState(account, 'approve')" class="w-full text-left px-4 py-2 text-xs text-green-600 hover:bg-green-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="check-circle" [size]="14"></lucide-icon> Aprobar
                            </button>
                            <button *ngIf="('accounts.activate' | hasPermission) && (account.status === 'PENDING_APPROVAL' || account.status === 'FROZEN')" (click)="changeState(account, 'activate')" class="w-full text-left px-4 py-2 text-xs text-green-600 hover:bg-green-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="play" [size]="14"></lucide-icon> Activar
                            </button>
                            <button *ngIf="('accounts.block' | hasPermission) && account.status !== 'BLOCKED' && account.status !== 'CLOSED'" (click)="changeStateWithReason(account, 'block')" class="w-full text-left px-4 py-2 text-xs text-red-600 hover:bg-red-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="ban" [size]="14"></lucide-icon> Bloquear
                            </button>
                            <button *ngIf="('accounts.freeze' | hasPermission) && account.status === 'ACTIVE'" (click)="changeStateWithReason(account, 'freeze')" class="w-full text-left px-4 py-2 text-xs text-yellow-600 hover:bg-yellow-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="snowflake" [size]="14"></lucide-icon> Congelar
                            </button>
                            <button *ngIf="('accounts.close' | hasPermission) && account.status !== 'CLOSED'" (click)="changeStateWithReason(account, 'close')" class="w-full text-left px-4 py-2 text-xs text-red-600 hover:bg-red-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="x-circle" [size]="14"></lucide-icon> Cerrar Cuenta
                            </button>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-muted-foreground">
                      No hay cuentas bancarias registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>

    <!-- Formulario Slide-over -->
    <app-account-form
      [isOpen]="isFormOpen"
      [account]="selectedAccount"
      (closed)="isFormOpen = false"
      (saved)="onFormSaved($event)">
    </app-account-form>

    <!-- Modal de Saldo -->
    <div *ngIf="balanceData" class="fixed inset-0 z-[100] flex items-center justify-center">
      <!-- Overlay blur -->
      <div 
        class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity"
        (click)="closeBalanceModal()">
      </div>
      
      <!-- Contenido del modal -->
      <div class="relative w-full max-w-sm bg-card rounded-2xl shadow-2xl border border-border p-6 transform transition-all">
        <!-- Cerrar -->
        <button 
          (click)="closeBalanceModal()"
          class="absolute right-4 top-4 p-2 rounded-full text-muted-foreground hover:bg-muted hover:text-foreground transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>

        <div class="flex flex-col items-center text-center mt-2">
          <div class="h-16 w-16 bg-primary/10 text-primary rounded-full flex items-center justify-center mb-4">
            <lucide-icon name="wallet" [size]="32"></lucide-icon>
          </div>
          <h3 class="text-xl font-bold text-foreground">Saldo de la Cuenta</h3>
          <p class="text-sm text-muted-foreground mt-1">{{ balanceData.accountNumber }}</p>
          <p class="text-xs text-muted-foreground">{{ balanceData.accountNameLabel || balanceData.customAlias }}</p>
        </div>

        <div class="mt-8 space-y-4">
          <div class="bg-green-500/10 border border-green-500/20 rounded-xl p-4 flex flex-col items-center">
            <span class="text-xs font-semibold text-green-600 uppercase tracking-wider">Saldo Disponible</span>
            <span class="text-2xl font-black text-green-700 dark:text-green-500 mt-1">
              {{ balanceData.availableBalance | currency:balanceData.currency }}
            </span>
          </div>

          <div class="bg-muted rounded-xl p-4 flex justify-between items-center">
            <span class="text-sm font-medium text-muted-foreground">Saldo Retenido</span>
            <span class="text-base font-bold text-foreground">
              {{ balanceData.heldBalance | currency:balanceData.currency }}
            </span>
          </div>
        </div>

        <button 
          (click)="closeBalanceModal()"
          class="mt-6 w-full bg-primary text-primary-foreground hover:bg-primary/90 font-medium py-2.5 rounded-xl transition-colors">
          Entendido
        </button>
      </div>
    </div>
  `
})
export class AccountsPageComponent implements OnInit {
  public readonly useCase = inject(AccountListUseCase);
  private readonly toastService = inject(ToastService);

  isFormOpen = false;
  selectedAccount: AccountOwnerResponse | null = null;
  balanceData: any = null;

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.useCase.loadAccounts();
  }

  openCreateForm() {
    this.selectedAccount = null;
    this.isFormOpen = true;
  }

  openEditForm(account: AccountOwnerResponse) {
    this.selectedAccount = account;
    this.isFormOpen = true;
  }

  async onFormSaved(event: { request: CreateAccountRequest | UpdateAccountRequest, isEditing: boolean }) {
    try {
      if (event.isEditing && this.selectedAccount) {
        await this.useCase.updateAccount(this.selectedAccount.id, event.request as UpdateAccountRequest);
      } else {
        await this.useCase.createAccount(event.request as CreateAccountRequest);
      }
      this.isFormOpen = false;
    } catch (error) {
      this.toastService.error('Error al guardar la cuenta: ' + error);
    }
  }

  async changeState(account: AccountOwnerResponse, action: 'approve' | 'activate') {
    if (confirm(`¿Estás seguro que deseas ${action === 'approve' ? 'aprobar' : 'activar'} esta cuenta?`)) {
      try {
        await this.useCase.changeAccountState(account.id, action);
        this.toastService.success(`Cuenta ${action === 'approve' ? 'aprobada' : 'activada'} con éxito.`);
      } catch (error) {
        this.toastService.error(`Error al ${action === 'approve' ? 'aprobar' : 'activar'}: ` + error);
      }
    }
  }

  async changeStateWithReason(account: AccountOwnerResponse, action: 'block' | 'freeze' | 'close') {
    const actionLabel = action === 'block' ? 'bloquear' : action === 'freeze' ? 'congelar' : 'cerrar';
    const reason = prompt(`Por favor, ingresa el motivo para ${actionLabel} la cuenta:`);
    if (reason !== null) {
      try {
        await this.useCase.changeAccountState(account.id, action, reason);
        this.toastService.success(`Cuenta ${actionLabel}da con éxito.`);
      } catch (error) {
        this.toastService.error('Error al cambiar estado: ' + error);
      }
    }
  }

  async viewBalance(account: AccountOwnerResponse) {
    try {
      const response = await this.useCase.getAccountBalance(account.id);
      if (response && response.data) {
        this.balanceData = response.data;
      }
    } catch (error) {
      this.toastService.error('Error al consultar saldo: ' + error);
    }
  }

  closeBalanceModal() {
    this.balanceData = null;
  }
}
