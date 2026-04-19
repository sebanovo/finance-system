import { Component, inject, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { HlmButtonImports } from "@shared/ui/button";
import { toast } from "@spartan-ng/brain/sonner";
import { TenantForm } from "../../components/tenant-form/tenant-form";
import { TenantUpsertFormValue } from "../../models/tenant.type";
import { TenantsStore } from "../../store/tenant.store";

@Component({
  selector: "app-tenant-create-page",
  imports: [HlmButtonImports, TenantForm],
  templateUrl: "./tenant-create-page.html",
  styleUrl: "./tenant-create-page.css",
})
export class TenantCreatePage implements OnInit {
  readonly store = inject(TenantsStore);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.store.clearCreateError();
  }

  async onCreateTenant(formValue: TenantUpsertFormValue): Promise<void> {
    this.store.clearCreateError();

    const createdTenant = await this.store.createTenant(formValue);

    if (!createdTenant) {
      const errorMessage =
        this.store.createError()?.message || "No se pudo crear el tenant.";

      toast("No se pudo crear el tenant", {
        description: errorMessage,
      });

      return;
    }

    toast("Tenant creado correctamente", {
      description: `${createdTenant.name} fue registrado con éxito.`,
      action: {
        label: "Undo",
        onClick: () => console.log("Undo create tenant"),
      },
    });

    void this.router.navigate(["/app/tenants"]);
  }

  onCancel(): void {
    this.store.clearCreateError();
    void this.router.navigate(["/app", "tenants"]);
  }
}
