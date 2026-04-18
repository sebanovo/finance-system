import { DatePipe } from "@angular/common";
import { Component, inject, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideArrowLeft,
  lucideBuilding2,
  lucidePencil,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { TenantsStore } from "../../store/tenant.store";

@Component({
  selector: "app-tenant-detail-page",
  imports: [
    NgIcon,
    DatePipe,
    HlmAlertImports,
    HlmBadgeImports,
    HlmButtonImports,
    HlmCardImports,
    HlmSkeletonImports,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideBuilding2,
      lucideArrowLeft,
      lucidePencil,
    }),
  ],
  templateUrl: "./tenant-detail-page.html",
  styleUrl: "./tenant-detail-page.css",
})
export class TenantDetailPage implements OnInit {
  readonly store = inject(TenantsStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private tenantId = "";

  async ngOnInit(): Promise<void> {
    this.store.clearSelectedTenantError();
    this.store.clearSelectedTenant();

    this.tenantId = this.route.snapshot.paramMap.get("id") ?? "";

    if (!this.tenantId) {
      void this.router.navigate(["/app", "tenants"]);
      return;
    }

    const tenant = await this.store.loadTenantById(this.tenantId);

    if (!tenant) {
      toast("No se pudo cargar el tenant", {
        description:
          this.store.selectedTenantError()?.message ||
          "Ocurrió un error inesperado.",
      });
    }
  }

  onBack(): void {
    this.store.clearSelectedTenantError();
    this.store.clearSelectedTenant();
    void this.router.navigate(["/app", "tenants"]);
  }

  onEditTenant(tenantId: string): void {
    void this.router.navigate(["/app", "tenants", tenantId, "edit"]);
  }
}
