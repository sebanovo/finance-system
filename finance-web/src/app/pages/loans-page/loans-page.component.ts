import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { LoansService } from '../../entities/loans/api/loans.service';
import { LoanInstallmentResponse, LoanResponse } from '../../entities/loans/model/loans.model';

@Component({
  standalone: true,
  selector: 'app-loans-page',
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="px-4 py-6 md:px-8">
      <div class="mb-6 flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-black tracking-tight text-[#1B5E20]">Préstamos</h1>
          <p class="mt-1 text-sm text-[#5F6F5F]">Gestiona solicitudes, desembolsos y pagos de préstamos del tenant.</p>
        </div>
        <button (click)="showForm.set(!showForm())"
          class="rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white hover:bg-[#256428]">
          {{ showForm() ? 'Cerrar' : 'Nuevo préstamo' }}
        </button>
      </div>

      <div *ngIf="message()" class="mb-4 rounded-2xl border border-[#C8E6C9] bg-[#F1F8E9] p-3 text-sm text-[#1B5E20]">{{ message() }}</div>
      <div *ngIf="error()" class="mb-4 rounded-2xl border border-red-200 bg-red-50 p-3 text-sm text-red-700">{{ error() }}</div>

      <form *ngIf="showForm()" [formGroup]="form" (ngSubmit)="create()"
        class="mb-6 grid grid-cols-1 gap-4 rounded-2xl border border-[#DDEED8] bg-white p-5 md:grid-cols-2">
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">ID de usuario (prestatario)</label>
          <input formControlName="userId" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm" placeholder="UUID del usuario">
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">ID de cuenta de desembolso</label>
          <input formControlName="accountId" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm" placeholder="UUID de la cuenta">
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Monto</label>
          <input type="number" formControlName="principal" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm">
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Tasa anual (%)</label>
          <input type="number" formControlName="annualInterestRate" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm">
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Plazo (meses)</label>
          <input type="number" formControlName="termMonths" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm">
        </div>
        <div>
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Método</label>
          <select formControlName="interestMethod" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm">
            <option value="FLAT">Fijo (FLAT)</option>
            <option value="FRENCH">Francés (cuota fija)</option>
          </select>
        </div>
        <div class="md:col-span-2">
          <label class="mb-1 block text-xs font-semibold text-[#567157]">Propósito (opcional)</label>
          <input formControlName="purpose" class="w-full rounded-xl border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-sm">
        </div>
        <div class="md:col-span-2">
          <button type="submit" [disabled]="form.invalid || busy()"
            class="rounded-full bg-[#2E7D32] px-5 py-2 text-sm font-semibold text-white hover:bg-[#256428] disabled:opacity-50">Crear solicitud</button>
        </div>
      </form>

      <div class="overflow-x-auto rounded-2xl border border-[#DDEED8] bg-white">
        <table class="w-full text-left text-sm">
          <thead class="bg-[#FAFCF8] text-xs uppercase text-[#5F6F5F]"><tr>
            <th class="px-4 py-3">Estado</th><th class="px-4 py-3">Monto</th><th class="px-4 py-3">Tasa/Plazo</th>
            <th class="px-4 py-3">Saldo</th><th class="px-4 py-3">Acciones</th>
          </tr></thead>
          <tbody>
            <tr *ngFor="let loan of loans()" class="border-t border-[#EEF5EC]">
              <td class="px-4 py-3"><span class="rounded-full px-2.5 py-1 text-xs font-bold" [ngClass]="statusClass(loan.status)">{{ statusLabel(loan.status) }}</span></td>
              <td class="px-4 py-3 font-semibold text-[#1B5E20]">{{ loan.principal | number:'1.2-2' }} {{ loan.currency }}</td>
              <td class="px-4 py-3 text-[#5F6F5F]">{{ loan.annualInterestRate }}% · {{ loan.termMonths }}m · {{ loan.interestMethod }}</td>
              <td class="px-4 py-3 text-[#5F6F5F]">{{ loan.outstandingBalance | number:'1.2-2' }}</td>
              <td class="px-4 py-3">
                <div class="flex flex-wrap items-center gap-1.5">
                  <button (click)="toggleSchedule(loan.id)" class="rounded-full border border-[#2E7D32] px-2.5 py-1 text-xs font-semibold text-[#2E7D32] hover:bg-[#F1F8E9]">Cuotas</button>
                  <ng-container *ngIf="loan.status === 'REQUESTED'">
                    <button (click)="approve(loan)" class="rounded-full bg-[#2E7D32] px-2.5 py-1 text-xs font-semibold text-white">Aprobar</button>
                    <button (click)="reject(loan)" class="rounded-full bg-red-600 px-2.5 py-1 text-xs font-semibold text-white">Rechazar</button>
                  </ng-container>
                  <button *ngIf="loan.status === 'APPROVED'" (click)="disburse(loan)" class="rounded-full bg-[#2E7D32] px-2.5 py-1 text-xs font-semibold text-white">Desembolsar</button>
                  <ng-container *ngIf="loan.status === 'DISBURSED'">
                    <input type="number" [value]="loan.outstandingBalance" #amt class="w-24 rounded-lg border border-[#DDEED8] bg-[#FAFCF8] px-2 py-1 text-xs">
                    <button (click)="pay(loan, amt.value)" class="rounded-full bg-[#2E7D32] px-2.5 py-1 text-xs font-semibold text-white">Pagar</button>
                  </ng-container>
                </div>
                <div *ngIf="expanded() === loan.id" class="mt-2 overflow-x-auto">
                  <table class="w-full text-left text-xs">
                    <thead class="text-[#5F6F5F]"><tr><th class="py-1">#</th><th>Vence</th><th>Capital</th><th>Interés</th><th>Total</th><th>Pagado</th><th>Estado</th></tr></thead>
                    <tbody>
                      <tr *ngFor="let i of schedule()" class="border-t border-[#EEF5EC]">
                        <td class="py-1">{{ i.number }}</td><td>{{ i.dueDate }}</td><td>{{ i.principalDue | number:'1.2-2' }}</td>
                        <td>{{ i.interestDue | number:'1.2-2' }}</td><td>{{ i.totalDue | number:'1.2-2' }}</td><td>{{ i.paidAmount | number:'1.2-2' }}</td>
                        <td>{{ i.status }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </td>
            </tr>
            <tr *ngIf="loans().length === 0"><td colspan="5" class="px-4 py-8 text-center text-sm text-[#5F6F5F]">No hay préstamos.</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class LoansPageComponent implements OnInit {
  private readonly loansService = inject(LoansService);
  private readonly fb = inject(FormBuilder);

  readonly loans = signal<LoanResponse[]>([]);
  readonly schedule = signal<LoanInstallmentResponse[]>([]);
  readonly expanded = signal<string | null>(null);
  readonly showForm = signal(false);
  readonly busy = signal(false);
  readonly message = signal<string | null>(null);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.group({
    userId: ['', Validators.required],
    accountId: ['', Validators.required],
    principal: [null as number | null, [Validators.required, Validators.min(0.01)]],
    annualInterestRate: [12, [Validators.required, Validators.min(0)]],
    termMonths: [6, [Validators.required, Validators.min(1)]],
    interestMethod: ['FLAT', Validators.required],
    purpose: ['']
  });

  async ngOnInit(): Promise<void> {
    await this.load();
  }

  private async load(): Promise<void> {
    this.busy.set(true);
    try {
      const res = await firstValueFrom(this.loansService.listLoans(0, 100));
      this.loans.set(res.data?.content ?? []);
    } catch (e: any) {
      this.error.set(e?.error?.message ?? 'No se pudieron cargar los préstamos.');
    } finally {
      this.busy.set(false);
    }
  }

  private flash(msg: string): void { this.message.set(msg); this.error.set(null); }

  async create(): Promise<void> {
    if (this.form.invalid || this.busy()) return;
    this.busy.set(true);
    try {
      const v = this.form.value;
      await firstValueFrom(this.loansService.requestLoan({
        userId: String(v.userId), accountId: String(v.accountId),
        principal: Number(v.principal), annualInterestRate: Number(v.annualInterestRate),
        termMonths: Number(v.termMonths), interestMethod: v.interestMethod as any, purpose: v.purpose || null
      }));
      this.flash('Préstamo creado (REQUESTED).');
      this.showForm.set(false);
      this.form.reset({ userId: '', accountId: '', principal: null, annualInterestRate: 12, termMonths: 6, interestMethod: 'FLAT', purpose: '' });
      await this.load();
    } catch (e: any) { this.error.set(e?.error?.message ?? 'No se pudo crear.'); }
    finally { this.busy.set(false); }
  }

  async approve(loan: LoanResponse): Promise<void> { await this.run(() => firstValueFrom(this.loansService.approveLoan(loan.id)), 'Aprobado.'); }
  async reject(loan: LoanResponse): Promise<void> {
    const reason = window.prompt('Motivo del rechazo (opcional):') ?? undefined;
    await this.run(() => firstValueFrom(this.loansService.rejectLoan(loan.id, reason)), 'Rechazado.');
  }
  async disburse(loan: LoanResponse): Promise<void> { await this.run(() => firstValueFrom(this.loansService.disburseLoan(loan.id)), 'Desembolsado.'); }
  async pay(loan: LoanResponse, raw: string): Promise<void> {
    const amount = Number(raw);
    if (!amount || amount <= 0) return;
    await this.run(() => firstValueFrom(this.loansService.payLoan(loan.id, { amount })), 'Pago registrado.');
  }

  private async run(action: () => Promise<unknown>, okMsg: string): Promise<void> {
    if (this.busy()) return;
    this.busy.set(true);
    try { await action(); this.flash(okMsg); await this.load(); }
    catch (e: any) { this.error.set(e?.error?.message ?? 'Operación fallida.'); }
    finally { this.busy.set(false); }
  }

  async toggleSchedule(loanId: string): Promise<void> {
    if (this.expanded() === loanId) { this.expanded.set(null); return; }
    try {
      const res = await firstValueFrom(this.loansService.getSchedule(loanId));
      this.schedule.set(res.data ?? []);
      this.expanded.set(loanId);
    } catch (e: any) { this.error.set(e?.error?.message ?? 'No se pudo cargar el cronograma.'); }
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
}
