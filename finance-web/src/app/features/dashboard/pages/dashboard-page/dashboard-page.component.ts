import { Component, inject, signal, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { DashboardService, TenantSummary } from '../../data-access/dashboard.service';
import { SessionStore } from '../../../../core/session/store/session.store';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './dashboard-page.html',
  styleUrls: ['./dashboard-page.css'],
})
export class DashboardPageComponent {
  private readonly dashboardService = inject(DashboardService);
  private readonly sessionStore = inject(SessionStore);

  summary = signal<TenantSummary | null>(null);
  isLoading = signal(false);
  error = signal<string | null>(null);

  /** User greeting name */
  greeting = computed(() => {
    const name = this.sessionStore.userFullName();
    return name ?? 'Usuario';
  });

  /** Time-based greeting */
  greetingText = computed(() => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Buenos días';
    if (hour < 19) return 'Buenas tardes';
    return 'Buenas noches';
  });

  /** Progress bar percentage */
  usersProgress = computed(() => {
    const data = this.summary();
    if (!data || !data.plan.maxUsers) return 0;
    return Math.round((data.usersCount / data.plan.maxUsers) * 100);
  });

  constructor() {
    this.loadSummary();
  }

  private loadSummary(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.dashboardService.getTenantSummary().subscribe({
      next: (data) => {
        this.summary.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        const errorMsg = err.error?.message || 'Error cargando el resumen del dashboard';
        this.error.set(errorMsg);
        this.isLoading.set(false);
      },
    });
  }

  getSubscriptionStatusText(): string {
    const status = this.summary()?.subscription.status;
    switch (status) {
      case 'ACTIVE': return 'Activa';
      case 'TRIAL': return 'En Prueba';
      case 'EXPIRED': return 'Expirada';
      case 'CANCELLED': return 'Cancelada';
      default: return 'Desconocido';
    }
  }

  getSubscriptionStatusClass(): string {
    const status = this.summary()?.subscription.status;
    switch (status) {
      case 'ACTIVE': return 'status-active';
      case 'TRIAL': return 'status-trial';
      case 'EXPIRED': return 'status-expired';
      case 'CANCELLED': return 'status-cancelled';
      default: return '';
    }
  }
}
