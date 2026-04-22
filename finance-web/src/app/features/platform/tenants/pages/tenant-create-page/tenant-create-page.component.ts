import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { PlatformTenantStore } from '../../store/platform-tenant.store';
import { TenantCreateFormComponent } from '../../components/tenant-create-form/tenant-create-form.component';
import { CreateTenantRequest } from '../../models/platform-tenant.models';

@Component({
  selector: 'app-tenant-create-page',
  standalone: true,
  imports: [TenantCreateFormComponent, RouterLink],
  template: `
    <section class="max-w-3xl mx-auto p-6">
      
      <header class="mb-6 flex items-center gap-4">
        <a routerLink="/platform/tenants" class="text-gray-500 hover:text-gray-700 bg-gray-100 p-2 rounded-full transition">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z" clip-rule="evenodd" />
          </svg>
        </a>
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Registrar Nueva Empresa</h1>
          <p class="text-sm text-gray-500">Aprovisiona un nuevo tenant en el sistema financiero.</p>
        </div>
      </header>

      @if (store.error()) {
        <div class="p-4 mb-6 text-sm text-red-800 rounded-lg bg-red-50 border border-red-200" role="alert">
          {{ store.error() }}
        </div>
      }

      <article class="bg-white rounded-lg shadow-md p-6 border border-gray-100">
        <app-tenant-create-form
          [isLoading]="store.isLoading()"
          (submitForm)="onCreate($event)">
        </app-tenant-create-form>
      </article>

    </section>
  `
})
export class TenantCreatePageComponent {
  readonly store = inject(PlatformTenantStore);
  private readonly router = inject(Router);

  onCreate(tenantData: CreateTenantRequest) {
    this.store.createTenant(tenantData, () => {
      this.router.navigate(['/platform/tenants']);
    });
  }
}