import { Component, input } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideCircleAlert,
  lucideInfo,
  lucideTriangleAlert,
} from "@ng-icons/lucide";
import { HlmCardImports } from "@shared/ui/card";
import { DashboardAlert } from "../../models/dashboard.type";

@Component({
  selector: "app-dashboard-alert-panel",
  imports: [NgIcon, HlmCardImports],
  providers: [
    provideIcons({
      lucideCircleAlert,
      lucideInfo,
      lucideTriangleAlert,
    }),
  ],
  templateUrl: "./dashboard-alert-panel.html",
  styleUrl: "./dashboard-alert-panel.css",
})
export class DashboardAlertPanel {
  readonly alerts = input.required<DashboardAlert[]>();

  alertToneClass(tone: DashboardAlert["tone"]): string {
    if (tone === "critical") {
      return "border-red-200 bg-red-50 text-red-700";
    }

    if (tone === "warning") {
      return "border-amber-200 bg-amber-50 text-amber-700";
    }

    return "border-blue-200 bg-blue-50 text-blue-700";
  }

  iconName(tone: DashboardAlert["tone"]): string {
    if (tone === "critical") {
      return "lucideCircleAlert";
    }

    if (tone === "warning") {
      return "lucideTriangleAlert";
    }

    return "lucideInfo";
  }
}
