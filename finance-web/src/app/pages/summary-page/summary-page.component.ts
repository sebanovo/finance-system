import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { SummaryCardsComponent, SummaryUseCase } from '../../features/dashboard';
import { TenantDashboardActivityItem } from '../../entities/dashboard';

@Component({
  selector: 'app-summary-page',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, SummaryCardsComponent],
  template: `
    <div class="space-y-6">
      @if (summaryUseCase.status() === 'loading') {
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

      @if (summaryUseCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6 flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="mt-0.5 text-red-700"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-red-700">Error al cargar el dashboard</h3>
            <p class="mt-1 text-sm text-red-700/80">{{ summaryUseCase.error() }}</p>
            <button (click)="retry()" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      @if (summaryUseCase.status() === 'success') {
        @if (summaryUseCase.data(); as dashboard) {
          <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
            <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
              <div class="space-y-3">
                <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
                  <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
                  Tenant owner
                </div>
                <div>
                  <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
                    Dashboard de la organización
                  </h1>
                  <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                    Vista ejecutiva del tenant con cuentas, transacciones, límites, contabilidad, FX, alertas y actividad reciente.
                  </p>
                </div>
              </div>

              <div class="grid gap-3 sm:grid-cols-3">
                <div class="rounded-2xl border border-[#C8E6C9] bg-white/85 px-4 py-3">
                  <p class="text-[11px] font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Generado el</p>
                  <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ formatDateTime(dashboard.metadata.generatedAt) }}</p>
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

          <div class="my-8">
            <app-summary-cards [data]="dashboard"></app-summary-cards>
          </div>

          <section class="grid gap-4 xl:grid-cols-[1.35fr_0.95fr]">
            <div class="min-w-0 rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
              <div class="flex items-center justify-between gap-4">
                <div>
                  <h2 class="text-lg font-bold text-[#1B5E20]">Comparaciones del periodo</h2>
                  <p class="text-sm text-[#6B7D6C]">Evolución contra el periodo anterior</p>
                </div>
                <button (click)="retry()" class="cursor-pointer rounded-full border border-[#C8E6C9] px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                  Recargar
                </button>
              </div>

              <div class="mt-5 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                <div class="min-w-0 rounded-2xl border border-[#E2EEDC] bg-[#FAFCF8] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Aperturas de cuentas</p>
                  <p class="mt-3 text-2xl font-black" [class.text-[#1B5E20]]="dashboard.comparisons.summary.accountOpeningsChangePercent >= 0" [class.text-[#C62828]]="dashboard.comparisons.summary.accountOpeningsChangePercent < 0">
                    {{ formatPercent(dashboard.comparisons.summary.accountOpeningsChangePercent) }}
                  </p>
                </div>
                <div class="min-w-0 rounded-2xl border border-[#E2EEDC] bg-[#FAFCF8] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Transacciones</p>
                  <p class="mt-3 text-2xl font-black" [class.text-[#1B5E20]]="dashboard.comparisons.summary.transactionsCountChangePercent >= 0" [class.text-[#C62828]]="dashboard.comparisons.summary.transactionsCountChangePercent < 0">
                    {{ formatPercent(dashboard.comparisons.summary.transactionsCountChangePercent) }}
                  </p>
                </div>
                <div class="min-w-0 rounded-2xl border border-[#E2EEDC] bg-[#FAFCF8] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Monto transacciones</p>
                  <p class="mt-3 text-2xl font-black" [class.text-[#1B5E20]]="dashboard.comparisons.summary.transactionsAmountChangePercent >= 0" [class.text-[#C62828]]="dashboard.comparisons.summary.transactionsAmountChangePercent < 0">
                    {{ formatPercent(dashboard.comparisons.summary.transactionsAmountChangePercent) }}
                  </p>
                </div>
                <div class="min-w-0 rounded-2xl border border-[#E2EEDC] bg-[#FAFCF8] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Fallidas</p>
                  <p class="mt-3 text-2xl font-black" [class.text-[#1B5E20]]="dashboard.comparisons.summary.failedTransactionsChangePercent <= 0" [class.text-[#C62828]]="dashboard.comparisons.summary.failedTransactionsChangePercent > 0">
                    {{ formatPercent(dashboard.comparisons.summary.failedTransactionsChangePercent) }}
                  </p>
                </div>
              </div>

              <div class="mt-6 grid gap-4 lg:grid-cols-2">
                <div class="min-w-0">
                  <h3 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">Cuentas por estado</h3>
                  <div class="mt-3 space-y-3">
                    @for (item of dashboard.accounts.byStatus; track item.code) {
                      <div class="flex min-w-0 items-center justify-between gap-3 rounded-xl border border-[#E8F2E2] bg-white px-4 py-3">
                        <div class="min-w-0">
                          <p class="min-w-0 break-words text-sm font-semibold text-[#1B5E20]">{{ item.label }}</p>
                          <p class="min-w-0 break-words text-xs text-[#6B7D6C]">{{ item.code }}</p>
                        </div>
                        <p class="text-lg font-black text-[#2E7D32]">{{ item.total }}</p>
                      </div>
                    }
                  </div>
                </div>

                <div class="min-w-0">
                  <h3 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">Transacciones por estado</h3>
                  <div class="mt-3 space-y-3">
                    @for (item of dashboard.transactions.byStatus; track item.code) {
                      <div class="flex min-w-0 items-center justify-between gap-3 rounded-xl border border-[#E8F2E2] bg-white px-4 py-3">
                        <div class="min-w-0">
                          <p class="min-w-0 break-words text-sm font-semibold text-[#1B5E20]">{{ item.label }}</p>
                          <p class="min-w-0 break-words text-xs text-[#6B7D6C]">{{ item.code }}</p>
                        </div>
                        <p class="text-lg font-black text-[#2E7D32]">{{ item.total }}</p>
                      </div>
                    }
                  </div>
                </div>
              </div>
            </div>

            <div class="min-w-0 rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
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
                    <div class="flex min-w-0 items-start justify-between gap-4">
                      <div class="min-w-0 flex-1">
                        <p class="min-w-0 break-words text-sm font-bold text-[#1B5E20]">{{ alert.title }}</p>
                        <p class="mt-1 min-w-0 break-words text-sm text-[#4F5D4F]">{{ alert.message }}</p>
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
                  <h2 class="text-lg font-bold text-[#1B5E20]">Cuentas recientes</h2>
                  <p class="text-sm text-[#6B7D6C]">Últimos movimientos y aperturas</p>
                </div>
                <span class="shrink-0 text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">{{ dashboard.accounts.pendingApproval.total + dashboard.accounts.blockedOrFrozen.total }} items</span>
              </div>

              <div class="mt-4 overflow-x-auto rounded-2xl border border-[#E8F2E2]">
                <table class="min-w-[680px] w-full divide-y divide-[#E8F2E2]">
                  <thead class="bg-[#F7FBF3]">
                    <tr class="text-left text-xs font-bold uppercase tracking-[0.12em] text-[#6B7D6C]">
                      <th class="px-4 py-3">Cuenta</th>
                      <th class="px-4 py-3">Titular</th>
                      <th class="px-4 py-3">Tipo</th>
                      <th class="px-4 py-3">Estado</th>
                      <th class="px-4 py-3 text-right">Saldo</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-[#EEF5EA] bg-white">
                    @for (account of dashboard.accounts.pendingApproval.items; track account.id) {
                      <tr>
                        <td class="px-4 py-3">
                          <p class="font-semibold text-[#1B5E20]">{{ account.accountNumber }}</p>
                          <p class="text-xs text-[#6B7D6C]">{{ formatDateTime(account.createdAt) }}</p>
                        </td>
                        <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ account.holderName }}</td>
                        <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ account.accountType }}</td>
                        <td class="px-4 py-3">
                          <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold uppercase tracking-[0.1em] text-[#2E7D32]">
                            {{ account.status }}
                          </span>
                        </td>
                        <td class="px-4 py-3 text-right text-sm font-semibold text-[#1B5E20]">
                          {{ formatMoney(account.balance.amount, account.balance.currency) }}
                        </td>
                      </tr>
                    }
                    @for (account of dashboard.accounts.blockedOrFrozen.items; track account.id) {
                      <tr>
                        <td class="px-4 py-3">
                          <p class="font-semibold text-[#1B5E20]">{{ account.accountNumber }}</p>
                          <p class="text-xs text-[#6B7D6C]">{{ formatDateTime(account.createdAt) }}</p>
                        </td>
                        <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ account.holderName }}</td>
                        <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ account.accountType }}</td>
                        <td class="px-4 py-3">
                          <span class="rounded-full bg-red-100 px-3 py-1 text-xs font-bold uppercase tracking-[0.1em] text-red-700">
                            {{ account.status }}
                          </span>
                        </td>
                        <td class="px-4 py-3 text-right text-sm font-semibold text-[#1B5E20]">
                          {{ formatMoney(account.balance.amount, account.balance.currency) }}
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>

            <div class="min-w-0 rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
              <div class="flex items-center justify-between">
                <div class="min-w-0">
                  <h2 class="text-lg font-bold text-[#1B5E20]">Actividad reciente</h2>
                  <p class="text-sm text-[#6B7D6C]">Eventos, transacciones y movimientos</p>
                </div>
                <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.recentActivity.length }}</span>
              </div>

              <div class="mt-4 space-y-3">
                @for (activity of dashboard.recentActivity; track activity.referenceId + activity.type) {
                  <div class="min-w-0 rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <div class="flex min-w-0 items-start justify-between gap-4">
                      <div class="min-w-0 flex-1">
                        <p class="min-w-0 break-words text-sm font-bold text-[#1B5E20]">{{ activity.title }}</p>
                        <p class="mt-1 text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">{{ activity.type }}</p>
                        <p class="mt-2 min-w-0 break-words text-xs text-[#6B7D6C]">{{ activity.source }}</p>
                      </div>
                      <span class="rounded-full px-3 py-1 text-[11px] font-bold uppercase tracking-[0.14em]"
                        [class.bg-red-100]="severity(activity.severity) === 'high'"
                        [class.text-red-700]="severity(activity.severity) === 'high'"
                        [class.bg-amber-100]="severity(activity.severity) === 'warn'"
                        [class.text-amber-800]="severity(activity.severity) === 'warn'"
                        [class.bg-sky-100]="severity(activity.severity) === 'info'"
                        [class.text-sky-700]="severity(activity.severity) === 'info'">
                        {{ activity.type }}
                      </span>
                    </div>
                    @if (activityDetails(activity).length > 0) {
                      <div class="mt-3 grid gap-2 sm:grid-cols-3">
                        @for (detail of activityDetails(activity); track detail.label) {
                          <div class="min-w-0 rounded-xl border border-[#E8F2E2] bg-white px-3 py-2">
                            <p class="text-[11px] font-bold uppercase tracking-[0.12em] text-[#6B7D6C]">{{ detail.label }}</p>
                            <p class="mt-1 min-w-0 break-words text-sm font-semibold text-[#1B5E20]">{{ detail.value }}</p>
                          </div>
                        }
                      </div>
                    }
                    <div class="mt-3 flex flex-wrap items-center justify-between gap-2 text-xs text-[#6B7D6C]">
                      <span>{{ formatDateTime(activity.timestamp) }}</span>
                      <span>{{ activity.severity }}</span>
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
                <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.transactions.recent.length }}</span>
              </div>

              <div class="mt-4 grid gap-4 lg:grid-cols-[1fr_0.85fr]">
                <div class="space-y-4">
                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <div class="flex items-center justify-between gap-3">
                      <p class="text-sm font-semibold text-[#567157]">Por tipo</p>
                      <span class="text-xs font-semibold text-[#6B7D6C]">{{ dashboard.transactions.byType.length }} grupos</span>
                    </div>
                    <div class="mt-3 space-y-3">
                      @for (item of dashboard.transactions.byType; track item.code) {
                        <div class="min-w-0">
                          <div class="flex items-center justify-between gap-3 text-sm">
                            <span class="min-w-0 break-words text-[#1B5E20]">{{ item.label }}</span>
                            <span class="font-semibold text-[#2E7D32]">{{ item.total }}</span>
                          </div>
                          <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                            <div class="h-full rounded-full bg-[#2E7D32]" [style.width.%]="barWidth(item.total, maxTotal(dashboard.transactions.byType))"></div>
                          </div>
                        </div>
                      }
                    </div>
                  </div>

                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <div class="flex items-center justify-between gap-3">
                      <p class="text-sm font-semibold text-[#567157]">Volumen diario</p>
                      <span class="text-xs font-semibold text-[#6B7D6C]">{{ dashboard.transactions.dailyVolume.length }} días</span>
                    </div>
                    <div class="mt-3 space-y-3">
                      @for (point of dashboard.transactions.dailyVolume; track point.date) {
                        <div class="min-w-0">
                          <div class="flex items-center justify-between gap-3 text-sm">
                            <span class="min-w-0 break-words text-xs text-[#6B7D6C]">{{ formatDate(point.date) }}</span>
                            <span class="font-semibold text-[#1B5E20]">{{ point.total }}</span>
                          </div>
                          <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                            <div class="h-full rounded-full bg-[#7CB342]" [style.width.%]="barWidth(point.total, maxTotal(dashboard.transactions.dailyVolume))"></div>
                          </div>
                        </div>
                      }
                    </div>
                  </div>
                </div>

                <div class="min-w-0">
                  <p class="text-sm font-semibold text-[#567157]">Movimientos recientes</p>
                  <div class="mt-3 space-y-3">
                    @for (item of dashboard.transactions.recent; track item.id) {
                      <div class="min-w-0 rounded-2xl border border-[#E8F2E2] bg-white p-4 shadow-sm">
                        <div class="flex min-w-0 items-start justify-between gap-4">
                          <div class="min-w-0 flex-1">
                            <p class="min-w-0 break-words text-sm font-semibold text-[#1B5E20]">{{ item.type }}</p>
                            <p class="mt-1 min-w-0 break-words text-xs text-[#6B7D6C]">
                              {{ item.sourceAccountNumber || 'N/A' }} → {{ item.targetAccountNumber || 'N/A' }}
                            </p>
                          </div>
                          <div class="text-right">
                            <p class="text-sm font-bold text-[#1B5E20]">{{ formatMoney(item.amount.amount, item.amount.currency) }}</p>
                            <p class="mt-1 text-[11px] font-bold uppercase tracking-[0.12em] text-[#2E7D32]">{{ item.status }}</p>
                          </div>
                        </div>
                        @if (item.description) {
                          <p class="mt-3 line-clamp-2 min-w-0 break-words text-xs text-[#6B7D6C]">{{ item.description }}</p>
                        }
                      </div>
                    }
                  </div>
                </div>
              </div>
            </div>

            <div class="space-y-4">
              <div class="min-w-0 rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
                <h2 class="text-lg font-bold text-[#1B5E20]">Límites</h2>
                <div class="mt-4 grid gap-3 md:grid-cols-2">
                  <div class="min-w-0 rounded-2xl bg-[#FAFCF8] p-4">
                    <p class="text-sm font-semibold text-[#567157]">Por tipo</p>
                    <div class="mt-3 space-y-2">
                      @for (item of dashboard.limits.byType; track item.code) {
                        <div class="min-w-0">
                          <div class="flex items-center justify-between gap-3 text-sm">
                            <span class="min-w-0 break-words text-[#1B5E20]">{{ item.label }}</span>
                            <span class="font-semibold text-[#2E7D32]">{{ item.total }}</span>
                          </div>
                          <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                            <div class="h-full rounded-full bg-[#2E7D32]" [style.width.%]="barWidth(item.total, maxTotal(dashboard.limits.byType))"></div>
                          </div>
                        </div>
                      }
                    </div>
                  </div>
                  <div class="min-w-0 rounded-2xl bg-[#FAFCF8] p-4">
                    <p class="text-sm font-semibold text-[#567157]">Por alcance</p>
                    <div class="mt-3 space-y-2">
                      @for (item of dashboard.limits.byScope; track item.code) {
                        <div class="min-w-0">
                          <div class="flex items-center justify-between gap-3 text-sm">
                            <span class="min-w-0 break-words text-[#1B5E20]">{{ item.label }}</span>
                            <span class="font-semibold text-[#2E7D32]">{{ item.total }}</span>
                          </div>
                          <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                            <div class="h-full rounded-full bg-[#7CB342]" [style.width.%]="barWidth(item.total, maxTotal(dashboard.limits.byScope))"></div>
                          </div>
                        </div>
                      }
                    </div>
                  </div>
                </div>
              </div>

              <div class="min-w-0 rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
                <h2 class="text-lg font-bold text-[#1B5E20]">Contabilidad y FX</h2>
                <div class="mt-4 space-y-4">
                  <div>
                    <p class="text-sm font-semibold text-[#567157]">Períodos y asientos</p>
                    <div class="mt-3 space-y-2">
                      @for (item of dashboard.accounting.periodsByStatus; track item.code) {
                        <div class="rounded-xl bg-[#FAFCF8] px-4 py-3 text-sm">
                          <div class="flex min-w-0 items-center justify-between gap-3">
                            <span class="min-w-0 break-words text-[#1B5E20]">{{ item.label }}</span>
                            <span class="font-semibold text-[#2E7D32]">{{ item.total }}</span>
                          </div>
                          <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                            <div class="h-full rounded-full bg-[#2E7D32]" [style.width.%]="barWidth(item.total, maxTotal(dashboard.accounting.periodsByStatus))"></div>
                          </div>
                        </div>
                      }
                    </div>
                  </div>

                  <div>
                    <p class="text-sm font-semibold text-[#567157]">Tipos de cambio</p>
                    <div class="mt-3 space-y-2">
                      @for (rate of dashboard.fx.exchangeRates; track rate.id) {
                        <div class="rounded-xl bg-[#FAFCF8] px-4 py-3 text-sm">
                          <div class="flex min-w-0 items-center justify-between gap-3">
                            <span class="min-w-0 break-words text-[#1B5E20]">{{ rate.sourceCurrency }} → {{ rate.targetCurrency }}</span>
                            <span class="font-semibold text-[#2E7D32]">{{ rate.rate }}</span>
                          </div>
                          <div class="mt-2 h-2 overflow-hidden rounded-full bg-white">
                            <div class="h-full rounded-full bg-[#7CB342]" [style.width.%]="rate.active ? 100 : 55"></div>
                          </div>
                        </div>
                      }
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section class="grid gap-4 xl:grid-cols-[1fr_1fr]">
            <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
              <h2 class="text-lg font-bold text-[#1B5E20]">Límites activos</h2>
              <div class="mt-4 space-y-3">
                @for (rule of dashboard.limits.activeRules; track rule.code) {
                  <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                    <div class="flex items-start justify-between gap-4">
                      <div>
                        <p class="font-semibold text-[#1B5E20]">{{ rule.name }}</p>
                        <p class="text-xs text-[#6B7D6C]">{{ rule.limitType }} · {{ rule.scopeType }} · {{ rule.period || 'N/A' }}</p>
                      </div>
                      <span class="rounded-full px-3 py-1 text-[11px] font-bold uppercase tracking-[0.14em]"
                        [class.bg-[#E8F5E9]]="rule.active"
                        [class.text-[#2E7D32]]="rule.active"
                        [class.bg-red-100]="!rule.active"
                        [class.text-red-700]="!rule.active">
                        {{ rule.active ? 'Activo' : 'Inactivo' }}
                      </span>
                    </div>
                  </div>
                }
              </div>
            </div>

            <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
              <h2 class="text-lg font-bold text-[#1B5E20]">Insights</h2>
              <div class="mt-4 space-y-3">
                @for (insight of dashboard.insights; track insight.code) {
                  <div class="rounded-2xl border px-4 py-4"
                    [class.border-red-200]="severity(insight.severity) === 'high'"
                    [class.bg-red-50]="severity(insight.severity) === 'high'"
                    [class.border-amber-200]="severity(insight.severity) === 'warn'"
                    [class.bg-amber-50]="severity(insight.severity) === 'warn'"
                    [class.border-sky-200]="severity(insight.severity) === 'info'"
                    [class.bg-sky-50]="severity(insight.severity) === 'info'">
                    <p class="font-semibold text-[#1B5E20]">{{ insight.title }}</p>
                    <p class="mt-1 text-sm text-[#4F5D4F]">{{ insight.message }}</p>
                    <div class="mt-3 flex items-center justify-between text-xs text-[#6B7D6C]">
                      <span>{{ insight.trend }}</span>
                      <span>{{ insight.value }}</span>
                    </div>
                  </div>
                }
              </div>
            </div>
          </section>
        }
      }
    </div>
  `
})
export class SummaryPageComponent implements OnInit {
  public readonly summaryUseCase = inject(SummaryUseCase);
  readonly loadingSkeleton = [1, 2, 3, 4];

  ngOnInit(): void {
    this.summaryUseCase.loadSummary();
  }

  retry(): void {
    this.summaryUseCase.loadSummary();
  }

  activityDetails(activity: TenantDashboardActivityItem): Array<{ label: string; value: string }> {
    const parsed = this.parseDescription(activity.description);
    if (!parsed) {
      return [];
    }

    const details = [
      { label: 'Nombre', value: this.asText(parsed['name']) },
      { label: 'Slug', value: this.asText(parsed['slug']) },
      { label: 'Plan', value: this.asText(parsed['planCode'] ?? parsed['plan']) }
    ].filter((item) => item.value);

    return details;
  }

  private parseDescription(value: string | null | undefined): Record<string, unknown> | null {
    if (!value) {
      return null;
    }

    try {
      const parsed = JSON.parse(value);
      return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? (parsed as Record<string, unknown>) : null;
    } catch {
      return null;
    }
  }

  private asText(value: unknown): string {
    return typeof value === 'string' ? value.trim() : '';
  }

  maxTotal(items: Array<{ total: number }>): number {
    const max = items.reduce((current, item) => Math.max(current, item.total), 0);
    return Math.max(max, 1);
  }

  barWidth(value: number, max: number): number {
    if (max <= 0) {
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

  formatDate(value: string | null | undefined): string {
    if (!value) return 'Sin fecha';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return 'Fecha inválida';
    return new Intl.DateTimeFormat('es-BO', {
      timeZone: 'America/La_Paz',
      dateStyle: 'medium'
    }).format(date);
  }

  formatPercent(value: number): string {
    const prefix = value > 0 ? '+' : '';
    return `${prefix}${value.toFixed(1)}%`;
  }

  formatMoney(amount: number, currency: string): string {
    return new Intl.NumberFormat('es-BO', {
      style: 'currency',
      currency,
      maximumFractionDigits: 2
    }).format(amount);
  }
}
