import { Component, computed, inject, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideLoaderCircle,
  lucidePencil,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { RoleForm } from "../../components/role-form/role-form";
import { RoleUpsertFormValue } from "../../model/role.type";
import { RolesStore } from "../../store/role.store";

@Component({
  selector: "app-role-edit-page",
  imports: [
    NgIcon,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmSkeletonImports,
    RoleForm,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideLoaderCircle,
      lucidePencil,
    }),
  ],
  templateUrl: "./role-edit-page.html",
  styleUrl: "./role-edit-page.css",
})
export class RoleEditPage implements OnInit {
  readonly store = inject(RolesStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private roleId = "";

  async ngOnInit(): Promise<void> {
    this.store.clearSelectedRole();
    this.store.clearUpdateError();

    this.roleId = this.route.snapshot.paramMap.get("id") ?? "";

    if (!this.roleId) {
      void this.router.navigate(["/app/roles"]);
      return;
    }

    await this.store.loadRoleById(this.roleId);
  }

  async onUpdateRole(formValue: RoleUpsertFormValue): Promise<void> {
    const updated = await this.store.updateRole(this.roleId, formValue);

    if (!updated) {
      toast("No se pudo actualizar el rol", {
        description: this.store.updateError()?.message || "Error inesperado",
      });
      return;
    }

    toast("Rol actualizado correctamente", {
      description: `${updated.name} actualizado.`,
    });

    void this.router.navigate(["/app/roles"]);
  }

  onCancel(): void {
    void this.router.navigate(["/app/roles"]);
  }

  readonly initialFormValue = computed<RoleUpsertFormValue | null>(() => {
    const role = this.store.selectedRole();
    if (!role) {
      return null;
    }

    return {
      name: role.name,
      description: role.description,
      permissionCodes: role.permissionCodes,
    };
  });
}
