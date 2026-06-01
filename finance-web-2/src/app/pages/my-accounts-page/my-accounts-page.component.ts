import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { MyAccountFormComponent, MyAccountListUseCase } from '../../features/account-management';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { AccountOwnerResponse } from '../../entities/accounts';

@Component({
  selector: 'app-my-accounts-page',
  standalone: true,
  imports: [CommonModule, MyAccountFormComponent, PlatformPaginationComponent, LucideAngularModule, HasPermissionPipe],
  providers: [CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="space-y-3">
            <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
              <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
              Usuario
            </div>
            <div>
              <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
                Mis cuentas
              </h1>
              <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                Consulta tus cuentas, revisa saldos, administra alias y crea nuevas cuentas si tu perfil lo permite.
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
              *ngIf="'me.accounts.create' | hasPermission"
              type="button"
              (click)="openCreateForm()"
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
          <p class="text-sm font-semibold text-[#567157]">Alias personalizados</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ accountStats().aliased }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Cuentas con nombre editable</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Saldo total</p>
          <p class="mt-4 text-2xl font-black text-[#1B5E20]">{{ formatMoney(totalBalance(), baseCurrency()) }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Sumatoria visible en la página</p>
        </div>
      </section>

      @if (useCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6 flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-red-700 mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-red-700">Error al cargar tus cuentas</h3>
            <p class="mt-1 text-sm text-red-700/80">{{ useCase.error() }}</p>
            <button (click)="reload()" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center gap-4 p-12 text-[#6B7D6C]">
          <svg class="h-8 w-8 animate-spin text-[#2E7D32]" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando cuentas...</p>
        </div>
      }

      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
          <div class="overflow-x-auto min-h-[300px]">
            <table class="w-full text-left text-sm">
              <thead class="border-b border-[#E8F2E2] bg-[#F7FBF3] text-xs uppercase tracking-[0.12em] text-[#6B7D6C]">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Cuenta</th>
                  <th scope="col" class="px-6 py-4 font-medium">Alias</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo</th>
                  <th scope="col" class="px-6 py-4 font-medium">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Saldo</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center w-16">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-[#EEF5EA] bg-white">
                @for (account of useCase.data(); track account.id) {
                  <tr class="transition-colors hover:bg-[#F7FBF3]">
                    <td class="px-6 py-4">
                      <div class="font-semibold text-[#1B5E20]">{{ account.accountNumber }}</div>
                      <div class="text-xs text-[#6B7D6C]">{{ formatDateTime(account.createdAt) }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="font-semibold text-[#1B5E20]">{{ account.customAlias || account.accountNameLabel }}</div>
                      <div class="text-xs text-[#6B7D6C]">{{ account.accountName }}</div>
                    </td>
                    <td class="px-6 py-4 text-[#4F5D4F]">{{ account.accountType }} - {{ account.currency }}</td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full" [ngClass]="accountStatusClass(account.status)">
                        {{ accountStatusLabel(account.status) }}
                      </span>
                    </td>
                    <td class="px-6 py-4 font-semibold text-right text-[#1B5E20]">{{ formatMoney(account.availableBalance, account.currency) }}</td>
                    <td class="px-6 py-4 text-center">
                      <div class="relative group inline-block text-left">
                        <button type="button" class="cursor-pointer p-2 rounded-md hover:bg-[#F1F8E9] text-[#6B7D6C] transition-colors focus:outline-none">
                          <lucide-icon name="more-horizontal" [size]="16"></lucide-icon>
                        </button>
                        <div class="absolute right-0 mt-1 w-48 bg-white border border-[#DDEED8] rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-50 overflow-hidden">
                          <div class="py-1">
                            <button *ngIf="'me.accounts.balance.read' | hasPermission" (click)="viewBalance(account)" class="w-full cursor-pointer text-left px-4 py-2 text-xs text-[#1B5E20] hover:bg-[#F1F8E9] transition-colors flex items-center gap-2">
                              <lucide-icon name="wallet" [size]="14"></lucide-icon> Ver saldo
                            </button>
                            <button *ngIf="'me.accounts.update.alias' | hasPermission" (click)="openAliasForm(account)" class="w-full cursor-pointer text-left px-4 py-2 text-xs text-[#1B5E20] hover:bg-[#F1F8E9] transition-colors flex items-center gap-2">
                              <lucide-icon name="pencil" [size]="14"></lucide-icon> Editar alias
                            </button>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-[#6B7D6C]">
                      No hay cuentas registradas.
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

    <app-my-account-form
      [isOpen]="isFormOpen()"
      [account]="selectedAccount()"
      (closed)="closeForm()"
      (saved)="onFormSaved($event)">
    </app-my-account-form>

    <div *ngIf="balanceData()" class="fixed inset-0 z-[100] flex items-start justify-center p-4 sm:items-center">
      <div class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity" (click)="closeBalanceModal()"></div>

      <div class="relative w-full max-w-sm max-h-[calc(100vh-2rem)] overflow-y-auto overscroll-contain rounded-[24px] border border-[#C8E6C9] bg-white p-6 shadow-[0_18px_50px_rgba(27,94,32,0.12)] transform transition-all">
        <button
          (click)="closeBalanceModal()"
          class="absolute right-4 top-4 p-2 rounded-full text-[#6B7D6C] hover:bg-[#F1F8E9] hover:text-[#1B5E20] transition-colors">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>

        <div class="flex flex-col items-center text-center mt-2">
          <div class="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-[#2E7D32]/10 text-[#2E7D32]">
            <lucide-icon name="wallet" [size]="32"></lucide-icon>
          </div>
          <h3 class="text-xl font-black text-[#1B5E20]">Saldo de la cuenta</h3>
          <p class="mt-1 text-sm text-[#6B7D6C]">{{ balanceData().accountNumber }}</p>
          <p class="text-xs text-[#6B7D6C]">{{ balanceData().accountNameLabel || balanceData().customAlias }}</p>
        </div>

        <div class="mt-8 space-y-4">
          <div class="flex flex-col items-center rounded-2xl border border-[#C8E6C9] bg-[#F7FBF3] p-4">
            <span class="text-xs font-semibold uppercase tracking-wider text-[#2E7D32]">Saldo disponible</span>
            <span class="mt-1 text-2xl font-black text-[#1B5E20]">
              {{ formatMoney(balanceData().availableBalance, balanceData().currency) }}
            </span>
          </div>

          <div class="flex items-center justify-between rounded-2xl bg-[#FAFCF8] p-4">
            <span class="text-sm font-medium text-[#567157]">Saldo retenido</span>
            <span class="text-base font-bold text-[#1B5E20]">
              {{ formatMoney(balanceData().heldBalance, balanceData().currency) }}
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
export class MyAccountsPageComponent implements OnInit {
  public readonly useCase = inject(MyAccountListUseCase);
  private readonly toastService = inject(ToastService);

  readonly pageSize = 20;
  readonly isFormOpen = signal(false);
  readonly selectedAccount = signal<AccountOwnerResponse | null>(null);
  readonly balanceData = signal<any>(null);

  readonly currentPage = computed(() => this.useCase.page()?.number ?? 0);
  readonly totalPages = computed(() => this.useCase.page()?.totalPages ?? 0);
  readonly totalElements = computed(() => this.useCase.page()?.totalElements ?? 0);

  readonly accountStats = computed(() => {
    const accounts = this.useCase.data();
    const active = accounts.filter((account) => account.active).length;
    const aliased = accounts.filter((account) => !!account.customAlias?.trim()).length;

    return { total: accounts.length, active, aliased };
  });

  ngOnInit(): void {
    this.reload();
  }

  async reload(): Promise<void> {
    await this.useCase.loadAccounts(this.currentPage(), this.pageSize);
  }

  onPageChange(page: number): void {
    this.useCase.loadAccounts(page, this.pageSize);
  }

  openCreateForm(): void {
    this.selectedAccount.set(null);
    this.isFormOpen.set(true);
  }

  openAliasForm(account: AccountOwnerResponse): void {
    this.selectedAccount.set(account);
    this.isFormOpen.set(true);
  }

  closeForm(): void {
    this.isFormOpen.set(false);
    this.selectedAccount.set(null);
  }

  async onFormSaved(event: { request: { accountName?: string; customAlias?: string; accountType?: string; currency?: string }; isEditing: boolean }): Promise<void> {
    try {
      if (event.isEditing && this.selectedAccount()) {
        await this.useCase.updateAlias(this.selectedAccount()!.id, { customAlias: event.request.customAlias || '' });
      } else {
        await this.useCase.createAccount({
          accountName: event.request.accountName || 'MAIN_WALLET',
          customAlias: event.request.customAlias,
          accountType: event.request.accountType || 'SAVINGS',
          currency: event.request.currency || 'USD'
        });
      }

      this.toastService.success(event.isEditing ? 'Alias actualizado correctamente' : 'Cuenta creada correctamente');
      this.closeForm();
    } catch (error) {
      this.toastService.error((error as Error).message || 'No se pudo guardar la cuenta');
    }
  }

  async viewBalance(account: AccountOwnerResponse): Promise<void> {
    try {
      const response = await this.useCase.getAccountBalance(account.id);
      if (response && response.data) {
        this.balanceData.set(response.data);
      }
    } catch (error) {
      this.toastService.error((error as Error).message || 'Error al consultar saldo');
    }
  }

  closeBalanceModal(): void {
    this.balanceData.set(null);
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

  formatMoney(amount: number, currency: string): string {
    return new Intl.NumberFormat('es-BO', {
      style: 'currency',
      currency,
      maximumFractionDigits: 2
    }).format(amount || 0);
  }

  formatDateTime(value: string | null | undefined): string {
    if (!value) return 'Sin fecha';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return 'Fecha inválida';
    return new Intl.DateTimeFormat('es-BO', {
      timeZone: 'America/La_Paz',
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(date);
  }

  baseCurrency(): string {
    return this.useCase.data()[0]?.currency || 'BOB';
  }

  totalBalance(): number {
    return this.useCase.data().reduce((sum, account) => sum + Number(account.availableBalance || 0), 0);
  }
}
