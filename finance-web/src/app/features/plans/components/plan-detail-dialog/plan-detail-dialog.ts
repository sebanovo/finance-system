import { DatePipe } from "@angular/common";
import { Component, input, output } from "@angular/core";
import { provideIcons } from "@ng-icons/core";
import { lucideAlertCircle } from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmDialogImports } from "@shared/ui/dialog";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { AppHttpError } from "../../../../core/http/models/app-http-error.model";
import { Plan } from "../../models/plan.type";

@Component({
  selector: "app-plan-detail-dialog",
  imports: [
    DatePipe,
    HlmAlertImports,
    HlmBadgeImports,
    HlmButtonImports,
    HlmCardImports,
    HlmDialogImports,
    HlmSkeletonImports,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
    }),
  ],
  templateUrl: "./plan-detail-dialog.html",
  styleUrl: "./plan-detail-dialog.css",
})
export class PlanDetailDialog {
  readonly open = input.required<boolean>();
  readonly plan = input<Plan | null>(null);
  readonly loading = input(false);
  readonly error = input<AppHttpError | null>(null);

  readonly openChange = output<boolean>();
  readonly editPlan = output<string>();

  onOpenChange(isOpen: boolean): void {
    this.openChange.emit(isOpen);
  }

  onEditPlan(planId: string): void {
    this.editPlan.emit(planId);
  }

  onClosed(): void {
    this.openChange.emit(false);
  }
}
