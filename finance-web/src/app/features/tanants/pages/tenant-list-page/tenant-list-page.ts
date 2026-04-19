import { Component, inject, OnInit, signal } from "@angular/core";
import { Router } from "@angular/router";
import { provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideInbox,
  lucidePlus,
  lucideUsers,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmEmptyImports } from "@shared/ui/empty";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { CardHeader } from "../../../../shared/custom-components/card-header/card-header";
import { EmptyState } from "../../../../shared/custom-components/empty-state/empty-state";
import { TableError } from "../../../../shared/custom-components/table-error/table-error";
import { SubscriptionManageDrawer } from "../../../subscription-assignment/components/subscription-manage-drawer/subscription-manage-drawer";
import { AssignSubscriptionTenantContext } from "../../../subscription-assignment/model/subscription-assignment.type";
import { TenantDetailDialog } from "../../components/tenant-detail-dialog/tenant-detail-dialog";
import { TenantTable } from "../../components/tenant-table/tenant-table";
import { Tenant } from "../../models/tenant.type";
import { TenantsStore } from "../../store/tenant.store";

@Component({
  selector: "app-tenant-list-page",
  imports: [
    TenantTable,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmEmptyImports,
    HlmSkeletonImports,
    TenantDetailDialog,
    CardHeader,
    TableError,
    EmptyState,
    SubscriptionManageDrawer,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideInbox,
      lucidePlus,
      lucideUsers,
    }),
  ],
  templateUrl: "./tenant-list-page.html",
  styleUrl: "./tenant-list-page.css",
})
export class TenantListPage implements OnInit {
  readonly store = inject(TenantsStore);
  private readonly router = inject(Router);
  readonly viewDialogOpen = signal(false);

  readonly assignSubscriptionDrawerOpen = signal(false);
  readonly selectedTenantForSubscription =
    signal<AssignSubscriptionTenantContext | null>(null);

  ngOnInit(): void {
    console.log("init", this.store.tenants);
    void this.store.loadTenants();
  }

  onRetry(): void {
    void this.store.reloadTenants();
  }

  onCreateTenant(): void {
    void this.router.navigate(["/app", "tenants", "create"]);
  }

  onEditTenant(tenantId: string): void {
    void this.router.navigate(["/app", "tenants", tenantId, "edit"]);
  }

  async onActivateTenant(tenant: Tenant): Promise<void> {
    await this.handleToggleTenant(tenant);
  }

  async onDeactivateTenant(tenant: Tenant): Promise<void> {
    await this.handleToggleTenant(tenant);
  }

  private async handleToggleTenant(tenant: Tenant): Promise<void> {
    this.store.clearToggleError();

    const updatedTenant = await this.store.toggleTenantActiveState(tenant);

    if (!updatedTenant) {
      toast("No se pudo actualizar el estado del tenant", {
        description:
          this.store.toggleError()?.message || "Ocurrió un error inesperado.",
      });
      return;
    }

    toast(
      updatedTenant.isActive
        ? "Tenant activado correctamente"
        : "Tenant desactivado correctamente",
      {
        description: `${updatedTenant.name} fue actualizado.`,
        action: {
          label: "Undo",
          onClick: () => console.log("Undo toggle tenant"),
        },
      }
    );
  }

  async onViewTenant(tenant: Tenant): Promise<void> {
    this.store.clearSelectedTenantError();
    this.store.clearSelectedTenant();
    this.viewDialogOpen.set(true);

    const loadedTenant = await this.store.loadTenantById(tenant.id);

    if (!loadedTenant) {
      toast("No se pudo cargar el tenant", {
        description:
          this.store.selectedTenantError()?.message ||
          "Ocurrió un error inesperado.",
      });
    }
  }

  onViewDialogOpenChange(isOpen: boolean): void {
    this.viewDialogOpen.set(isOpen);

    if (!isOpen) {
      this.store.clearSelectedTenantError();
      this.store.clearSelectedTenant();
    }
  }

  onAssignSubscription(tenant: Tenant): void {
    this.selectedTenantForSubscription.set({
      id: tenant.id,
      name: tenant.name,
      slug: tenant.slug,
    });

    this.assignSubscriptionDrawerOpen.set(true);
  }

  onAssignSubscriptionDrawerOpenChange(isOpen: boolean): void {
    this.assignSubscriptionDrawerOpen.set(isOpen);

    if (!isOpen) {
      this.selectedTenantForSubscription.set(null);
    }
  }

  onSubscriptionAssigned(): void {
    toast("Suscripción asignada", {
      description: "La suscripción fue registrada correctamente.",
    });
  }
}
