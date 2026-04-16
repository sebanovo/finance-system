import { Component, inject, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { HlmButtonImports } from "@shared/ui/button";
import { toast } from "@spartan-ng/brain/sonner";
import { PlanForm } from "../../components/plan-form/plan-form";
import { PlanUpsertFormValue } from "../../models/plan.type";
import { PlansStore } from "../../store/plan.store";

@Component({
  selector: "app-plan-create-page",
  imports: [HlmButtonImports, PlanForm],
  templateUrl: "./plan-create-page.html",
  styleUrl: "./plan-create-page.css",
})
export class PlanCreatePage implements OnInit {
  readonly store = inject(PlansStore);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.store.clearCreateError();
  }

  async onCreatePlan(formValue: PlanUpsertFormValue): Promise<void> {
    this.store.clearCreateError();

    const createdPlan = await this.store.createPlan(formValue);

    if (!createdPlan) {
      const errorMessage =
        this.store.createError()?.message || "No se pudo crear el plan.";

      toast("No se pudo crear el plan", {
        description: errorMessage,
      });

      return;
    }

    toast("Plan creado correctamente", {
      description: `${createdPlan.name} fue registrado con éxito.`,
      action: {
        label: "Undo",
        onClick: () => console.log("Undo create plan"),
      },
    });

    void this.router.navigate(["/app/plans"]);
  }

  onCancel(): void {
    this.store.clearCreateError();
    void this.router.navigate(["/app", "plans"]);
  }
}
