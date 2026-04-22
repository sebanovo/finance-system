import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import { PlansStore } from "../../plans/store/plan.store";
import { SubscriptionsStore } from "../../subscriptions/store/subscription.store";
import {
  toAssignedSubscription,
  toAssignSubscriptionRequest,
} from "../adapters/subscription-assignment.adapter";
import {
  AssignedSubscription,
  AssignSubscriptionFormValue,
} from "../model/subscription-assignment.type";
import { AssignSubscriptionApi } from "../services/subscription-assignment.service";

@Injectable({
  providedIn: "root",
})
export class AssignSubscriptionStore {
  private readonly assignSubscriptionApi = inject(AssignSubscriptionApi);
  readonly plansStore = inject(PlansStore);
  readonly subscriptionsStore = inject(SubscriptionsStore);

  readonly assignedSubscription = signal<AssignedSubscription | null>(null);

  readonly loading = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly hasError = computed(() => this.error() !== null);

  readonly submitting = signal(false);
  readonly submitError = signal<AppHttpError | null>(null);
  readonly hasSubmitError = computed(() => this.submitError() !== null);

  readonly selectedPlanCode = signal<string>("");
  readonly currentPlanCode = signal<string | null>(null);

  readonly isLoading = computed(
    () =>
      this.loading() ||
      this.plansStore.loading() ||
      this.subscriptionsStore.loading()
  );

  readonly availablePlans = computed(() => this.plansStore.plans());

  readonly isEmpty = computed(
    () =>
      !this.isLoading() &&
      !this.hasError() &&
      this.availablePlans().length === 0
  );

  readonly selectedPlan = computed(
    () =>
      this.availablePlans().find(
        plan => plan.code === this.selectedPlanCode()
      ) ?? null
  );

  readonly hasCurrentPlan = computed(() => this.currentPlanCode() !== null);

  readonly isDirty = computed(() => {
    return this.selectedPlanCode() !== (this.currentPlanCode() ?? "");
  });

  async loadForAssign(tenantId: string): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    this.submitError.set(null);
    this.assignedSubscription.set(null);
    this.selectedPlanCode.set("");
    this.currentPlanCode.set(null);

    try {
      if (!this.plansStore.hasLoaded()) {
        await this.plansStore.loadPlans();
      }

      if (!this.subscriptionsStore.hasLoaded()) {
        await this.subscriptionsStore.loadSubscriptions();
      }

      const currentSubscription = this.subscriptionsStore
        .subscriptions()
        .find(
          subscription =>
            subscription.tenantId === tenantId &&
            subscription.isCurrentSubscription
        );

      const currentPlanCode = currentSubscription?.planCode ?? null;

      this.currentPlanCode.set(currentPlanCode);
      this.selectedPlanCode.set(currentPlanCode ?? "");
    } catch (error) {
      this.error.set(error as AppHttpError);
      this.assignedSubscription.set(null);
      this.selectedPlanCode.set("");
      this.currentPlanCode.set(null);
    } finally {
      this.loading.set(false);
    }
  }

  selectPlan(planCode: string): void {
    this.selectedPlanCode.set(planCode);
  }

  async assign(
    tenantId: string,
    formValue: AssignSubscriptionFormValue
  ): Promise<AssignedSubscription | null> {
    this.submitting.set(true);
    this.submitError.set(null);

    try {
      const payload = toAssignSubscriptionRequest(tenantId, formValue);
      const dto = await firstValueFrom(
        this.assignSubscriptionApi.assignSubscription(payload)
      );
      const assignedSubscription = toAssignedSubscription(dto);

      this.assignedSubscription.set(assignedSubscription);
      this.currentPlanCode.set(assignedSubscription.planCode);
      this.selectedPlanCode.set(assignedSubscription.planCode);

      this.subscriptionsStore.subscriptions.update(currentSubscriptions =>
        currentSubscriptions.map(subscription =>
          subscription.tenantId === tenantId &&
          subscription.isCurrentSubscription
            ? { ...subscription, isCurrentSubscription: false }
            : subscription
        )
      );

      this.subscriptionsStore.subscriptions.update(currentSubscriptions => [
        assignedSubscription,
        ...currentSubscriptions,
      ]);

      return assignedSubscription;
    } catch (error) {
      this.submitError.set(error as AppHttpError);
      return null;
    } finally {
      this.submitting.set(false);
    }
  }

  clearError(): void {
    this.error.set(null);
  }

  clearSubmitError(): void {
    this.submitError.set(null);
  }

  resetState(): void {
    this.assignedSubscription.set(null);
    this.selectedPlanCode.set("");
    this.currentPlanCode.set(null);
    this.error.set(null);
    this.submitError.set(null);
    this.loading.set(false);
    this.submitting.set(false);
  }

  // private readonly assignSubscriptionApi = inject(AssignSubscriptionApi);
  // readonly plansStore = inject(PlansStore);

  // readonly assignedSubscription = signal<AssignedSubscription | null>(null);

  // readonly loading = signal(false);
  // readonly error = signal<AppHttpError | null>(null);
  // readonly hasError = computed(() => this.error() !== null);

  // readonly submitting = signal(false);
  // readonly submitError = signal<AppHttpError | null>(null);
  // readonly hasSubmitError = computed(() => this.submitError() !== null);

  // readonly selectedPlanCode = signal<string>("");

  // readonly isLoading = computed(
  //   () => this.loading() || this.plansStore.loading()
  // );

  // readonly availablePlans = computed(() => this.plansStore.plans());

  // readonly isEmpty = computed(
  //   () =>
  //     !this.isLoading() &&
  //     !this.hasError() &&
  //     this.availablePlans().length === 0
  // );

  // readonly selectedPlan = computed(
  //   () =>
  //     this.availablePlans().find(
  //       plan => plan.code === this.selectedPlanCode()
  //     ) ?? null
  // );

  // async loadForAssign(): Promise<void> {
  //   this.loading.set(true);
  //   this.error.set(null);
  //   this.submitError.set(null);
  //   this.assignedSubscription.set(null);
  //   this.selectedPlanCode.set("");

  //   try {
  //     if (!this.plansStore.hasLoaded()) {
  //       await this.plansStore.loadPlans();
  //     }
  //   } catch (error) {
  //     this.error.set(error as AppHttpError);
  //   } finally {
  //     this.loading.set(false);
  //   }
  // }

  // selectPlan(planCode: string): void {
  //   this.selectedPlanCode.set(planCode);
  // }

  // async assign(
  //   tenantId: string,
  //   formValue: AssignSubscriptionFormValue
  // ): Promise<AssignedSubscription | null> {
  //   this.submitting.set(true);
  //   this.submitError.set(null);

  //   try {
  //     const payload = toAssignSubscriptionRequest(tenantId, formValue);
  //     const dto = await firstValueFrom(
  //       this.assignSubscriptionApi.assignSubscription(payload)
  //     );
  //     const assignedSubscription = toAssignedSubscription(dto);

  //     this.assignedSubscription.set(assignedSubscription);

  //     return assignedSubscription;
  //   } catch (error) {
  //     this.submitError.set(error as AppHttpError);
  //     return null;
  //   } finally {
  //     this.submitting.set(false);
  //   }
  // }

  // clearError(): void {
  //   this.error.set(null);
  // }

  // clearSubmitError(): void {
  //   this.submitError.set(null);
  // }

  // resetState(): void {
  //   this.assignedSubscription.set(null);
  //   this.selectedPlanCode.set("");
  //   this.error.set(null);
  //   this.submitError.set(null);
  //   this.loading.set(false);
  //   this.submitting.set(false);
  // }
}
