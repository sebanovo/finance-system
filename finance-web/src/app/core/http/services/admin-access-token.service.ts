import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class AdminAccessTokenService {
  private readonly adminKey = "superadmin";

  getSuperAdmin(): string | null {
    const value = localStorage.getItem(this.adminKey);

    if (!value) {
      return null;
    }

    const admin = value.trim();
    return admin.length > 0 ? admin : null;
  }

  setSuperAdmin(): void {
    const value = "SuperAdmin";

    if (!value) {
      this.clearSuperAdmin();
      return;
    }

    localStorage.setItem(this.adminKey, value);
  }

  clearSuperAdmin(): void {
    localStorage.removeItem(this.adminKey);
  }
}
