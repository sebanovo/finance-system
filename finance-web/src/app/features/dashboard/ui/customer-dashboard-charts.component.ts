import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import {
  CustomerDashboardAccountItem,
  CustomerDashboardCurrencyBalanceItem,
  CustomerDashboardTransactionAggregateItem,
  CustomerSummaryResponse,
} from '../../../entities/dashboard';

interface DashboardChartBar {
  label: string;
  value: number;
  valueLabel: string;
  percent: number;
  color: string;
}

@Component({
  selector: 'app-customer-dashboard-charts',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (data; as dashboard) {
      <section class="grid gap-4 xl:grid-cols-2">
        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <div class="flex items-start justify-between gap-4">
            <div>
              <h2 class="text-lg font-bold text-[#1B5E20]">Cuentas por tipo</h2>
              <p class="text-sm text-[#6B7D6C]">Mix de productos del cliente</p>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.accounts.items.length }}</span>
          </div>

          <div class="mt-4 space-y-3">
            @for (item of accountTypeBars; track item.label) {
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <div class="flex items-center justify-between gap-3 text-sm">
                  <span class="min-w-0 truncate font-semibold text-[#1B5E20]">{{ item.label }}</span>
                  <span class="font-bold" [style.color]="item.color">{{ item.valueLabel }}</span>
                </div>
                <div class="mt-2 h-2 rounded-full bg-[#E8F5E9]">
                  <div class="h-2 rounded-full" [style.width.%]="item.percent" [style.background]="item.color"></div>
                </div>
              </div>
            }
          </div>
        </div>

        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <div class="flex items-start justify-between gap-4">
            <div>
              <h2 class="text-lg font-bold text-[#1B5E20]">Saldo por moneda</h2>
              <p class="text-sm text-[#6B7D6C]">Distribución del patrimonio disponible</p>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.balances.byCurrency.length }}</span>
          </div>

          <div class="mt-4 space-y-3">
            @for (item of balanceBars; track item.label) {
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <div class="flex items-center justify-between gap-3 text-sm">
                  <span class="min-w-0 truncate font-semibold text-[#1B5E20]">{{ item.label }}</span>
                  <span class="font-bold text-[#2E7D32]">{{ item.valueLabel }}</span>
                </div>
                <div class="mt-2 h-2 rounded-full bg-[#E8F5E9]">
                  <div class="h-2 rounded-full" [style.width.%]="item.percent" [style.background]="item.color"></div>
                </div>
              </div>
            }
          </div>
        </div>

        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <div class="flex items-start justify-between gap-4">
            <div>
              <h2 class="text-lg font-bold text-[#1B5E20]">Movimientos por tipo</h2>
              <p class="text-sm text-[#6B7D6C]">Distribución monetaria de los últimos registros</p>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.transactions.byType.length }}</span>
          </div>

          <div class="mt-4 space-y-3">
            @for (item of transactionTypeBars; track item.label) {
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
                <div class="flex items-center justify-between gap-3 text-sm">
                  <span class="min-w-0 truncate font-semibold text-[#1B5E20]">{{ item.label }}</span>
                  <span class="font-bold text-[#2E7D32]">{{ item.valueLabel }}</span>
                </div>
                <div class="mt-2 h-2 rounded-full bg-[#E8F5E9]">
                  <div class="h-2 rounded-full" [style.width.%]="item.percent" [style.background]="item.color"></div>
                </div>
              </div>
            }
          </div>
        </div>

        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm xl:col-span-2">
          <div class="flex items-start justify-between gap-4">
            <div>
              <h2 class="text-lg font-bold text-[#1B5E20]">Volumen mensual</h2>
              <p class="text-sm text-[#6B7D6C]">Evolución de tus montos por día</p>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.transactions.monthlyVolume.length }} puntos</span>
          </div>

          @if (monthlyVolumeBars.length > 0) {
            <div class="mt-5 flex h-56 items-end gap-3 rounded-3xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
              @for (item of monthlyVolumeBars; track item.label) {
                <div class="flex min-w-0 flex-1 flex-col items-center justify-end">
                  <div class="flex h-full w-full items-end justify-center">
                    <div class="w-full max-w-10 rounded-t-2xl bg-gradient-to-t from-[#A5D6A7] via-[#66BB6A] to-[#2E7D32]" [style.height.%]="item.percent"></div>
                  </div>
                  <p class="mt-3 text-center text-[10px] font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">{{ item.label }}</p>
                  <p class="mt-1 text-center text-[11px] font-bold text-[#1B5E20]">{{ item.valueLabel }}</p>
                </div>
              }
            </div>
          } @else {
            <div class="mt-4 rounded-2xl border border-dashed border-[#D7E7D6] bg-[#FAFCF8] p-6 text-sm text-[#6B7D6C]">
              No hay volumen mensual para graficar.
            </div>
          }
        </div>
      </section>
    }
  `,
})
export class CustomerDashboardChartsComponent {
  @Input() data: CustomerSummaryResponse | null = null;

  readonly palette = ['#2E7D32', '#43A047', '#66BB6A', '#1B5E20', '#7CB342', '#A5D6A7'];

  get accountTypeBars(): DashboardChartBar[] {
    const accounts = this.data?.accounts.items ?? [];
    const grouped = new Map<string, number>();
    for (const account of accounts) {
      const key = this.prettyLabel(account.type || 'Cuenta');
      grouped.set(key, (grouped.get(key) ?? 0) + 1);
    }
    return this.buildCountBarsFromMap(grouped);
  }

  get balanceBars(): DashboardChartBar[] {
    return this.buildMoneyBars(
      (this.data?.balances.byCurrency ?? []).map((item) => ({
        label: item.currency,
        amount: item.balance.amount,
        currency: item.balance.currency,
      })),
    );
  }

  get transactionTypeBars(): DashboardChartBar[] {
    return this.buildMoneyBars(
      (this.data?.transactions.byType ?? []).map((item) => ({
        label: this.prettyLabel(item.type),
        amount: item.amount.amount,
        currency: item.amount.currency,
      })),
    );
  }

  get monthlyVolumeBars(): DashboardChartBar[] {
    return this.buildMoneyBars(
      (this.data?.transactions.monthlyVolume ?? []).slice(-7).map((item) => ({
        label: this.formatDate(item.date),
        amount: item.amount.amount,
        currency: item.amount.currency,
      })),
    );
  }

  private buildCountBarsFromMap(grouped: Map<string, number>): DashboardChartBar[] {
    const entries = Array.from(grouped.entries());
    const max = Math.max(1, ...entries.map((entry) => entry[1]));
    return entries.map((entry, index) => ({
      label: entry[0],
      value: entry[1],
      valueLabel: `${entry[1]}`,
      percent: Math.round((entry[1] / max) * 100),
      color: this.colorForIndex(index, entry[0]),
    }));
  }

  private buildMoneyBars(
    items: Array<{ label: string; amount: number; currency: string }>,
  ): DashboardChartBar[] {
    const max = Math.max(1, ...items.map((item) => item.amount));
    return items.map((item, index) => ({
      label: item.label,
      value: item.amount,
      valueLabel: this.formatMoney(item.amount, item.currency),
      percent: Math.round((item.amount / max) * 100),
      color: this.colorForIndex(index, item.label),
    }));
  }

  private colorForIndex(index: number, label?: string): string {
    const normalized = (label ?? '').toUpperCase();
    if (normalized.includes('ACTIVE')) {
      return '#2E7D32';
    }
    if (normalized.includes('PENDING') || normalized.includes('REVIEW')) {
      return '#F9A825';
    }
    if (normalized.includes('FAILED') || normalized.includes('BLOCKED') || normalized.includes('REJECT')) {
      return '#C62828';
    }
    return this.palette[index % this.palette.length];
  }

  private prettyLabel(value: string): string {
    return value
      .replaceAll('_', ' ')
      .toLowerCase()
      .replace(/(^|\s)\S/g, (c) => c.toUpperCase());
  }

  private formatDate(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return 'N/D';
    }
    return date.toLocaleDateString('es-BO', { day: '2-digit', month: '2-digit' });
  }

  private formatMoney(amount: number, currency: string): string {
    return `${amount.toFixed(2)} ${currency}`;
  }
}
