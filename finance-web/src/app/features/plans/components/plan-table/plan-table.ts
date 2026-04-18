import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideEye, lucidePencil } from "@ng-icons/lucide";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmSwitchImports } from "@shared/ui/switch";
import { HlmTableImports } from "@shared/ui/table";
import { Plan } from "../../models/plan.type";

@Component({
  selector: "app-plan-table",
  imports: [
    HlmTableImports,
    HlmBadgeImports,
    HlmButtonImports,
    NgIcon,
    HlmSwitchImports,
  ],
  providers: [provideIcons({ lucidePencil, lucideEye })],
  templateUrl: "./plan-table.html",
  styleUrl: "./plan-table.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlanTable {
  readonly plans = input.required<Plan[]>();

  readonly viewPlan = output<Plan>();
  readonly activatePlan = output<Plan>();
  readonly deactivatePlan = output<Plan>();
  readonly editPlan = output<string>();

  readonly togglingPlanIds = input<string[]>([]);

  onViewPlan(plan: Plan): void {
    console.log("click en onViewPlan", plan);
    this.viewPlan.emit(plan);
  }

  onEditPlan(planId: string): void {
    this.editPlan.emit(planId);
  }

  isPlanToggling(planId: string): boolean {
    return this.togglingPlanIds().includes(planId);
  }

  onTogglePlan(plan: Plan, value: boolean): void {
    if (value) {
      this.deactivatePlan.emit(plan);
    } else {
      this.activatePlan.emit(plan);
    }
  }
}
