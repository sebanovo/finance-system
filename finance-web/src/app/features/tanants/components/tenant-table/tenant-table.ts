import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideEye, lucidePencil } from "@ng-icons/lucide";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmSwitchImports } from "@shared/ui/switch";
import { HlmTableImports } from "@shared/ui/table";
import { Tenant } from "../../models/tenant.type";

@Component({
  selector: "app-tenant-table",
  imports: [
    HlmTableImports,
    HlmBadgeImports,
    HlmButtonImports,
    NgIcon,
    HlmSwitchImports,
  ],
  providers: [provideIcons({ lucidePencil, lucideEye })],
  templateUrl: "./tenant-table.html",
  styleUrl: "./tenant-table.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantTable {
  readonly tenants = input.required<Tenant[]>();

  readonly viewTenant = output<Tenant>();
  readonly activateTenant = output<Tenant>();
  readonly deactivateTenant = output<Tenant>();
  readonly editTenant = output<string>();

  readonly togglingTenantIds = input<string[]>([]);

  onViewTenant(tenant: Tenant): void {
    console.log("click en onViewTenant", tenant);
    this.viewTenant.emit(tenant);
  }

  onEditTenant(tenantId: string): void {
    this.editTenant.emit(tenantId);
  }

  isTenantToggling(tenantId: string): boolean {
    return this.togglingTenantIds().includes(tenantId);
  }

  onToggleTenant(tenant: Tenant, value: boolean): void {
    if (value) {
      this.deactivateTenant.emit(tenant);
    } else {
      this.activateTenant.emit(tenant);
    }
  }
}
