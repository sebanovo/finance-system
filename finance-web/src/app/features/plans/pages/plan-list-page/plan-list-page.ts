import { Component, inject, OnInit, signal } from "@angular/core";
import { Router } from "@angular/router";
import { provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideInbox,
  lucidePlus,
  lucideUsers,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmEmptyImports } from "@shared/ui/empty";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { CardHeader } from "../../../../shared/custom-components/card-header/card-header";
import { EmptyState } from "../../../../shared/custom-components/empty-state/empty-state";
import { TableError } from "../../../../shared/custom-components/table-error/table-error";
import { PlanDetailDialog } from "../../components/plan-detail-dialog/plan-detail-dialog";
import { PlanTable } from "../../components/plan-table/plan-table";
import { Plan } from "../../models/plan.type";
import { PlansStore } from "../../store/plan.store";

@Component({
  selector: "app-plan-list-page",
  imports: [
    PlanTable,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmEmptyImports,
    HlmSkeletonImports,
    PlanDetailDialog,
    CardHeader,
    TableError,
    EmptyState,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideInbox,
      lucidePlus,
      lucideUsers,
    }),
  ],
  templateUrl: "./plan-list-page.html",
  styleUrl: "./plan-list-page.css",
})
export class PlanListPage implements OnInit {
  readonly store = inject(PlansStore);
  private readonly router = inject(Router);
  readonly viewDialogOpen = signal(false);

  ngOnInit(): void {
    console.log("init", this.store.plans);
    void this.store.loadPlans();
  }

  onRetry(): void {
    void this.store.reloadPlans();
  }

  onCreatePlan(): void {
    void this.router.navigate(["/app", "plans", "create"]);
  }

  onEditPlan(planId: string): void {
    void this.router.navigate(["/app", "plans", planId, "edit"]);
  }

  async onActivatePlan(plan: Plan): Promise<void> {
    await this.handleTogglePlan(plan);
  }

  async onDeactivatePlan(plan: Plan): Promise<void> {
    await this.handleTogglePlan(plan);
  }

  private async handleTogglePlan(plan: Plan): Promise<void> {
    this.store.clearToggleError();

    const updatedPlan = await this.store.togglePlanActiveState(plan);
    console.log(this.store.toggleError());

    if (!updatedPlan) {
      toast("No se pudo actualizar el estado del plan", {
        description:
          this.store.toggleError()?.message || "Ocurrió un error inesperado.",
      });
      return;
    }

    toast(
      updatedPlan.isActive
        ? "Plan activado correctamente"
        : "Plan desactivado correctamente",
      {
        description: `${updatedPlan.name} fue actualizado.`,
        action: {
          label: "Undo",
          onClick: () => console.log("Undo toggle plan"),
        },
      }
    );
  }

  async onViewPlan(plan: Plan): Promise<void> {
    this.store.clearSelectedPlanError();
    this.store.clearSelectedPlan();
    this.viewDialogOpen.set(true);

    const loadedPlan = await this.store.loadPlanById(plan.id);

    if (!loadedPlan) {
      toast("No se pudo cargar el plan", {
        description:
          this.store.selectedPlanError()?.message ||
          "Ocurrió un error inesperado.",
      });
    }
  }

  onViewDialogOpenChange(isOpen: boolean): void {
    this.viewDialogOpen.set(isOpen);

    if (!isOpen) {
      this.store.clearSelectedPlanError();
      this.store.clearSelectedPlan();
    }
  }
}
