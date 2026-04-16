import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";

import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import {
  toCreateTenantRequest,
  toTenant,
  toTenants,
} from "../adapters/tenant.adapter";
import { Tenant, TenantUpsertFormValue } from "../models/tenant.type";
import { TenantsApi } from "../services/tenant.service";

@Injectable({
  providedIn: "root",
})
export class TenantsStore {
  private readonly tenantsApi = inject(TenantsApi);

  readonly tenants = signal<Tenant[]>([]);
  readonly loading = signal(false);
  readonly hasLoaded = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly hasError = computed(() => this.error() !== null);

  readonly submitting = signal(false);
  readonly createError = signal<AppHttpError | null>(null);
  readonly hasCreateError = computed(() => this.createError() !== null);

  readonly selectedTenant = signal<Tenant | null>(null);
  readonly selectedTenantLoading = signal(false);
  readonly selectedTenantError = signal<AppHttpError | null>(null);
  readonly hasSelectedTenantError = computed(
    () => this.selectedTenantError() !== null
  );

  readonly togglingTenantIds = signal<string[]>([]);
  readonly toggleError = signal<AppHttpError | null>(null);
  readonly hasToggleError = computed(() => this.toggleError() !== null);

  readonly isEmpty = computed(
    () => this.hasLoaded() && !this.loading() && this.tenants().length === 0
  );

  async loadTenants(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const tenantDtos = await firstValueFrom(this.tenantsApi.getTenants());
      this.tenants.set(toTenants(tenantDtos));
      this.hasLoaded.set(true);
    } catch (error) {
      this.error.set(error as AppHttpError);
      this.tenants.set([]);
      this.hasLoaded.set(true);
    } finally {
      this.loading.set(false);
    }
  }

  async createTenant(formValue: TenantUpsertFormValue): Promise<Tenant | null> {
    this.submitting.set(true);
    this.createError.set(null);

    try {
      const payload = toCreateTenantRequest(formValue);
      const createdTenantDto = await firstValueFrom(
        this.tenantsApi.createTenant(payload)
      );
      const createdTenant = toTenant(createdTenantDto);

      this.tenants.update(currentTenants => [createdTenant, ...currentTenants]);

      return createdTenant;
    } catch (error) {
      this.createError.set(error as AppHttpError);
      return null;
    } finally {
      this.submitting.set(false);
    }
  }

  async loadTenantById(tenantId: string): Promise<Tenant | null> {
    this.selectedTenantLoading.set(true);
    this.selectedTenantError.set(null);
    this.selectedTenant.set(null);

    try {
      const tenantDto = await firstValueFrom(
        this.tenantsApi.getTenantById(tenantId)
      );
      const tenant = toTenant(tenantDto);

      this.selectedTenant.set(tenant);

      return tenant;
    } catch (error) {
      this.selectedTenantError.set(error as AppHttpError);
      this.selectedTenant.set(null);
      return null;
    } finally {
      this.selectedTenantLoading.set(false);
    }
  }

  async toggleTenantActiveState(tenant: Tenant): Promise<Tenant | null> {
    this.startTogglingTenant(tenant.id);
    this.toggleError.set(null);

    try {
      const updatedTenantDto = tenant.isActive
        ? await firstValueFrom(this.tenantsApi.deactivateTenant(tenant.id))
        : await firstValueFrom(this.tenantsApi.activateTenant(tenant.id));

      const updatedTenant = toTenant(updatedTenantDto);

      this.tenants.update(currentTenants =>
        currentTenants.map(currentTenant =>
          currentTenant.id === updatedTenant.id ? updatedTenant : currentTenant
        )
      );

      if (this.selectedTenant()?.id === updatedTenant.id) {
        this.selectedTenant.set(updatedTenant);
      }

      return updatedTenant;
    } catch (error) {
      this.toggleError.set(error as AppHttpError);
      return null;
    } finally {
      this.finishTogglingTenant(tenant.id);
    }
  }

  isTenantToggling(tenantId: string): boolean {
    return this.togglingTenantIds().includes(tenantId);
  }

  private startTogglingTenant(tenantId: string): void {
    this.togglingTenantIds.update(currentIds =>
      currentIds.includes(tenantId) ? currentIds : [...currentIds, tenantId]
    );
  }

  private finishTogglingTenant(tenantId: string): void {
    this.togglingTenantIds.update(currentIds =>
      currentIds.filter(currentId => currentId !== tenantId)
    );
  }

  clearError(): void {
    this.error.set(null);
  }

  clearCreateError(): void {
    this.createError.set(null);
  }

  reloadTenants(): Promise<void> {
    return this.loadTenants();
  }

  clearSelectedTenantError(): void {
    this.selectedTenantError.set(null);
  }

  clearSelectedTenant(): void {
    this.selectedTenant.set(null);
  }

  clearToggleError(): void {
    this.toggleError.set(null);
  }
}
