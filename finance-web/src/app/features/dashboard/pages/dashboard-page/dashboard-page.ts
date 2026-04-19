import { Component, inject, OnInit } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideAlertCircle, lucideGauge } from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import {
  DashboardAlertPanel,
  DashboardCurrentSubscriptionCard,
  DashboardMetricCard,
  DashboardTenantOverviewCard,
} from "../../components";
import { DashboardStore } from "../../store/dashboard.store";

@Component({
  selector: "app-dashboard-page",
  imports: [
    NgIcon,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmSkeletonImports,
    DashboardAlertPanel,
    DashboardCurrentSubscriptionCard,
    DashboardMetricCard,
    DashboardTenantOverviewCard,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideGauge,
    }),
  ],
  templateUrl: "./dashboard-page.html",
  styleUrl: "./dashboard-page.css",
})
export class DashboardPage implements OnInit {
  readonly store = inject(DashboardStore);

  ngOnInit(): void {
    void this.store.loadDashboard();
  }

  onRetry(): void {
    void this.store.reloadDashboard();
  }
}
