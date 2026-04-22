import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import {
  toSubscription,
  toSubscriptions,
} from "../adapters/subscription.adapter";
import { Subscription } from "../models/subscription.type";
import { SubscriptionsApi } from "../services/subscription.service";

@Injectable({
  providedIn: "root",
})
export class SubscriptionsStore {
  private readonly subscriptionsApi = inject(SubscriptionsApi);

  readonly subscriptions = signal<Subscription[]>([]);
  readonly loading = signal(false);
  readonly hasLoaded = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly hasError = computed(() => this.error() !== null);

  readonly selectedSubscription = signal<Subscription | null>(null);
  readonly selectedSubscriptionLoading = signal(false);
  readonly selectedSubscriptionError = signal<AppHttpError | null>(null);
  readonly hasSelectedSubscriptionError = computed(
    () => this.selectedSubscriptionError() !== null
  );

  readonly currentSubscription = signal<Subscription | null>(null);
  readonly currentSubscriptionLoading = signal(false);
  readonly currentSubscriptionError = signal<AppHttpError | null>(null);
  readonly hasCurrentSubscriptionError = computed(
    () => this.currentSubscriptionError() !== null
  );

  readonly isEmpty = computed(
    () =>
      this.hasLoaded() && !this.loading() && this.subscriptions().length === 0
  );

  async loadSubscriptions(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const subscriptionDtos = await firstValueFrom(
        this.subscriptionsApi.getSubscriptions()
      );
      this.subscriptions.set(toSubscriptions(subscriptionDtos));
      this.hasLoaded.set(true);
    } catch (error) {
      this.error.set(error as AppHttpError);
      this.subscriptions.set([]);
      this.hasLoaded.set(true);
    } finally {
      this.loading.set(false);
    }
  }

  async loadSubscriptionById(
    subscriptionId: string
  ): Promise<Subscription | null> {
    this.selectedSubscriptionLoading.set(true);
    this.selectedSubscriptionError.set(null);
    this.selectedSubscription.set(null);

    try {
      const subscriptionDto = await firstValueFrom(
        this.subscriptionsApi.getSubscriptionById(subscriptionId)
      );
      const subscription = toSubscription(subscriptionDto);

      this.selectedSubscription.set(subscription);

      return subscription;
    } catch (error) {
      this.selectedSubscriptionError.set(error as AppHttpError);
      this.selectedSubscription.set(null);
      return null;
    } finally {
      this.selectedSubscriptionLoading.set(false);
    }
  }

  async loadCurrentSubscription(): Promise<Subscription | null> {
    this.currentSubscriptionLoading.set(true);
    this.currentSubscriptionError.set(null);
    this.currentSubscription.set(null);

    try {
      const subscriptionDto = await firstValueFrom(
        this.subscriptionsApi.getCurrentSubscription()
      );
      const subscription = toSubscription(subscriptionDto);

      this.currentSubscription.set(subscription);

      return subscription;
    } catch (error) {
      this.currentSubscriptionError.set(error as AppHttpError);
      this.currentSubscription.set(null);
      return null;
    } finally {
      this.currentSubscriptionLoading.set(false);
    }
  }

  reloadSubscriptions(): Promise<void> {
    return this.loadSubscriptions();
  }

  clearError(): void {
    this.error.set(null);
  }

  clearSelectedSubscriptionError(): void {
    this.selectedSubscriptionError.set(null);
  }

  clearSelectedSubscription(): void {
    this.selectedSubscription.set(null);
  }

  clearCurrentSubscriptionError(): void {
    this.currentSubscriptionError.set(null);
  }

  clearCurrentSubscription(): void {
    this.currentSubscription.set(null);
  }
}
