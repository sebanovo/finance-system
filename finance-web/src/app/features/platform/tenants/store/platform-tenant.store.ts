import { Injectable, inject, signal, computed } from '@angular/core';
import { PlatformTenantService } from '../services/platform-tenant.service';
import { PlatformTenantResponse, CreateTenantRequest } from '../models/platform-tenant.models';

@Injectable({
  providedIn: 'root'
})
export class PlatformTenantStore {
  private readonly tenantService = inject(PlatformTenantService);

  // 1. Estado Centralizado (Signals)
  readonly tenants = signal<PlatformTenantResponse[]>([]);
  readonly selectedTenant = signal<PlatformTenantResponse | null>(null);
  readonly isLoading = signal<boolean>(false);
  readonly error = signal<string | null>(null);

  // 2. Selectores Derivados (Computed)
  readonly activeTenantsCount = computed(() => this.tenants().filter(t => t.active).length);
  readonly totalTenantsCount = computed(() => this.tenants().length);

  // 3. Acciones (Orquestación del CRUD)

  loadTenants(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.tenantService.getTenants().subscribe({
      next: (response) => {
        this.tenants.set(response.data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar las empresas (Tenants).');
        this.isLoading.set(false);
      }
    });
  }

  loadTenantById(id: string): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.tenantService.getTenantById(id).subscribe({
      next: (response) => {
        this.selectedTenant.set(response.data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar el detalle de la empresa.');
        this.isLoading.set(false);
      }
    });
  }

  clearSelectedTenant(): void {
    this.selectedTenant.set(null);
  }

  createTenant(tenantData: CreateTenantRequest, onSuccess?: () => void): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.tenantService.createTenant(tenantData).subscribe({
      next: (response) => {
        // Agregamos el nuevo tenant a la lista actual reactivamente
        this.tenants.update(currentList => [...currentList, response.data]);
        this.isLoading.set(false);
        
        if (onSuccess) onSuccess();
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al crear la nueva empresa.');
        this.isLoading.set(false);
      }
    });
  }

  activateTenant(id: string): void {
    this.tenantService.activateTenant(id).subscribe({
      next: () => {
        this.tenants.update(currentList => 
          currentList.map(t => t.id === id ? { ...t, active: true, status: 'ACTIVE' } : t)
        );
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al activar la empresa.');
      }
    });
  }

  deactivateTenant(id: string): void {
    this.tenantService.deactivateTenant(id).subscribe({
      next: () => {
        this.tenants.update(currentList => 
          currentList.map(t => t.id === id ? { ...t, active: false, status: 'INACTIVE' } : t)
        );
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al desactivar/bloquear la empresa.');
      }
    });
  }
}