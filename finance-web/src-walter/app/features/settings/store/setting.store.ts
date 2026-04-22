import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import {
  toTenantSettings,
  toUpdateTenantSettingsRequest,
} from "../adapters/setting.adapter";
import {
  TenantSettings,
  TenantSettingsFormValue,
} from "../models/setting.type";
import { TenantSettingsApi } from "../services/setting.service";

@Injectable({
  providedIn: "root",
})
export class TenantSettingsStore {
  private readonly tenantSettingsApi = inject(TenantSettingsApi);

  readonly settings = signal<TenantSettings | null>(null);

  readonly loading = signal(false);
  readonly hasLoaded = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly hasError = computed(() => this.error() !== null);

  readonly submitting = signal(false);
  readonly updateError = signal<AppHttpError | null>(null);
  readonly hasUpdateError = computed(() => this.updateError() !== null);

  async loadTenantSettings(): Promise<TenantSettings | null> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const dto = await firstValueFrom(
        this.tenantSettingsApi.getTenantSettings()
      );
      const settings = toTenantSettings(dto);

      this.settings.set(settings);
      this.hasLoaded.set(true);

      return settings;
    } catch (error) {
      this.error.set(error as AppHttpError);
      this.settings.set(null);
      this.hasLoaded.set(true);
      return null;
    } finally {
      this.loading.set(false);
    }
  }

  async updateTenantSettings(
    formValue: TenantSettingsFormValue
  ): Promise<TenantSettings | null> {
    this.submitting.set(true);
    this.updateError.set(null);

    try {
      const payload = toUpdateTenantSettingsRequest(formValue);
      const dto = await firstValueFrom(
        this.tenantSettingsApi.updateTenantSettings(payload)
      );
      const updatedSettings = toTenantSettings(dto);

      this.settings.set(updatedSettings);

      return updatedSettings;
    } catch (error) {
      this.updateError.set(error as AppHttpError);
      return null;
    } finally {
      this.submitting.set(false);
    }
  }

  reloadTenantSettings(): Promise<TenantSettings | null> {
    return this.loadTenantSettings();
  }

  clearError(): void {
    this.error.set(null);
  }

  clearUpdateError(): void {
    this.updateError.set(null);
  }

  clearSettings(): void {
    this.settings.set(null);
  }
}
