import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import { toCreatePlanRequest, toPlan, toPlans } from "../adapters/plan.adapter";
import { Plan, PlanUpsertFormValue } from "../models/plan.type";
import { PlansApi } from "../services/plan.service";

@Injectable({
  providedIn: "root",
})
export class PlansStore {
  private readonly plansApi = inject(PlansApi);

  readonly plans = signal<Plan[]>([]);
  readonly loading = signal(false);
  readonly hasLoaded = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly hasError = computed(() => this.error() !== null);

  readonly submitting = signal(false);
  readonly createError = signal<AppHttpError | null>(null);
  readonly hasCreateError = computed(() => this.createError() !== null);

  readonly selectedPlan = signal<Plan | null>(null);
  readonly selectedPlanLoading = signal(false);
  readonly selectedPlanError = signal<AppHttpError | null>(null);
  readonly hasSelectedPlanError = computed(
    () => this.selectedPlanError() !== null
  );

  readonly togglingPlanIds = signal<string[]>([]);
  readonly toggleError = signal<AppHttpError | null>(null);
  readonly hasToggleError = computed(() => this.toggleError() !== null);

  readonly isEmpty = computed(
    () => this.hasLoaded() && !this.loading() && this.plans().length === 0
  );

  async loadPlans(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const planDtos = await firstValueFrom(this.plansApi.getPlans());
      this.plans.set(toPlans(planDtos));
      this.hasLoaded.set(true);
    } catch (error) {
      this.error.set(error as AppHttpError);
      this.plans.set([]);
      this.hasLoaded.set(true);
    } finally {
      this.loading.set(false);
    }
  }

  async createPlan(formValue: PlanUpsertFormValue): Promise<Plan | null> {
    this.submitting.set(true);
    this.createError.set(null);

    try {
      const payload = toCreatePlanRequest(formValue);
      const createdPlanDto = await firstValueFrom(
        this.plansApi.createPlan(payload)
      );
      const createdPlan = toPlan(createdPlanDto);

      this.plans.update(currentPlans => [createdPlan, ...currentPlans]);

      return createdPlan;
    } catch (error) {
      this.createError.set(error as AppHttpError);
      return null;
    } finally {
      this.submitting.set(false);
    }
  }

  async loadPlanById(planId: string): Promise<Plan | null> {
    this.selectedPlanLoading.set(true);
    this.selectedPlanError.set(null);
    this.selectedPlan.set(null);

    try {
      const planDto = await firstValueFrom(this.plansApi.getPlanById(planId));
      const plan = toPlan(planDto);

      this.selectedPlan.set(plan);

      return plan;
    } catch (error) {
      this.selectedPlanError.set(error as AppHttpError);
      this.selectedPlan.set(null);
      return null;
    } finally {
      this.selectedPlanLoading.set(false);
    }
  }

  async togglePlanActiveState(plan: Plan): Promise<Plan | null> {
    this.startTogglingPlan(plan.id);
    this.toggleError.set(null);

    try {
      const updatedPlanDto = plan.isActive
        ? await firstValueFrom(this.plansApi.deactivatePlan(plan.id))
        : await firstValueFrom(this.plansApi.activatePlan(plan.id));

      const updatedPlan = toPlan(updatedPlanDto);

      this.plans.update(currentPlans =>
        currentPlans.map(currentPlan =>
          currentPlan.id === updatedPlan.id ? updatedPlan : currentPlan
        )
      );

      if (this.selectedPlan()?.id === updatedPlan.id) {
        this.selectedPlan.set(updatedPlan);
      }

      return updatedPlan;
    } catch (error) {
      this.toggleError.set(error as AppHttpError);
      return null;
    } finally {
      this.finishTogglingPlan(plan.id);
    }
  }

  isPlanToggling(planId: string): boolean {
    return this.togglingPlanIds().includes(planId);
  }

  private startTogglingPlan(planId: string): void {
    this.togglingPlanIds.update(currentIds =>
      currentIds.includes(planId) ? currentIds : [...currentIds, planId]
    );
  }

  private finishTogglingPlan(planId: string): void {
    this.togglingPlanIds.update(currentIds =>
      currentIds.filter(currentId => currentId !== planId)
    );
  }

  clearError(): void {
    this.error.set(null);
  }

  clearCreateError(): void {
    this.createError.set(null);
  }

  reloadPlans(): Promise<void> {
    return this.loadPlans();
  }

  clearSelectedPlanError(): void {
    this.selectedPlanError.set(null);
  }

  clearSelectedPlan(): void {
    this.selectedPlan.set(null);
  }

  clearToggleError(): void {
    this.toggleError.set(null);
  }
}
