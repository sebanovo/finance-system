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
import { RoleDetailDialog } from "../../components/role-detail-dialog/role-detail-dialog";
import { RoleTable } from "../../components/role-table/role-table";
import { Role } from "../../model/role.type";
import { RolesStore } from "../../store/role.store";

@Component({
  selector: "app-role-list-page",
  imports: [
    RoleTable,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmEmptyImports,
    HlmSkeletonImports,
    RoleDetailDialog,
    CardHeader,
    TableError,
    EmptyState,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideInbox,
      lucidePlus,
      lucideUsers,
    }),
  ],
  templateUrl: "./role-list-page.html",
  styleUrl: "./role-list-page.css",
})
export class RoleListPage implements OnInit {
  readonly store = inject(RolesStore);
  private readonly router = inject(Router);
  readonly viewDialogOpen = signal(false);

  ngOnInit(): void {
    void this.store.loadRoles();
  }

  onRetry(): void {
    void this.store.reloadRoles();
  }

  onCreateRole(): void {
    void this.router.navigate(["/app", "roles", "create"]);
  }

  onEditRole(roleId: string): void {
    void this.router.navigate(["/app", "roles", roleId, "edit"]);
  }

  async onActivateRole(role: Role): Promise<void> {
    await this.handleToggleRole(role);
  }

  async onDeactivateRole(role: Role): Promise<void> {
    await this.handleToggleRole(role);
  }

  private async handleToggleRole(role: Role): Promise<void> {
    this.store.clearToggleError();

    const updatedRole = await this.store.toggleRoleActiveState(role);

    if (!updatedRole) {
      toast("No se pudo actualizar el estado del rol", {
        description:
          this.store.toggleError()?.message || "Ocurrió un error inesperado.",
      });
      return;
    }

    toast(
      updatedRole.isActive
        ? "Rol activado correctamente"
        : "Rol desactivado correctamente",
      {
        description: `${updatedRole.name} fue actualizado.`,
      }
    );
  }

  async onViewRole(role: Role): Promise<void> {
    this.store.clearSelectedRoleError();
    this.store.clearSelectedRole();
    this.viewDialogOpen.set(true);

    const loaded = await this.store.loadRoleById(role.id);

    if (!loaded) {
      toast("No se pudo cargar el rol", {
        description:
          this.store.selectedRoleError()?.message ||
          "Ocurrió un error inesperado.",
      });
    }
  }

  onViewDialogOpenChange(isOpen: boolean): void {
    this.viewDialogOpen.set(isOpen);

    if (!isOpen) {
      this.store.clearSelectedRoleError();
      this.store.clearSelectedRole();
    }
  }
}
