import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import {
  TenantDashboardCountItem,
  TenantDashboardDailyMoneyPoint,
  TenantSummaryResponse,
} from '../../../entities/dashboard';

interface DashboardChartBar {
  label: string;
  value: number;
  valueLabel: string;
  percent: number;
  color: string;
}

@Component({
  selector: 'app-tenant-dashboard-charts',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (data; as dashboard) {
      <section class="grid gap-4 xl:grid-cols-2">
        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <div class="flex items-start justify-between gap-4">
            <div>
              <h2 class="text-lg font-bold text-[#1B5E20]">Usuarios</h2>
              <p class="text-sm text-[#6B7D6C]">Distribución activa e inactiva</p>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.summary.users.total }}</span>
          </div>

          <div class="mt-5 flex flex-col gap-5 sm:flex-row sm:items-center">
            <div class="mx-auto flex h-40 w-40 shrink-0 items-center justify-center rounded-full border border-[#E8F2E2] bg-[#F7FBF3] p-3">
              <div class="flex h-full w-full items-center justify-center rounded-full bg-white shadow-inner" [style.background]="userDonutBackground">
                <div class="rounded-full bg-white px-4 py-3 text-center shadow-[0_10px_30px_rgba(27,94,32,0.08)]">
                  <p class="text-3xl font-black text-[#1B5E20]">{{ dashboard.summary.users.active }}</p>
                  <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Activos</p>
                </div>
              </div>
            </div>

            <div class="flex-1 space-y-3">
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] px-4 py-3">
                <div class="flex items-center justify-between gap-4 text-sm">
                  <span class="font-semibold text-[#1B5E20]">Activos</span>
                  <span class="font-bold text-[#2E7D32]">{{ dashboard.summary.users.active }}</span>
                </div>
                <div class="mt-2 h-2 rounded-full bg-[#E8F5E9]">
                  <div class="h-2 rounded-full bg-[#2E7D32]" [style.width.%]="userActivePercent"></div>
                </div>
              </div>
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#FAFCF8] px-4 py-3">
                <div class="flex items-center justify-between gap-4 text-sm">
                  <span class="font-semibold text-[#1B5E20]">Inactivos</span>
                  <span class="font-bold text-[#C62828]">{{ dashboard.summary.users.inactive }}</span>
                </div>
                <div class="mt-2 h-2 rounded-full bg-[#FCE8E8]">
                  <div class="h-2 rounded-full bg-[#C62828]" [style.width.%]="100 - userActivePercent"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <div class="flex items-start justify-between gap-4">
            <div>
              <h2 class="text-lg font-bold text-[#1B5E20]">Cuentas por estado</h2>
              <p class="text-sm text-[#6B7D6C]">Balance operativo del tenant</p>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.summary.accounts.total }}</span>
          </div>

          <div class="mt-4 space-y-3">
            @for (item of accountStatusBars; track item.label) {
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
              <h2 class="text-lg font-bold text-[#1B5E20]">Transacciones por tipo</h2>
              <p class="text-sm text-[#6B7D6C]">Distribución del movimiento monetario</p>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dashboard.summary.transactions.total }}</span>
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
              <h2 class="text-lg font-bold text-[#1B5E20]">Volumen diario</h2>
              <p class="text-sm text-[#6B7D6C]">Evolución reciente en montos</p>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-bold text-[#2E7D32]">{{ dailyAmountBars.length }} puntos</span>
          </div>

          @if (dailyAmountBars.length > 0) {
            <div class="mt-5 flex h-56 items-end gap-3 rounded-3xl border border-[#E8F2E2] bg-[#FAFCF8] p-4">
              @for (item of dailyAmountBars; track item.label) {
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
              No hay volumen diario suficiente para graficar.
            </div>
          }
        </div>
      </section>
    }
  `,
})
export class TenantDashboardChartsComponent {
  @Input() data: TenantSummaryResponse | null = null;

  readonly palette = ['#2E7D32', '#43A047', '#66BB6A', '#1B5E20', '#7CB342', '#A5D6A7'];

  get userActivePercent(): number {
    const total = this.data?.summary.users.total ?? 0;
    const active = this.data?.summary.users.active ?? 0;
    return total > 0 ? Math.round((active / total) * 100) : 0;
  }

  get userDonutBackground(): string {
    const active = this.data?.summary.users.active ?? 0;
    const inactive = this.data?.summary.users.inactive ?? 0;
    const total = Math.max(1, active + inactive);
    const activePercent = (active / total) * 100;
    return `conic-gradient(#2E7D32 0 ${activePercent}%, #E0E9DE ${activePercent}% 100%)`;
  }

  get accountStatusBars(): DashboardChartBar[] {
    return this.buildCountBars(this.data?.accounts.byStatus ?? []);
  }

  get transactionTypeBars(): DashboardChartBar[] {
    return this.buildCountBars(this.data?.transactions.byType ?? []);
  }

  get dailyAmountBars(): DashboardChartBar[] {
    const points = (this.data?.transactions.dailyAmount ?? []).slice(-7);
    return this.buildMoneyBars(points);
  }

  private buildCountBars(items: TenantDashboardCountItem[]): DashboardChartBar[] {
    const max = Math.max(1, ...items.map((item) => item.total));
    return items.map((item, index) => ({
      label: this.prettyLabel(item.label || item.code),
      value: item.total,
      valueLabel: `${item.total}`,
      percent: Math.round((item.total / max) * 100),
      color: this.colorForIndex(index, item.code),
    }));
  }

  private buildMoneyBars(items: TenantDashboardDailyMoneyPoint[]): DashboardChartBar[] {
    const max = Math.max(1, ...items.map((item) => item.amount.amount));
    return items.map((item, index) => ({
      label: this.formatDate(item.date),
      value: item.amount.amount,
      valueLabel: this.formatMoney(item.amount.amount, item.amount.currency),
      percent: Math.round((item.amount.amount / max) * 100),
      color: this.colorForIndex(index),
    }));
  }

  private colorForIndex(index: number, code?: string): string {
    if (code) {
      const normalized = code.toUpperCase();
      if (normalized.includes('ACTIVE') || normalized.includes('COMPLETED') || normalized.includes('APPROVED')) {
        return '#2E7D32';
      }
      if (normalized.includes('PENDING') || normalized.includes('REVIEW')) {
        return '#F9A825';
      }
      if (normalized.includes('FAILED') || normalized.includes('BLOCKED') || normalized.includes('REJECT')) {
        return '#C62828';
      }
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
