import { Component, input } from "@angular/core";
import { HlmCardImports } from "@shared/ui/card";
import { DashboardMetric } from "../../models/dashboard.type";

@Component({
  selector: "app-dashboard-metric-card",
  imports: [HlmCardImports],
  templateUrl: "./dashboard-metric-card.html",
  styleUrl: "./dashboard-metric-card.css",
})
export class DashboardMetricCard {
  readonly metric = input.required<DashboardMetric>();

  metricToneClass(): string {
    const tone = this.metric().tone;

    if (tone === "success") {
      return "text-emerald-600";
    }

    if (tone === "warning") {
      return "text-amber-600";
    }

    return "text-foreground";
  }
}
