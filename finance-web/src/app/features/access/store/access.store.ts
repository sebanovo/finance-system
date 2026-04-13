import { inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import { toPermissions } from "../adapters/access.adapter";
import { Permission } from "../models/access.model";
import { AccessApi } from "../services/access.service";

@Injectable({
  providedIn: "root",
})
export class AccessStore {
  private readonly accessApi = inject(AccessApi);

  readonly permissions = signal<Permission[]>([]);
  readonly loading = signal(false);
  readonly hasLoaded = signal(false);
  readonly error = signal<AppHttpError | null>(null);

  async loadPermisssions(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const permissionDtos = await firstValueFrom(
        this.accessApi.getPermissions()
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
}
