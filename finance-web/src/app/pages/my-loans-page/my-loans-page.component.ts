import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { LoansService } from '../../entities/loans/api/loans.service';
import { LoanInstallmentResponse, LoanResponse } from '../../entities/loans/model/loans.model';
import { AccountsService } from '../../entities/accounts/api/accounts.service';
import { AccountOwnerResponse } from '../../entities/accounts/model/accounts.model';

@Component({
  standalone: true,
  selector: 'app-my-loans-page',
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="px-4 py-6 md:px-8">
      <div class="mb-6 flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-black tracking-tight text-[#1B5E20]">Mis préstamos</h1>
          <p class="mt-1 text-sm text-[#5F6F5F]">Solicita un préstamo, consulta tus cuotas y registra pagos.</p>
        </div>
        <button (click)="showForm.set(!showForm())"
          class="rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white hover:bg-[#256428]">
          {{ showForm() ? 'Cerrar' : 'Solicitar préstamo' }}
        </button>
      </div>

      <div *ngIf="message()" class="mb-4 rounded-2xl border border-[#C8E6C9] bg-[#F1F8E9] p-3 text-sm text-[#1B5E20]">{{ message() }}</div>
      <div *ngIf="error()" class="mb-4 rounded-2xl border border-red-200 bg-red-50 p-3 text-sm text-red-700">{{ error() }}</div>

      <!-- Request form -->
      <form *ngIf="showForm()" [formGroup]="form" (ngSubmit)="submitRequest()"
        class="mb-6 grid grid-cols-1 gap-4 rounded-2xl border border-[#DDEED8] bg-white p-5 md:grid-cols-2">
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Cuenta de desembolso</label>
          <select formControlName="accountId" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm">
            <option value="">Selecciona una cuenta</option>
            <option *ngFor="let a of accounts()" [value]="a.id">{{ a.accountNumber }} · {{ a.currency }} · {{ a.availableBalance | number:'1.2-2' }}</option>
          </select>
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Monto</label>
          <input type="number" formControlName="principal" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm" placeholder="1000.00">
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Tasa anual (%)</label>
          <input type="number" formControlName="annualInterestRate" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm" placeholder="12.00">
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Plazo (meses)</label>
          <input type="number" formControlName="termMonths" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm" placeholder="6">
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Método de interés</label>
          <select formControlName="interestMethod" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm">
            <option value="FLAT">Fijo (FLAT)</option>
            <option value="FRENCH">Francés (cuota fija)</option>
          </select>
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Propósito (opcional)</label>
          <input type="text" formControlName="purpose" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm" placeholder="Capital de trabajo">
        </div>
        <div class="md:col-span-2">
          <button type="submit" [disabled]="form.invalid || busy()"
            class="rounded-full bg-[#2E7D32] px-5 py-2 text-sm font-semibold text-white hover:bg-[#256428] disabled:opacity-50">
            Enviar solicitud
          </button>
        </div>
      </form>

      <!-- Loans list -->
      <div *ngIf="loans().length === 0 && !busy()" class="rounded-2xl border border-[#DDEED8] bg-white p-8 text-center text-sm text-[#5F6F5F]">
        Aún no tienes préstamos.
      </div>

      <div class="space-y-4">
        <div *ngFor="let loan of loans()" class="rounded-2xl border border-[#DDEED8] bg-white p-5">
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              <span class="rounded-full px-2.5 py-1 text-xs font-bold" [ngClass]="statusClass(loan.status)">{{ statusLabel(loan.status) }}</span>
              <span class="ml-2 text-sm text-[#5F6F5F]">{{ loan.purpose || 'Préstamo' }}</span>
            </div>
            <div class="text-right">
              <p class="text-lg font-black text-[#1B5E20]">{{ loan.principal | number:'1.2-2' }} {{ loan.currency }}</p>
              <p class="text-xs text-[#5F6F5F]">{{ loan.annualInterestRate }}% · {{ loan.termMonths }} meses · {{ loan.interestMethod }}</p>
            </div>
          </div>

          <div class="mt-3 grid grid-cols-3 gap-2 text-center text-sm">
            <div class="rounded-xl bg-[#FAFCF8] p-2"><p class="text-xs text-[#5F6F5F]">Total</p><p class="font-bold text-[#1B5E20]">{{ loan.totalDue | number:'1.2-2' }}</p></div>
            <div class="rounded-xl bg-[#FAFCF8] p-2"><p class="text-xs text-[#5F6F5F]">Pagado</p><p class="font-bold text-[#1B5E20]">{{ loan.totalPaid | number:'1.2-2' }}</p></div>
            <div class="rounded-xl bg-[#FAFCF8] p-2"><p class="text-xs text-[#5F6F5F]">Saldo</p><p class="font-bold text-[#1B5E20]">{{ loan.outstandingBalance | number:'1.2-2' }}</p></div>
          </div>

          <div class="mt-3 flex flex-wrap items-center gap-2">
            <button (click)="toggleSchedule(loan.id)" class="rounded-full border border-[#2E7D32] px-3 py-1.5 text-xs font-semibold text-[#2E7D32] hover:bg-[#F1F8E9]">
              {{ expanded() === loan.id ? 'Ocultar cuotas' : 'Ver cuotas' }}
            </button>
            <ng-container *ngIf="loan.status === 'DISBURSED'">
              <input type="number" [value]="loan.outstandingBalance" #payAmt
                class="w-28 rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-2 py-1.5 text-xs">
              <button (click)="pay(loan, payAmt.value)" [disabled]="busy()"
                class="rounded-full bg-[#2E7D32] px-3 py-1.5 text-xs font-semibold text-white hover:bg-[#256428] disabled:opacity-50">Pagar</button>
            </ng-container>
          </div>

          <div *ngIf="expanded() === loan.id" class="mt-3 overflow-x-auto">
            <table class="w-full text-left text-xs">
              <thead class="text-[#5F6F5F]"><tr>
                <th class="py-1">#</th><th>Vence</th><th>Capital</th><th>Interés</th><th>Total</th><th>Pagado</th><th>Estado</th>
              </tr></thead>
              <tbody>
                <tr *ngFor="let i of schedule()" class="border-t border-[#EEF5EC]">
                  <td class="py-1">{{ i.number }}</td><td>{{ i.dueDate }}</td>
                  <td>{{ i.principalDue | number:'1.2-2' }}</td><td>{{ i.interestDue | number:'1.2-2' }}</td>
                  <td>{{ i.totalDue | number:'1.2-2' }}</td><td>{{ i.paidAmount | number:'1.2-2' }}</td>
                  <td><span class="rounded px-1.5 py-0.5 font-semibold" [ngClass]="instClass(i.status)">{{ i.status }}</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `
})
export class MyLoansPageComponent implements OnInit {
  private readonly loansService = inject(LoansService);
  private readonly accountsService = inject(AccountsService);
  private readonly fb = inject(FormBuilder);

  readonly loans = signal<LoanResponse[]>([]);
  readonly accounts = signal<AccountOwnerResponse[]>([]);
  readonly schedule = signal<LoanInstallmentResponse[]>([]);
  readonly expanded = signal<string | null>(null);
  readonly showForm = signal(false);
  readonly busy = signal(false);
  readonly message = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.group({
    accountId: ['', Validators.required],
    principal: [null as number | null, [Validators.required, Validators.min(0.01)]],
    annualInterestRate: [12, [Validators.required, Validators.min(0)]],
    termMonths: [6, [Validators.required, Validators.min(1)]],
    interestMethod: ['FLAT', Validators.required],
    purpose: ['']
  });

  async ngOnInit(): Promise<void> {
    await Promise.all([this.loadLoans(), this.loadAccounts()]);
  }

  private async loadLoans(): Promise<void> {
    this.busy.set(true);
    try {
      const res = await firstValueFrom(this.loansService.listMyLoans(0, 100));
      this.loans.set(res.data?.content ?? []);
    } catch (e: any) {
      this.error.set(e?.error?.message ?? 'No se pudieron cargar los préstamos.');
    } finally {
      this.busy.set(false);
    }
  }

  private async loadAccounts(): Promise<void> {
    try {
      const res = await firstValueFrom(this.accountsService.listMyAccounts(0, 100));
      this.accounts.set((res.data?.content ?? []).filter(a => a.availableBalance >= 0));
    } catch {
      // accounts optional for viewing
    }
  }

  async submitRequest(): Promise<void> {
    if (this.form.invalid || this.busy()) return;
    this.busy.set(true);
    this.message.set(null);
    this.error.set(null);
    try {
      const v = this.form.value;
      await firstValueFrom(this.loansService.requestMyLoan({
        accountId: String(v.accountId),
        principal: Number(v.principal),
        annualInterestRate: Number(v.annualInterestRate),
        termMonths: Number(v.termMonths),
        interestMethod: v.interestMethod as any,
        purpose: v.purpose || null
      }));
      this.message.set('Solicitud enviada. Un administrador la revisará.');
      this.showForm.set(false);
      this.form.reset({ annualInterestRate: 12, termMonths: 6, interestMethod: 'FLAT', accountId: '', principal: null, purpose: '' });
      await this.loadLoans();
    } catch (e: any) {
      this.error.set(e?.error?.message ?? 'No se pudo enviar la solicitud.');
    } finally {
      this.busy.set(false);
    }
  }

  async toggleSchedule(loanId: string): Promise<void> {
    if (this.expanded() === loanId) { this.expanded.set(null); return; }
    try {
      const res = await firstValueFrom(this.loansService.getMySchedule(loanId));
      this.schedule.set(res.data ?? []);
      this.expanded.set(loanId);
    } catch (e: any) {
      this.error.set(e?.error?.message ?? 'No se pudo cargar el cronograma.');
    }
  }

  async pay(loan: LoanResponse, rawAmount: string): Promise<void> {
    const amount = Number(rawAmount);
    if (!amount || amount <= 0 || this.busy()) return;
    this.busy.set(true);
    this.message.set(null);
    this.error.set(null);
    try {
      await firstValueFrom(this.loansService.payMyLoan(loan.id, { amount }));
      this.message.set('Pago registrado.');
      if (this.expanded() === loan.id) { await this.toggleSchedule(loan.id); this.expanded.set(loan.id); }
      await this.loadLoans();
    } catch (e: any) {
      this.error.set(e?.error?.message ?? 'No se pudo registrar el pago.');
    } finally {
      this.busy.set(false);
    }
  }

  statusLabel(s: string): string {
    return { REQUESTED: 'Solicitado', APPROVED: 'Aprobado', REJECTED: 'Rechazado', DISBURSED: 'Desembolsado', PAID_OFF: 'Pagado', CANCELLED: 'Cancelado' }[s] ?? s;
  }
  statusClass(s: string): string {
    if (s === 'DISBURSED') return 'bg-[#E8F5E9] text-[#2E7D32]';
    if (s === 'PAID_OFF') return 'bg-blue-100 text-blue-700';
    if (s === 'REJECTED' || s === 'CANCELLED') return 'bg-red-100 text-red-700';
    return 'bg-amber-100 text-amber-700';
  }
  instClass(s: string): string {
    if (s === 'PAID') return 'bg-[#E8F5E9] text-[#2E7D32]';
    if (s === 'PARTIAL') return 'bg-amber-100 text-amber-700';
    if (s === 'OVERDUE') return 'bg-red-100 text-red-700';
    return 'bg-gray-100 text-gray-600';
  }
}
