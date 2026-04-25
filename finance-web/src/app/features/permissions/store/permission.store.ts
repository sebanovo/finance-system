import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import { toPermissions } from "../adapters/permission.adapter";
import { Permission } from "../model/permission.type";
import { PermissionsApi } from "../services/permission.service";

@Injectable({
  providedIn: "root",
})
export class PermissionsStore {
  private readonly permissionsApi = inject(PermissionsApi);

  readonly permissions = signal<Permission[]>([]);
  readonly loading = signal(false);
  readonly hasLoaded = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly hasError = computed(() => this.error() !== null);

  readonly isEmpty = computed(
    () => this.hasLoaded() && !this.loading() && this.permissions().length === 0
  );

  async loadPermissions(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const permissionDtos = await firstValueFrom(
        this.permissionsApi.getPermissions()
      );
      this.permissions.set(toPermissions(permissionDtos));
      this.hasLoaded.set(true);
    } catch (error) {
      this.error.set(error as AppHttpError);
      this.permissions.set([]);
      this.hasLoaded.set(true);
    } finally {
      this.loading.set(false);
    }
  }

  reloadPermissions(): Promise<void> {
    return this.loadPermissions();
  }

  clearError(): void {
    this.error.set(null);
  }
}
