import { Component, inject, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { HlmButtonImports } from "@shared/ui/button";
import { toast } from "@spartan-ng/brain/sonner";
import { RoleForm } from "../../components/role-form/role-form";
import { RoleUpsertFormValue } from "../../model/role.type";
import { RolesStore } from "../../store/role.store";

@Component({
  selector: "app-role-create-page",
  imports: [HlmButtonImports, RoleForm],
  templateUrl: "./role-create-page.html",
  styleUrl: "./role-create-page.css",
})
export class RoleCreatePage implements OnInit {
  readonly store = inject(RolesStore);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.store.clearCreateError();
  }

  async onCreateRole(formValue: RoleUpsertFormValue): Promise<void> {
    const created = await this.store.createRole(formValue);

    if (!created) {
      toast("No se pudo crear el rol", {
        description: this.store.createError()?.message || "Error inesperado",
      });
      return;
    }

    toast("Rol creado correctamente", {
      description: `${created.name} fue registrado.`,
    });

    void this.router.navigate(["/app/roles"]);
  }

  onCancel(): void {
    void this.router.navigate(["/app/roles"]);
  }
}
