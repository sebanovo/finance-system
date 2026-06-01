import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { CustomerSummaryResponse, DashboardService } from '../../../entities/dashboard';

export interface CustomerSummaryState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: CustomerSummaryResponse | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class CustomerSummaryUseCase {
  private readonly dashboardService = inject(DashboardService);

  private readonly state = signal<CustomerSummaryState>({
    status: 'idle',
    data: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadSummary(): Promise<void> {
    this.state.set({ status: 'loading', data: null, error: null });

    try {
      const response = await firstValueFrom(this.dashboardService.getCustomerSummary());

      if (response.success && response.data) {
        this.state.set({ status: 'success', data: this.normalizeSummary(response.data), error: null });
      } else {
        this.state.set({
          status: 'error',
          data: null,
          error: response.message || 'No se pudo cargar el resumen del cliente'
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: null, error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', data: null, error: null });
  }

  private normalizeSummary(summary: CustomerSummaryResponse): CustomerSummaryResponse {
    const metadata = summary.metadata ?? {
      generatedAt: new Date().toISOString(),
      timezone: 'America/La_Paz',
      tenantSlug: '',
      generatedBy: '',
      baseCurrency: 'BOB',
      dataCompleteness: '0%'
    };

    const baseCurrency = this.normalizeCurrency(metadata.baseCurrency);

    const money = (value?: { amount?: number | null; currency?: string | null } | null) => ({
      amount: this.normalizeAmount(value?.amount),
      currency: this.normalizeCurrency(value?.currency ?? baseCurrency)
    });

    return {
      metadata: {
        generatedAt: metadata.generatedAt ?? new Date().toISOString(),
        timezone: metadata.timezone ?? 'America/La_Paz',
        tenantSlug: metadata.tenantSlug ?? '',
        generatedBy: metadata.generatedBy ?? '',
        baseCurrency,
        dataCompleteness: metadata.dataCompleteness ?? '0%'
      },
      summary: {
        accounts: this.normalizeAmount(summary.summary?.accounts),
        totalBalance: money(summary.summary?.totalBalance ?? null),
        monthlyIncome: money(summary.summary?.monthlyIncome ?? null),
        monthlyExpenses: money(summary.summary?.monthlyExpenses ?? null),
        pendingTransactions: this.normalizeAmount(summary.summary?.pendingTransactions),
        unreadNotifications: this.normalizeAmount(summary.summary?.unreadNotifications)
      },
      accounts: {
        items: Array.isArray(summary.accounts?.items) ? summary.accounts.items : []
      },
      balances: {
        byCurrency: Array.isArray(summary.balances?.byCurrency) ? summary.balances.byCurrency : []
      },
      transactions: {
        monthlyVolume: Array.isArray(summary.transactions?.monthlyVolume) ? summary.transactions.monthlyVolume : [],
        byType: Array.isArray(summary.transactions?.byType) ? summary.transactions.byType : [],
        recent: {
          total: this.normalizeAmount(summary.transactions?.recent?.total),
          items: Array.isArray(summary.transactions?.recent?.items) ? summary.transactions.recent.items : []
        },
        pending: {
          total: this.normalizeAmount(summary.transactions?.pending?.total),
          items: Array.isArray(summary.transactions?.pending?.items) ? summary.transactions.pending.items : []
        }
      },
      limits: {
        transfer: {
          daily: {
            period: summary.limits?.transfer?.daily?.period ?? 'DAILY',
            used: money(summary.limits?.transfer?.daily?.used ?? null),
            limit: money(summary.limits?.transfer?.daily?.limit ?? null),
            usedCount: this.normalizeAmount(summary.limits?.transfer?.daily?.usedCount),
            limitCount: summary.limits?.transfer?.daily?.limitCount ?? null,
            activeRules: this.normalizeAmount(summary.limits?.transfer?.daily?.activeRules),
            requiresReview: !!summary.limits?.transfer?.daily?.requiresReview,
            applicable: summary.limits?.transfer?.daily?.applicable ?? true
          },
          monthly: {
            period: summary.limits?.transfer?.monthly?.period ?? 'MONTHLY',
            used: money(summary.limits?.transfer?.monthly?.used ?? null),
            limit: money(summary.limits?.transfer?.monthly?.limit ?? null),
            usedCount: this.normalizeAmount(summary.limits?.transfer?.monthly?.usedCount),
            limitCount: summary.limits?.transfer?.monthly?.limitCount ?? null,
            activeRules: this.normalizeAmount(summary.limits?.transfer?.monthly?.activeRules),
            requiresReview: !!summary.limits?.transfer?.monthly?.requiresReview,
            applicable: summary.limits?.transfer?.monthly?.applicable ?? true
          }
        },
        withdrawal: {
          daily: {
            period: summary.limits?.withdrawal?.daily?.period ?? 'DAILY',
            used: money(summary.limits?.withdrawal?.daily?.used ?? null),
            limit: money(summary.limits?.withdrawal?.daily?.limit ?? null),
            usedCount: this.normalizeAmount(summary.limits?.withdrawal?.daily?.usedCount),
            limitCount: summary.limits?.withdrawal?.daily?.limitCount ?? null,
            activeRules: this.normalizeAmount(summary.limits?.withdrawal?.daily?.activeRules),
            requiresReview: !!summary.limits?.withdrawal?.daily?.requiresReview,
            applicable: summary.limits?.withdrawal?.daily?.applicable ?? true
          }
        },
        activeRules: Array.isArray(summary.limits?.activeRules) ? summary.limits.activeRules : []
      },
      notifications: {
        unread: this.normalizeAmount(summary.notifications?.unread),
        items: Array.isArray(summary.notifications?.items) ? summary.notifications.items : []
      },
      alerts: Array.isArray(summary.alerts) ? summary.alerts : [],
      insights: Array.isArray(summary.insights) ? summary.insights : []
    };
  }

  private normalizeAmount(value: number | string | null | undefined): number {
    if (typeof value === 'number' && Number.isFinite(value)) {
      return value;
    }

    if (typeof value === 'string') {
      const parsed = Number(value);
      return Number.isFinite(parsed) ? parsed : 0;
    }

    return 0;
  }

  private normalizeCurrency(value: string | null | undefined): string {
    const currency = (value || 'BOB').trim().toUpperCase();
    return currency.length > 0 ? currency : 'BOB';
  }
}
