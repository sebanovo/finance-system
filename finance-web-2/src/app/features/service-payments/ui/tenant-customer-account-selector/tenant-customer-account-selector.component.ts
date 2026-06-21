import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, computed, inject, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { AccountListUseCase } from '../../../../features/account-management';
import { UserListUseCase } from '../../../../features/user-management';
import { AccountOwnerResponse } from '../../../../entities/accounts';
import { TenantUserResponse } from '../../../../entities/user';

@Component({
  selector: 'app-tenant-customer-account-selector',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <section class="space-y-4 rounded-[24px] border border-[#C8E6C9] bg-[#FAFCF8] p-4">
      <div>
        <h4 class="text-sm font-bold uppercase tracking-wide text-[#567157]">Buscar cliente</h4>
        <p class="mt-1 text-sm text-[#6B7D6C]">
          Busca por nombre, email, teléfono o documento y selecciona el cliente correcto.
        </p>
      </div>

      <input
        type="text"
        [value]="search()"
        (input)="search.set($any($event.target).value)"
        placeholder="nombre, email, teléfono o documento"
        class="h-11 w-full rounded-2xl border border-[#DDEED8] bg-white px-4 text-sm text-[#1B5E20] outline-none placeholder:text-[#9AA99A] focus:border-[#2E7D32]" />

      @if (userListUseCase.status() === 'loading' || accountListUseCase.status() === 'loading') {
        <p class="text-sm font-semibold text-[#1B5E20]">Buscando clientes y cuentas...</p>
      } @else {
        <div class="space-y-3">
          @if (filteredUsers().length > 0) {
            <div class="space-y-2">
              <p class="text-xs font-bold uppercase tracking-wide text-[#567157]">Resultados</p>
              <button
                *ngFor="let user of filteredUsers()"
                type="button"
                (click)="selectUser(user)"
                class="w-full rounded-[20px] border px-4 py-3 text-left transition-colors"
                [ngClass]="selectedUserId === user.id.toString() ? 'border-[#2E7D32] bg-[#F1F8E9]' : 'border-[#DDEED8] bg-white hover:bg-[#FAFCF8]'">
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <p class="text-sm font-bold text-[#1B5E20]">{{ fullName(user) }}</p>
                    <p class="mt-1 text-xs text-[#6B7D6C]">{{ user.email }}</p>
                  </div>
                  <span class="rounded-full px-3 py-1 text-[11px] font-bold"
                    [ngClass]="user.active ? 'bg-[#E8F5E9] text-[#1B5E20]' : 'bg-slate-100 text-slate-600'">
                    {{ user.active ? 'Activo' : 'Inactivo' }}
                  </span>
                </div>
              </button>
            </div>
          } @else {
            <div class="rounded-[20px] border border-dashed border-[#DDEED8] bg-white p-4 text-center">
              <p class="text-sm font-semibold text-[#1B5E20]">No se encontraron clientes</p>
            </div>
          }
        </div>
      }

      @if (selectedUser()) {
        <div class="rounded-[20px] border border-[#C8E6C9] bg-white p-4 shadow-sm">
          <div class="flex items-center justify-between gap-3">
            <div>
              <p class="text-xs font-bold uppercase tracking-wide text-[#567157]">Cliente seleccionado</p>
              <p class="mt-1 text-sm font-bold text-[#1B5E20]">{{ fullName(selectedUser()!) }}</p>
              <p class="mt-1 text-xs text-[#6B7D6C]">{{ selectedUser()!.email }}</p>
            </div>

            <button
              type="button"
              (click)="clearSelection()"
              class="rounded-full border border-[#DDEED8] px-3 py-2 text-xs font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              Cambiar
            </button>
          </div>

          <div class="mt-4">
            <p class="text-xs font-bold uppercase tracking-wide text-[#567157]">Selecciona la cuenta del cliente</p>
            <div class="mt-3 space-y-2">
              @if (accountsForSelectedUser().length > 0) {
                <button
                  *ngFor="let account of accountsForSelectedUser()"
                  type="button"
                  (click)="selectAccount(account)"
                  class="w-full rounded-[20px] border px-4 py-3 text-left transition-colors"
                  [ngClass]="selectedAccountNumber === account.accountNumber ? 'border-[#2E7D32] bg-[#F1F8E9]' : 'border-[#DDEED8] bg-[#FAFCF8] hover:bg-white'">
                  <div class="flex items-start justify-between gap-3">
                    <div>
                      <p class="text-sm font-bold text-[#1B5E20]">{{ account.displayName || account.accountNameLabel }}</p>
                      <p class="mt-1 text-xs text-[#6B7D6C]">{{ account.accountNumber }}</p>
                    </div>
                    <p class="text-sm font-black text-[#1B5E20]">
                      {{ account.availableBalance | number:'1.2-2' }} {{ account.currency }}
                    </p>
                  </div>
                </button>
              } @else {
                <div class="rounded-[20px] border border-dashed border-[#DDEED8] bg-[#FAFCF8] p-4 text-center">
                  <p class="text-sm font-semibold text-[#1B5E20]">El cliente no tiene cuentas visibles</p>
                </div>
              }
            </div>
          </div>

          @if (selectedAccount()) {
            <div class="mt-4 rounded-[20px] border border-[#DDEED8] bg-[#FAFCF8] p-4">
              <p class="text-xs font-bold uppercase tracking-wide text-[#567157]">Cuenta seleccionada</p>
              <p class="mt-1 text-sm font-bold text-[#1B5E20]">{{ selectedAccount()!.displayName || selectedAccount()!.accountNameLabel }}</p>
              <p class="mt-1 text-xs text-[#6B7D6C]">{{ selectedAccount()!.accountNumber }}</p>
            </div>
          }
        </div>
      }
    </section>
  `
})
export class TenantCustomerAccountSelectorComponent implements OnInit {
  @Input() selectedUserId: string | null = null;
  @Input() selectedAccountNumber: string | null = null;

  @Output() userSelected = new EventEmitter<TenantUserResponse | null>();
  @Output() accountSelected = new EventEmitter<AccountOwnerResponse | null>();

  readonly search = signal('');
  readonly userListUseCase = inject(UserListUseCase);
  readonly accountListUseCase = inject(AccountListUseCase);

  ngOnInit(): void {
    void this.userListUseCase.loadUsers(0, 100);
    void this.accountListUseCase.loadAccounts(0, 200);
  }

  filteredUsers(): TenantUserResponse[] {
    const q = this.search().trim().toLowerCase();
    const users = this.userListUseCase.data();

    if (!q) {
      return users;
    }

    return users.filter((user) => {
      const fullName = this.fullName(user).toLowerCase();
      return fullName.includes(q) || user.email.toLowerCase().includes(q);
    });
  }

  selectedUser(): TenantUserResponse | null {
    return this.userListUseCase.data().find((user) => user.id.toString() === this.selectedUserId) ?? null;
  }

  selectedAccount(): AccountOwnerResponse | null {
    return this.accountListUseCase.data().find((account) => account.accountNumber === this.selectedAccountNumber) ?? null;
  }

  accountsForSelectedUser(): AccountOwnerResponse[] {
    if (!this.selectedUserId) {
      return [];
    }

    return this.accountListUseCase.data().filter((account) => account.userId === this.selectedUserId);
  }

  selectUser(user: TenantUserResponse): void {
    this.userSelected.emit(user);
    this.accountSelected.emit(null);
  }

  selectAccount(account: AccountOwnerResponse): void {
    this.accountSelected.emit(account);
  }

  clearSelection(): void {
    this.userSelected.emit(null);
    this.accountSelected.emit(null);
  }

  fullName(user: TenantUserResponse): string {
    return `${user.firstName} ${user.lastName}`.trim();
  }
}
