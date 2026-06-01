import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component, computed, inject, OnInit } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { AccountFormComponent, AccountListUseCase } from '../../features/account-management';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { AccountOwnerResponse, CreateAccountRequest, UpdateAccountRequest } from '../../entities/accounts';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-accounts-page',
  standalone: true,
  imports: [CommonModule, AccountFormComponent, PlatformPaginationComponent, LucideAngularModule, HasPermissionPipe],
  providers: [CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="space-y-3">
            <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
              <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
              Owner Admin
            </div>
            <div>
              <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
                Cuentas bancarias
              </h1>
              <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                Administra las cuentas bancarias del tenant con una vista clara y consistente.
              </p>
            </div>
          </div>

          <div class="flex flex-wrap gap-3">
            <button
              type="button"
              (click)="reload()"
              class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              <lucide-icon name="refresh-ccw" [size]="16"></lucide-icon>
              Recargar
            </button>

            <button 
              *ngIf="'accounts.create' | hasPermission"
              (click)="openCreateForm()"
              type="button"
              class="inline-flex cursor-pointer items-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428] shadow-sm">
              <lucide-icon name="plus" [size]="16"></lucide-icon>
              Nueva cuenta
            </button>
          </div>
        </div>
      </section>

      <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Cuentas en página</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ accountStats().total }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Cargadas actualmente en pantalla</p>
        </div>

        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Activas</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ accountStats().active }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Disponibles para operación</p>
        </div>

        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Pendientes</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ accountStats().pending }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">En espera de aprobación</p>
        </div>

        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Restringidas</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ accountStats().restricted }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Bloqueadas, congeladas o cerradas</p>
        </div>
      </section>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6 flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-red-700">Error al cargar cuentas</h3>
            <p class="mt-1 text-sm text-red-700/80">{{ useCase.error() }}</p>
            <button (click)="reload()" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center gap-4 p-12 text-[#6B7D6C]">
          <svg class="h-8 w-8 animate-spin text-[#2E7D32]" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando cuentas...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        @if (useCase.page(); as page) {
          <div class="flex items-center justify-between gap-3 rounded-2xl border border-[#DDEED8] bg-white px-4 py-3 text-sm text-[#567157]">
            <p>
              <span class="font-semibold text-[#1B5E20]">{{ page.totalElements }}</span>
              cuenta(s) registradas
            </p>
            <p>
              Página <span class="font-semibold text-[#1B5E20]">{{ page.number + 1 }}</span>
              de <span class="font-semibold text-[#1B5E20]">{{ page.totalPages }}</span>
            </p>
          </div>
        }

        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
          <div class="overflow-x-auto min-h-[300px]">
            <table class="w-full text-left text-sm">
              <thead class="border-b border-[#E8F2E2] bg-[#F7FBF3] text-xs uppercase tracking-[0.12em] text-[#6B7D6C]">
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
              <tbody class="divide-y divide-[#EEF5EA] bg-white">
                @for (account of useCase.data(); track account.id) {
                  <tr class="transition-colors hover:bg-[#F7FBF3]">
                    <td class="px-6 py-4">
                      <div class="font-semibold text-[#1B5E20]">{{ account.userFullName }}</div>
                      <div class="text-xs text-[#6B7D6C]">{{ account.userEmail }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="font-semibold text-[#1B5E20]">{{ account.accountNumber }}</div>
                      <div class="text-xs text-[#6B7D6C]">{{ account.customAlias || account.accountNameLabel }}</div>
                    </td>
                    <td class="px-6 py-4 text-[#4F5D4F]">{{ account.accountType }} - {{ account.currency }}</td>
                    <td class="px-6 py-4 font-semibold text-right text-[#1B5E20]">{{ account.availableBalance | currency:account.currency }}</td>
                    <td class="px-6 py-4 font-semibold text-right text-[#1B5E20]">{{ account.heldBalance | currency:account.currency }}</td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full" [ngClass]="accountStatusClass(account.status)">
                        {{ accountStatusLabel(account.status) }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-center">
                      <div class="relative group inline-block text-left">
                        <button class="cursor-pointer p-2 rounded-md hover:bg-muted text-muted-foreground transition-colors focus:outline-none">
                          <lucide-icon name="more-horizontal" [size]="16"></lucide-icon>
                        </button>
                        <div class="absolute right-0 mt-1 w-48 bg-card border border-border rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-50 overflow-hidden">
                          <div class="py-1">
                            <button *ngIf="'accounts.balance.read' | hasPermission" (click)="viewBalance(account)" class="w-full cursor-pointer text-left px-4 py-2 text-xs text-foreground hover:bg-muted transition-colors flex items-center gap-2">
                              <lucide-icon name="wallet" [size]="14"></lucide-icon> Ver Saldo
                            </button>
                            <button *ngIf="'accounts.update' | hasPermission" (click)="openEditForm(account)" class="w-full cursor-pointer text-left px-4 py-2 text-xs text-foreground hover:bg-muted transition-colors flex items-center gap-2">
                              <lucide-icon name="pencil" [size]="14"></lucide-icon> Editar
                            </button>
                            
                            <div class="h-px bg-border my-1"></div>
                            
                            <!-- Acciones de estado (condicionales al rol) -->
                            <button *ngIf="('accounts.approve' | hasPermission) && account.status === 'PENDING_APPROVAL'" (click)="changeState(account, 'approve')" class="w-full cursor-pointer text-left px-4 py-2 text-xs text-green-600 hover:bg-green-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="check-circle" [size]="14"></lucide-icon> Aprobar
                            </button>
                            <button *ngIf="('accounts.activate' | hasPermission) && account.status !== 'ACTIVE' && account.status !== 'CLOSED'" (click)="changeState(account, 'activate')" class="w-full cursor-pointer text-left px-4 py-2 text-xs text-green-600 hover:bg-green-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="play" [size]="14"></lucide-icon> Activar
                            </button>
                            <button *ngIf="('accounts.block' | hasPermission) && account.status !== 'BLOCKED' && account.status !== 'CLOSED'" (click)="changeStateWithReason(account, 'block')" class="w-full cursor-pointer text-left px-4 py-2 text-xs text-red-600 hover:bg-red-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="ban" [size]="14"></lucide-icon> Bloquear
                            </button>
                            <button *ngIf="('accounts.freeze' | hasPermission) && account.status === 'ACTIVE'" (click)="changeStateWithReason(account, 'freeze')" class="w-full cursor-pointer text-left px-4 py-2 text-xs text-yellow-600 hover:bg-yellow-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="snowflake" [size]="14"></lucide-icon> Congelar
                            </button>
                            <button *ngIf="('accounts.close' | hasPermission) && account.status !== 'CLOSED'" (click)="changeStateWithReason(account, 'close')" class="w-full cursor-pointer text-left px-4 py-2 text-xs text-red-600 hover:bg-red-500/10 transition-colors flex items-center gap-2">
                              <lucide-icon name="x-circle" [size]="14"></lucide-icon> Cerrar Cuenta
                            </button>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-[#6B7D6C]">
                      No hay cuentas bancarias registradas.
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
    </div>

    <!-- Formulario Slide-over -->
    <app-account-form
      [isOpen]="isFormOpen"
      [account]="selectedAccount"
      (closed)="isFormOpen = false"
      (saved)="onFormSaved($event)">
    </app-account-form>

    <!-- Modal de Saldo -->
    <div *ngIf="balanceData" class="fixed inset-0 z-[100] flex items-start justify-center p-4 sm:items-center">
      <!-- Overlay blur -->
      <div 
        class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity"
        (click)="closeBalanceModal()">
      </div>
      
      <!-- Contenido del modal -->
      <div class="relative w-full max-w-sm max-h-[calc(100vh-2rem)] overflow-y-auto overscroll-contain rounded-[24px] border border-[#C8E6C9] bg-white p-6 shadow-[0_18px_50px_rgba(27,94,32,0.12)] transform transition-all">
        <!-- Cerrar -->
        <button 
          (click)="closeBalanceModal()"
          class="absolute right-4 top-4 p-2 rounded-full text-muted-foreground hover:bg-muted hover:text-foreground transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>

        <div class="flex flex-col items-center text-center mt-2">
          <div class="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-[#2E7D32]/10 text-[#2E7D32]">
            <lucide-icon name="wallet" [size]="32"></lucide-icon>
          </div>
          <h3 class="text-xl font-black text-[#1B5E20]">Saldo de la cuenta</h3>
          <p class="mt-1 text-sm text-[#6B7D6C]">{{ balanceData.accountNumber }}</p>
          <p class="text-xs text-[#6B7D6C]">{{ balanceData.accountNameLabel || balanceData.customAlias }}</p>
        </div>

        <div class="mt-8 space-y-4">
          <div class="flex flex-col items-center rounded-2xl border border-[#C8E6C9] bg-[#F7FBF3] p-4">
            <span class="text-xs font-semibold uppercase tracking-wider text-[#2E7D32]">Saldo disponible</span>
            <span class="mt-1 text-2xl font-black text-[#1B5E20]">
              {{ balanceData.availableBalance | currency:balanceData.currency }}
            </span>
          </div>

          <div class="flex items-center justify-between rounded-2xl bg-[#FAFCF8] p-4">
            <span class="text-sm font-medium text-[#567157]">Saldo retenido</span>
            <span class="text-base font-bold text-[#1B5E20]">
              {{ balanceData.heldBalance | currency:balanceData.currency }}
            </span>
          </div>
        </div>

        <button 
          (click)="closeBalanceModal()"
          class="mt-6 w-full cursor-pointer rounded-full bg-[#2E7D32] py-2.5 font-semibold text-white transition-colors hover:bg-[#256428]">
          Entendido
        </button>
      </div>
    </div>
  `
})
export class AccountsPageComponent implements OnInit {
  public readonly useCase = inject(AccountListUseCase);
  private readonly toastService = inject(ToastService);
  readonly pageSize = 20;
  readonly currentPage = computed(() => this.useCase.page()?.number ?? 0);
  readonly totalPages = computed(() => this.useCase.page()?.totalPages ?? 0);
  readonly totalElements = computed(() => this.useCase.page()?.totalElements ?? 0);

  readonly accountStats = computed(() => {
    const accounts = this.useCase.data();
    const active = accounts.filter((account) => account.active).length;
    const pending = accounts.filter((account) => ['PENDING_APPROVAL', 'PENDING_VERIFICATION'].includes(account.status)).length;
    const restricted = accounts.filter((account) => ['FROZEN', 'BLOCKED', 'CLOSED', 'SUSPENDED'].includes(account.status)).length;

    return {
      total: accounts.length,
      active,
      pending,
      restricted
    };
  });

  isFormOpen = false;
  selectedAccount: AccountOwnerResponse | null = null;
  balanceData: any = null;

  ngOnInit() {
    this.reload();
  }

  async reload() {
    await this.useCase.loadAccounts(this.currentPage(), this.pageSize);
  }

  onPageChange(page: number): void {
    this.useCase.loadAccounts(page, this.pageSize);
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

  accountStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      ACTIVE: 'Activo',
      PENDING_APPROVAL: 'Pendiente',
      PENDING_VERIFICATION: 'Verificación',
      FROZEN: 'Congelado',
      BLOCKED: 'Bloqueado',
      SUSPENDED: 'Suspendido',
      CLOSED: 'Cerrado'
    };

    return labels[status] ?? status;
  }

  accountStatusClass(status: string): string {
    if (status === 'ACTIVE') {
      return 'bg-green-500/10 text-green-600';
    }
    if (status === 'PENDING_APPROVAL' || status === 'PENDING_VERIFICATION' || status === 'FROZEN') {
      return 'bg-yellow-500/10 text-yellow-600';
    }
    if (status === 'SUSPENDED' || status === 'BLOCKED' || status === 'CLOSED') {
      return 'bg-red-500/10 text-red-600';
    }

    return 'bg-slate-500/10 text-slate-600';
  }
}
