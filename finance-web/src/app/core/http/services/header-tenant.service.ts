import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class HeaderTenantService {
  private readonly tenantKey = "tenant";

  getTenant(): string | null {
    const value = localStorage.getItem(this.tenantKey);

    if (!value) {
      return null;
    }

    const tenant = value.trim();
    return tenant.length > 0 ? tenant : null;
  }

  setTenant(tenant: string): void {
    const value = tenant.trim();

    if (!value) {
      this.clearTenant();
      return;
    }

    localStorage.setItem(this.tenantKey, value);
  }

  clearTenant(): void {
    localStorage.removeItem(this.tenantKey);
  }
}
