import { Component, inject, input } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideBuilding2,
  lucideGlobe,
  lucideMail,
  lucidePhone,
} from "@ng-icons/lucide";
import { HlmCardImports } from "@shared/ui/card";
import { AuthService } from "../../../auth/services/auth.service";
import {
  DashboardSectionState,
  DashboardTenantOverview,
} from "../../models/dashboard.type";

@Component({
  selector: "app-dashboard-tenant-overview-card",
  imports: [NgIcon, HlmCardImports],
  providers: [
    provideIcons({
      lucideBuilding2,
      lucideGlobe,
      lucideMail,
      lucidePhone,
    }),
  ],
  templateUrl: "./dashboard-tenant-overview-card.html",
  styleUrl: "./dashboard-tenant-overview-card.css",
})
export class DashboardTenantOverviewCard {
  readonly section =
    input.required<DashboardSectionState<DashboardTenantOverview>>();
  private readonly admin = inject(AuthService);

  protected isAdmin(): boolean {
    return this.admin.isAdmin();
  }
}
