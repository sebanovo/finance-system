import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PlatformTenantStore } from '../../store/platform-tenant.store';
import { TenantTableComponent } from '../../components/tenant-table/tenant-table.component';
import { TenantDetailDialogComponent } from '../../components/tenant-detail-dialog/tenant-detail-dialog.component';
import { PlatformTenantResponse } from '../../models/platform-tenant.models';

@Component({
  selector: 'app-tenant-list-page',
  standalone: true,
  imports: [TenantTableComponent, TenantDetailDialogComponent, RouterLink],
  template: `
    <section class="p-6">
      <header class="flex justify-between items-center mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Organizaciones (Tenants)</h1>
          <p class="text-sm text-gray-500">Gestiona las empresas que utilizan tu plataforma financiera.</p>
        </div>
        <a routerLink="create" class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition shadow-sm">
          + Nueva Empresa
        </a>
      </header>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        <article class="bg-white p-4 rounded-lg shadow border-l-4 border-blue-500">
          <p class="text-xs uppercase font-bold text-gray-500">Total Registradas</p>
          <p class="text-2xl font-bold">{{ store.totalTenantsCount() }}</p>
        </article>
        <article class="bg-white p-4 rounded-lg shadow border-l-4 border-green-500">
          <p class="text-xs uppercase font-bold text-gray-500">Suscripciones Activas</p>
          <p class="text-2xl font-bold">{{ store.activeTenantsCount() }}</p>
        </article>
      </div>

      @if (store.error()) {
        <div class="p-4 mb-4 text-sm text-red-800 rounded-lg bg-red-50 border border-red-200" role="alert">
          {{ store.error() }}
        </div>
      }

      <app-tenant-table 
        [tenants]="store.tenants()" 
        [isLoading]="store.isLoading()"
        (toggleStatus)="onToggleStatus($event)"
        (viewDetails)="onViewDetails($event)">
      </app-tenant-table>

      <app-tenant-detail-dialog
        [tenant]="store.selectedTenant()"
        (close)="store.clearSelectedTenant()">
      </app-tenant-detail-dialog>
    </section>
  `
})
export class TenantListPageComponent implements OnInit {
  readonly store = inject(PlatformTenantStore);

  ngOnInit() {
    this.store.loadTenants();
  }

  onToggleStatus(tenant: PlatformTenantResponse) {
    if (tenant.active) {
      this.store.deactivateTenant(tenant.id);
    } else {
      this.store.activateTenant(tenant.id);
    }
  }

  onViewDetails(tenantId: string) {
    this.store.loadTenantById(tenantId);
  }
}