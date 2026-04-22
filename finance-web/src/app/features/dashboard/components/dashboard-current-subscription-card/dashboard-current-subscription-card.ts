import { Component, inject, input } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideBadgeCheck,
  lucideCalendarClock,
  lucideLayers3,
  lucideUsers,
} from "@ng-icons/lucide";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmCardImports } from "@shared/ui/card";
import { AuthService } from "../../../auth/services/auth.service";
import {
  DashboardCurrentSubscriptionOverview,
  DashboardSectionState,
} from "../../models/dashboard.type";

@Component({
  selector: "app-dashboard-current-subscription-card",
  imports: [NgIcon, HlmBadgeImports, HlmCardImports],
  providers: [
    provideIcons({
      lucideBadgeCheck,
      lucideCalendarClock,
      lucideLayers3,
      lucideUsers,
    }),
  ],
  templateUrl: "./dashboard-current-subscription-card.html",
  styleUrl: "./dashboard-current-subscription-card.css",
})
export class DashboardCurrentSubscriptionCard {
  private readonly admin = inject(AuthService);
  readonly section =
    input.required<
      DashboardSectionState<DashboardCurrentSubscriptionOverview>
    >();

  protected isAdmin(): boolean {
    return this.admin.isAdmin();
  }
}
