import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { CustomerDashboardChartsComponent } from '../../features/dashboard';
import { CustomerSummaryUseCase } from '../../features/dashboard/application/customer-summary.usecase';

@Component({
  selector: 'app-customer-summary-page',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, CustomerDashboardChartsComponent],
  template: `
    <div class="space-y-6">
      @if (customerSummaryUseCase.status() === 'loading') {
        <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          @for (item of loadingSkeleton; track item) {
            <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
              <div class="h-4 w-24 rounded bg-[#E8F5E9] animate-pulse"></div>
              <div class="mt-4 h-8 w-20 rounded bg-[#E8F5E9] animate-pulse"></div>
              <div class="mt-3 h-3 w-32 rounded bg-[#E8F5E9] animate-pulse"></div>
            </div>
          }
        </div>
      }

      @if (customerSummaryUseCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6 flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="mt-0.5 text-red-700"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-red-700">Error al cargar el dashboard</h3>
            <p class="mt-1 text-sm text-red-700/80">{{ customerSummaryUseCase.error() }}</p>
            <button (click)="retry()" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      @if (customerSummaryUseCase.status() === 'success') {
        @if (customerSummaryUseCase.data(); as dashboard) {
          <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
            <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
              <div class="space-y-3">
                <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
                  <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
                  Cliente
                </div>
                <div>
                  <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
                    Mi dashboard
                  </h1>
                  <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                    Resumen personal con cuentas, movimientos, límites, notificaciones y señales importantes.
                  </p>
                </div>
              </div>

              <div class="grid gap-3 sm:grid-cols-3">
                <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
                  <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Generado el</p>
                  <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ formatDateTimeValue(dashboard.metadata.generatedAt) }}</p>
                </div>
                <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
                  <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Base</p>
                  <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ dashboard.metadata.baseCurrency }}</p>
                </div>
                <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
                  <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Cobertura</p>
                  <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ dashboard.metadata.dataCompleteness }}</p>
                </div>
              </div>
            </div>
          </section>

          <section class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            <div class="min-w-0 rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
              <p class="text-sm font-semibold text-[#567157]">Cuentas</p>
              <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ dashboard.summary.accounts }}</p>
              <p class="mt-2 text-xs text-[#6B7D6C]">Registradas en tu cuenta</p>
            </div>
            <div class="min-w-0 rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
              <p class="text-sm font-semibold text-[#567157]">Saldo total</p>
              <p class="mt-4 text-2xl font-black text-[#1B5E20]">{{ formatMoneyValue(dashboard.summary.totalBalance) }}</p>
              <p class="mt-2 text-xs text-[#6B7D6C]">Suma disponible</p>
            </div>
            <div class="min-w-0 rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
              <p class="text-sm font-semibold text-[#567157]">Ingresos del mes</p>
              <p class="mt-4 text-2xl font-black text-[#1B5E20]">{{ formatMoneyValue(dashboard.summary.monthlyIncome) }}</p>
              <p class="mt-2 text-xs text-[#6B7D6C]">Movimientos positivos</p>
            </div>
            <div class="min-w-0 rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
              <p class="text-sm font-semibold text-[#567157]">Gastos del mes</p>
              <p class="mt-4 text-2xl font-black text-[#1B5E20]">{{ formatMoneyValue(dashboard.summary.monthlyExpenses) }}</p>
              <p class="mt-2 text-xs text-[#6B7D6C]">Movimientos negativos</p>
            </div>
            <div class="min-w-0 rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
              <p class="text-sm font-semibold text-[#567157]">Pendientes</p>
              <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ dashboard.summary.pendingTransactions }}</p>
              <p class="mt-2 text-xs text-[#6B7D6C]">Transacciones por completar</p>
            </div>
            <div class="min-w-0 rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
              <p class="text-sm font-semibold text-[#567157]">Notificaciones</p>
              <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ dashboard.summary.unreadNotifications }}</p>
              <p class="mt-2 text-xs text-[#6B7D6C]">Sin leer</p>
            </div>
          </section>

          <div class="my-8">
            <app-customer-dashboard-charts [data]="dashboard"></app-customer-dashboard-charts>
          </div>

          <section class="grid gap-4 xl:grid-cols-[1.1fr_0.9fr]">
            <div class="min-w-0 rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
              <div class="flex items-center justify-between gap-3">
                <div>
                  <h2 class="text-lg font-bold text-[#1B5E20]">Mis cuentas</h2>
                  <p class="text-sm text-[#6B7D6C]">Cuentas vinculadas a tu perfil</p>
                </div>
                <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.accounts.items.length }}</span>
              </div>
              <div class="mt-4 overflow-x-auto rounded-2xl border border-[#E8F2E2]">
                <table class="min-w-[680px] w-full divide-y divide-[#E8F2E2]">
                  <thead class="bg-[#F7FBF3]">
                    <tr class="text-left text-xs font-bold uppercase tracking-[0.12em] text-[#6B7D6C]">
                      <th class="px-4 py-3">Cuenta</th>
                      <th class="px-4 py-3">Etiqueta</th>
                      <th class="px-4 py-3">Tipo</th>
                      <th class="px-4 py-3">Estado</th>
                      <th class="px-4 py-3 text-right">Saldo</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-[#EEF5EA] bg-white">
                    @for (account of dashboard.accounts.items; track account.id) {
                      <tr>
                        <td class="px-4 py-3">
                          <p class="font-semibold text-[#1B5E20]">{{ account.accountNumber }}</p>
                          <p class="text-xs text-[#6B7D6C]">{{ formatDateTimeValue(account.createdAt) }}</p>
                        </td>
                        <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ account.label }}</td>
                        <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ account.type }}</td>
                        <td class="px-4 py-3">
                          <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold uppercase tracking-[0.1em] text-[#2E7D32]">{{ account.status }}</span>
                        </td>
                        <td class="px-4 py-3 text-right text-sm font-semibold text-[#1B5E20]">{{ formatMoneyValue(account.balance) }}</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>

            <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
              <div class="flex items-center justify-between">
                <div>
                  <h2 class="text-lg font-bold text-[#1B5E20]">Alertas</h2>
                  <p class="text-sm text-[#6B7D6C]">Señales que requieren atención</p>
                </div>
                <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.alerts.length }}</span>
              </div>
              <div class="mt-4 space-y-3">
                @for (alert of dashboard.alerts; track alert.code) {
                  <div class="min-w-0 rounded-2xl border px-4 py-4"
                    [class.border-red-200]="severity(alert.severity) === 'high'"
                    [class.bg-red-50]="severity(alert.severity) === 'high'"
                    [class.border-amber-200]="severity(alert.severity) === 'warn'"
                    [class.bg-amber-50]="severity(alert.severity) === 'warn'"
                    [class.border-sky-200]="severity(alert.severity) === 'info'"
                    [class.bg-sky-50]="severity(alert.severity) === 'info'">
                    <div class="flex items-start justify-between gap-4">
                      <div>
                        <p class="text-sm font-bold text-[#1B5E20]">{{ alert.title }}</p>
                        <p class="mt-1 text-sm text-[#4F5D4F]">{{ alert.message }}</p>
                      </div>
                      <span class="rounded-full px-3 py-1 text-[11px] font-bold uppercase tracking-[0.14em]"
                        [class.bg-red-100]="severity(alert.severity) === 'high'"
                        [class.text-red-700]="severity(alert.severity) === 'high'"
                        [class.bg-amber-100]="severity(alert.severity) === 'warn'"
                        [class.text-amber-800]="severity(alert.severity) === 'warn'"
                        [class.bg-sky-100]="severity(alert.severity) === 'info'"
                        [class.text-sky-700]="severity(alert.severity) === 'info'">
                        {{ alert.count }}
                      </span>
                    </div>
                  </div>
                }
              </div>
            </div>
          </section>

          <section class="grid gap-4 xl:grid-cols-[1.2fr_0.8fr]">
            <div class="min-w-0 rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
              <div class="flex items-center justify-between gap-3">
                <div>
                  <h2 class="text-lg font-bold text-[#1B5E20]">Transacciones</h2>
                  <p class="text-sm text-[#6B7D6C]">Distribución, volumen y últimos movimientos</p>
                </div>
                <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.transactions.recent.total }}</span>
              </div>

              <div class="mt-4 grid gap-4 lg:grid-cols-[1fr_0.95fr]">
                <div class="space-y-4">
                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <div class="flex items-center justify-between gap-3">
                      <p class="text-sm font-semibold text-[#567157]">Por tipo</p>
                      <span class="text-xs font-semibold text-[#6B7D6C]">{{ dashboard.transactions.byType.length }} grupos</span>
                    </div>
                    <div class="mt-3 space-y-3">
                      @for (item of dashboard.transactions.byType; track item.type) {
                        <div class="min-w-0">
                          <div class="flex items-center justify-between gap-3 text-sm">
                            <span class="min-w-0 break-words text-[#1B5E20]">{{ item.type }}</span>
                            <span class="font-semibold text-[#2E7D32]">{{ item.total }}</span>
                          </div>
                          <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                            <div class="h-full rounded-full bg-[#2E7D32]" [style.width.%]="barWidth(item.total, maxTransactionTotal(dashboard.transactions.byType))"></div>
                          </div>
                        </div>
                      }
                    </div>
                  </div>

                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <div class="flex items-center justify-between gap-3">
                      <p class="text-sm font-semibold text-[#567157]">Volumen mensual</p>
                      <span class="text-xs font-semibold text-[#6B7D6C]">{{ dashboard.transactions.monthlyVolume.length }} puntos</span>
                    </div>
                    <div class="mt-3 space-y-3">
                      @for (point of dashboard.transactions.monthlyVolume; track point.date) {
                        <div class="min-w-0">
                          <div class="flex items-center justify-between gap-3 text-sm">
                            <span class="min-w-0 break-words text-xs text-[#6B7D6C]">{{ formatDate(point.date) }}</span>
                            <span class="font-semibold text-[#1B5E20]">{{ formatMoneyValue(point.amount) }}</span>
                          </div>
                          <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                            <div class="h-full rounded-full bg-[#7CB342]" [style.width.%]="barWidth(point.amount.amount, maxTransactionAmount(dashboard.transactions.monthlyVolume))"></div>
                          </div>
                        </div>
                      }
                    </div>
                  </div>
                </div>

                <div class="min-w-0">
                  <p class="text-sm font-semibold text-[#567157]">Últimos movimientos</p>
                  <div class="mt-3 space-y-3">
                    @for (item of dashboard.transactions.recent.items; track item.id) {
                      <div class="min-w-0 rounded-2xl border border-[#E8F2E2] bg-white p-4 shadow-sm">
                        <div class="flex min-w-0 items-start justify-between gap-4">
                          <div class="min-w-0 flex-1">
                            <p class="min-w-0 break-words text-sm font-semibold text-[#1B5E20]">{{ item.reference }}</p>
                            <p class="mt-1 min-w-0 break-words text-xs text-[#6B7D6C]">
                              {{ item.sourceAccountNumber || 'N/A' }} → {{ item.targetAccountNumber || 'N/A' }}
                            </p>
                            @if (item.description) {
                              <p class="mt-2 min-w-0 break-words text-xs text-[#6B7D6C]">{{ item.description }}</p>
                            }
                          </div>
                          <div class="text-right">
                            <p class="text-sm font-bold text-[#1B5E20]">{{ formatMoneyValue(item.amount) }}</p>
                            <p class="mt-1 text-[11px] font-bold uppercase tracking-[0.12em] text-[#2E7D32]">{{ item.status }}</p>
                            <p class="mt-2 text-xs text-[#6B7D6C]">{{ item.type }}</p>
                          </div>
                        </div>
                      </div>
                    }
                  </div>
                </div>
              </div>
            </div>

            <div class="space-y-4">
              <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
                <h2 class="text-lg font-bold text-[#1B5E20]">Límites</h2>
                <div class="mt-4 space-y-4">
                  <div class="rounded-2xl bg-[#FAFCF8] p-4">
                    <div class="flex items-center justify-between gap-3">
                      <p class="text-sm font-semibold text-[#567157]">Transferencias</p>
                      <span class="text-xs font-semibold text-[#6B7D6C]">Diario / Mensual</span>
                    </div>
                    <div class="mt-3 space-y-3">
                      <div class="min-w-0">
                        <div class="flex items-center justify-between gap-3 text-sm">
                          <span class="min-w-0 break-words text-[#1B5E20]">Uso diario</span>
                          <span class="font-semibold text-[#2E7D32]">{{ formatMoneyValue(dashboard.limits.transfer.daily.used) }} / {{ formatMoneyValue(dashboard.limits.transfer.daily.limit) }}</span>
                        </div>
                        <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                          <div class="h-full rounded-full bg-[#2E7D32]" [style.width.%]="usagePercent(dashboard.limits.transfer.daily.used.amount, dashboard.limits.transfer.daily.limit.amount)"></div>
                        </div>
                      </div>
                      <div class="min-w-0">
                        <div class="flex items-center justify-between gap-3 text-sm">
                          <span class="min-w-0 break-words text-[#1B5E20]">Uso mensual</span>
                          <span class="font-semibold text-[#2E7D32]">{{ formatMoneyValue(dashboard.limits.transfer.monthly.used) }} / {{ formatMoneyValue(dashboard.limits.transfer.monthly.limit) }}</span>
                        </div>
                        <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                          <div class="h-full rounded-full bg-[#7CB342]" [style.width.%]="usagePercent(dashboard.limits.transfer.monthly.used.amount, dashboard.limits.transfer.monthly.limit.amount)"></div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="rounded-2xl bg-[#FAFCF8] p-4">
                    <div class="flex items-center justify-between gap-3">
                      <p class="text-sm font-semibold text-[#567157]">Retiros</p>
                      <span class="text-xs font-semibold text-[#6B7D6C]">Diario</span>
                    </div>
                    <div class="mt-3 space-y-3">
                      <div class="min-w-0">
                        <div class="flex items-center justify-between gap-3 text-sm">
                          <span class="min-w-0 break-words text-[#1B5E20]">Uso diario</span>
                          <span class="font-semibold text-[#2E7D32]">{{ formatMoneyValue(dashboard.limits.withdrawal.daily.used) }} / {{ formatMoneyValue(dashboard.limits.withdrawal.daily.limit) }}</span>
                        </div>
                        <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                          <div class="h-full rounded-full bg-[#7CB342]" [style.width.%]="usagePercent(dashboard.limits.withdrawal.daily.used.amount, dashboard.limits.withdrawal.daily.limit.amount)"></div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="rounded-2xl bg-[#FAFCF8] p-4">
                    <p class="text-sm font-semibold text-[#567157]">Reglas activas</p>
                    <div class="mt-3 space-y-2">
                      @for (rule of dashboard.limits.activeRules; track rule.code) {
                        <div class="flex items-start justify-between gap-4 rounded-xl bg-white px-4 py-3">
                          <div class="min-w-0">
                            <p class="min-w-0 break-words text-sm font-semibold text-[#1B5E20]">{{ rule.name }}</p>
                            <p class="mt-1 min-w-0 break-words text-xs text-[#6B7D6C]">{{ rule.limitType }} · {{ rule.scopeType }} · {{ rule.period || 'N/A' }}</p>
                          </div>
                          <span class="rounded-full px-3 py-1 text-[11px] font-bold uppercase tracking-[0.14em]"
                            [class.bg-[#E8F5E9]]="rule.active"
                            [class.text-[#2E7D32]]="rule.active"
                            [class.bg-red-100]="!rule.active"
                            [class.text-red-700]="!rule.active">
                            {{ rule.active ? 'Activo' : 'Inactivo' }}
                          </span>
                        </div>
                      }
                    </div>
                  </div>
                </div>
              </div>

              <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
                <h2 class="text-lg font-bold text-[#1B5E20]">Notificaciones</h2>
                <div class="mt-4 space-y-3">
                  @for (notification of dashboard.notifications.items; track notification.id) {
                    <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                      <div class="flex items-start justify-between gap-4">
                        <div class="min-w-0 flex-1">
                          <p class="min-w-0 break-words text-sm font-semibold text-[#1B5E20]">{{ notification.title }}</p>
                          <p class="mt-1 min-w-0 break-words text-xs text-[#6B7D6C]">{{ notification.type }}</p>
                        </div>
                        <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-[11px] font-bold uppercase tracking-[0.12em] text-[#2E7D32]">
                          {{ notification.status }}
                        </span>
                      </div>
                    </div>
                  }
                </div>
              </div>

              <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
                <h2 class="text-lg font-bold text-[#1B5E20]">Balances por moneda</h2>
                <div class="mt-4 space-y-3">
                  @for (balance of dashboard.balances.byCurrency; track balance.currency) {
                    <div class="rounded-2xl bg-[#FAFCF8] px-4 py-3">
                      <div class="flex items-center justify-between gap-3 text-sm">
                        <span class="min-w-0 break-words text-[#1B5E20]">{{ balance.currency }}</span>
                        <span class="font-semibold text-[#2E7D32]">{{ formatMoneyValue(balance.balance) }}</span>
                      </div>
                      <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                        <div class="h-full rounded-full bg-[#7CB342]" [style.width.%]="usagePercent(balance.balance.amount, dashboard.summary.totalBalance.amount || 1)"></div>
                      </div>
                    </div>
                  }
                </div>
              </div>
            </div>
          </section>
        }
      }
    </div>
  `
})
export class CustomerSummaryPageComponent implements OnInit {
  public readonly customerSummaryUseCase = inject(CustomerSummaryUseCase);
  readonly loadingSkeleton = [1, 2, 3, 4];

  ngOnInit(): void {
    this.customerSummaryUseCase.loadSummary();
  }

  retry(): void {
    this.customerSummaryUseCase.loadSummary();
  }

  maxTransactionTotal(items: Array<{ total: number }>): number {
    const max = items.reduce((current, item) => Math.max(current, item.total), 0);
    return Math.max(max, 1);
  }

  maxTransactionAmount(items: Array<{ amount: { amount: number } }>): number {
    const max = items.reduce((current, item) => Math.max(current, item.amount.amount), 0);
    return Math.max(max, 1);
  }

  usagePercent(used: number, limit: number): number {
    if (!limit || limit <= 0) {
      return 0;
    }

    return Math.max(8, Math.min(100, Math.round((used / limit) * 100)));
  }

  barWidth(value: number, max: number): number {
    if (!max || max <= 0) {
      return 0;
    }

    return Math.max(8, Math.min(100, Math.round((value / max) * 100)));
  }

  severity(value: string | null | undefined): 'info' | 'warn' | 'high' {
    const normalized = (value || '').toUpperCase();
    if (normalized === 'HIGH' || normalized === 'CRITICAL') return 'high';
    if (normalized === 'WARN' || normalized === 'WARNING') return 'warn';
    return 'info';
  }

  formatDateTimeValue(value: string | null | undefined): string {
    if (!value) return 'Sin fecha';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return 'Fecha inválida';
    return new Intl.DateTimeFormat('es-BO', {
      timeZone: 'America/La_Paz',
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(date);
  }

  formatDate(value: string | null | undefined): string {
    if (!value) return 'Sin fecha';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return 'Fecha inválida';
    return new Intl.DateTimeFormat('es-BO', {
      timeZone: 'America/La_Paz',
      dateStyle: 'medium'
    }).format(date);
  }

  formatMoneyValue(value: { amount?: number | null; currency?: string | null } | null | undefined): string {
    const safeAmount = Number.isFinite(value?.amount) ? Number(value?.amount) : 0;
    const safeCurrency = (value?.currency || 'BOB').trim().toUpperCase() || 'BOB';

    try {
      return new Intl.NumberFormat('es-BO', {
        style: 'currency',
        currency: safeCurrency,
        maximumFractionDigits: 2
      }).format(safeAmount);
    } catch {
      return `${safeAmount.toLocaleString('es-BO', { maximumFractionDigits: 2 })} ${safeCurrency}`.trim();
    }
  }

}
