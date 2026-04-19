import { Component, computed, inject, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideLoaderCircle,
  lucideUserPen,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { PlanForm } from "../../components/plan-form/plan-form";
import { PlanUpsertFormValue } from "../../models/plan.type";
import { PlansStore } from "../../store/plan.store";

@Component({
  selector: "app-plan-edit-page",
  imports: [
    NgIcon,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmSkeletonImports,
    PlanForm,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideLoaderCircle,
      lucideUserPen,
    }),
  ],
  templateUrl: "./plan-edit-page.html",
  styleUrl: "./plan-edit-page.css",
})
export class PlanEditPage implements OnInit {
  readonly store = inject(PlansStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private planId = "";

  async ngOnInit(): Promise<void> {
    this.store.clearSelectedPlanError();
    this.store.clearSelectedPlan();

    this.planId = this.route.snapshot.paramMap.get("id") ?? "";

    if (!this.planId) {
      void this.router.navigate(["/app/plans"]);
      return;
    }

    const plan = await this.store.loadPlanById(this.planId);

    if (!plan) {
      toast("No se pudo cargar el plan", {
        description:
          this.store.selectedPlanError()?.message ||
          "Ocurrió un error inesperado.",
      });
    }
  }

  // SOLO cuando tengas update en backend
  async onUpdatePlan(formValue: PlanUpsertFormValue): Promise<void> {
    console.log("Update pendiente", formValue);

    toast("Funcionalidad no disponible", {
      description: "Aún no existe endpoint para actualizar planes.",
    });
  }

  onCancel(): void {
    void this.router.navigate(["/app/plans"]);
  }

  readonly initialFormValue = computed<PlanUpsertFormValue | null>(() => {
    const plan = this.store.selectedPlan();

    if (!plan) {
      return null;
    }

    return {
      code: plan.code,
      name: plan.name,
      description: plan.description,
      maxUsers: plan.maxUsers,
      maxRoles: plan.maxRoles,
      planType: plan.planType,
      trialDays: plan.trialDays,
    };
  });
}
