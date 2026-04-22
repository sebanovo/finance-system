import { computed, inject, Injectable } from "@angular/core";

import { PlansStore } from "../../plans/store/plan.store";
import { RolesStore } from "../../roles/store/role.store";
import { TenantSettingsStore } from "../../settings/store/setting.store";
import { SubscriptionsStore } from "../../subscriptions/store/subscription.store";
import { TenantsStore } from "../../tanants/store/tenant.store";
import { UsersStore } from "../../users/store/user.store";
import { DashboardService } from "../services/dashboard.service";

@Injectable({
  providedIn: "root",
})
export class DashboardStore {
  private readonly dashboardService = inject(DashboardService);

  readonly usersStore = inject(UsersStore);
  readonly tenantsStore = inject(TenantsStore);
  readonly plansStore = inject(PlansStore);
  readonly rolesStore = inject(RolesStore);
  readonly subscriptionsStore = inject(SubscriptionsStore);
  readonly tenantSettingsStore = inject(TenantSettingsStore);

  readonly loading = computed(
    () =>
      this.usersStore.loading() ||
      this.tenantsStore.loading() ||
      this.plansStore.loading() ||
      this.rolesStore.loading() ||
      this.subscriptionsStore.loading() ||
      this.subscriptionsStore.currentSubscriptionLoading() ||
      this.tenantSettingsStore.loading()
  );

  readonly totalUsers = computed(() => this.usersStore.users().length);
  readonly activeUsers = computed(
    () => this.usersStore.users().filter(user => user.isActive).length
  );

  readonly totalTenants = computed(() => this.tenantsStore.tenants().length);
  readonly activeTenants = computed(
    () => this.tenantsStore.tenants().filter(tenant => tenant.isActive).length
  );

  readonly totalPlans = computed(() => this.plansStore.plans().length);
  readonly activePlans = computed(
    () => this.plansStore.plans().filter(plan => plan.isActive).length
  );

  readonly totalRoles = computed(() => this.rolesStore.roles().length);
  readonly activeRoles = computed(
    () => this.rolesStore.roles().filter(role => role.isActive).length
  );

  readonly totalSubscriptions = computed(
    () => this.subscriptionsStore.subscriptions().length
  );

  readonly currentSubscriptionsCount = computed(
    () =>
      this.subscriptionsStore
        .subscriptions()
        .filter(subscription => subscription.isCurrentSubscription).length
  );

  readonly trialSubscriptionsCount = computed(
    () =>
      this.subscriptionsStore
        .subscriptions()
        .filter(subscription => subscription.isTrial).length
  );

  readonly hasAnyUsefulData = computed(() => {
    return (
      this.usersStore.users().length > 0 ||
      this.tenantsStore.tenants().length > 0 ||
      this.plansStore.plans().length > 0 ||
      this.rolesStore.roles().length > 0 ||
      this.subscriptionsStore.subscriptions().length > 0 ||
      this.subscriptionsStore.currentSubscription() !== null ||
      this.tenantSettingsStore.settings() !== null
    );
  });

  readonly shouldShowGlobalError = computed(() => {
    return !this.loading() && !this.hasAnyUsefulData();
  });

  readonly metrics = computed(() => [
    this.dashboardService.createMetric({
      label: "Usuarios",
      value: this.usersStore.hasError() ? "—" : this.totalUsers(),
      helper: this.usersStore.hasError()
        ? "No disponible"
        : `${this.activeUsers()} activos`,
      tone: this.usersStore.hasError()
        ? "warning"
        : this.activeUsers() > 0
          ? "success"
          : "warning",
      loading: this.usersStore.loading(),
      error: this.usersStore.error()?.message || null,
    }),
    this.dashboardService.createMetric({
      label: "Tenants",
      value: this.tenantsStore.hasError() ? "—" : this.totalTenants(),
      helper: this.tenantsStore.hasError()
        ? "No disponible"
        : `${this.activeTenants()} activos`,
      tone: this.tenantsStore.hasError()
        ? "warning"
        : this.activeTenants() > 0
          ? "success"
          : "warning",
      loading: this.tenantsStore.loading(),
      error: this.tenantsStore.error()?.message || null,
    }),
    this.dashboardService.createMetric({
      label: "Roles",
      value: this.rolesStore.hasError() ? "—" : this.totalRoles(),
      helper: this.rolesStore.hasError()
        ? "No disponible"
        : `${this.activeRoles()} activos`,
      tone: this.rolesStore.hasError()
        ? "warning"
        : this.activeRoles() > 0
          ? "success"
          : "warning",
      loading: this.rolesStore.loading(),
      error: this.rolesStore.error()?.message || null,
    }),
    this.dashboardService.createMetric({
      label: "Suscripciones",
      value: this.subscriptionsStore.hasError()
        ? "—"
        : this.currentSubscriptionsCount(),
      helper: this.subscriptionsStore.hasError()
        ? "No disponible"
        : `${this.trialSubscriptionsCount()} en trial`,
      tone: this.subscriptionsStore.hasError()
        ? "warning"
        : this.currentSubscriptionsCount() > 0
          ? "success"
          : "warning",
      loading: this.subscriptionsStore.loading(),
      error: this.subscriptionsStore.error()?.message || null,
    }),
  ]);

  readonly tenantOverviewState = computed(() =>
    this.dashboardService.createSectionState({
      data: this.dashboardService.toTenantOverview(
        this.tenantSettingsStore.settings()
      ),
      loading: this.tenantSettingsStore.loading(),
      error: this.tenantSettingsStore.error()?.message || null,
    })
  );

  readonly currentSubscriptionState = computed(() =>
    this.dashboardService.createSectionState({
      data: this.dashboardService.toCurrentSubscriptionOverview(
        this.subscriptionsStore.currentSubscription()
      ),
      loading: this.subscriptionsStore.currentSubscriptionLoading(),
      error:
        this.subscriptionsStore.currentSubscriptionError()?.message || null,
    })
  );

  readonly alerts = computed(() =>
    this.dashboardService.createAlerts({
      usersError: this.usersStore.hasError(),
      rolesError: this.rolesStore.hasError(),
      plansError: this.plansStore.hasError(),
      subscriptionsError: this.subscriptionsStore.hasError(),
      currentSubscriptionError:
        this.subscriptionsStore.hasCurrentSubscriptionError(),
      totalUsers: this.totalUsers(),
      activeRoles: this.activeRoles(),
      activePlans: this.activePlans(),
      currentSubscriptionsCount: this.currentSubscriptionsCount(),
      currentSubscription: this.subscriptionsStore.currentSubscription(),
    })
  );

  async loadDashboard(): Promise<void> {
    const tasks: Promise<unknown>[] = [];

    if (!this.usersStore.hasLoaded()) {
      tasks.push(this.usersStore.loadUsers());
    }

    if (!this.tenantsStore.hasLoaded()) {
      tasks.push(this.tenantsStore.loadTenants());
    }

    if (!this.plansStore.hasLoaded()) {
      tasks.push(this.plansStore.loadPlans());
    }

    if (!this.rolesStore.hasLoaded()) {
      tasks.push(this.rolesStore.loadRoles());
    }

    if (!this.subscriptionsStore.hasLoaded()) {
      tasks.push(this.subscriptionsStore.loadSubscriptions());
    }

    if (!this.subscriptionsStore.currentSubscription()) {
      tasks.push(this.subscriptionsStore.loadCurrentSubscription());
    }

    if (!this.tenantSettingsStore.hasLoaded()) {
      tasks.push(this.tenantSettingsStore.loadTenantSettings());
    }

    await Promise.all(tasks);
  }

  async reloadDashboard(): Promise<void> {
    await Promise.all([
      this.usersStore.reloadUsers(),
      this.tenantsStore.reloadTenants(),
      this.plansStore.reloadPlans(),
      this.rolesStore.reloadRoles(),
      this.subscriptionsStore.reloadSubscriptions(),
      this.subscriptionsStore.loadCurrentSubscription(),
      this.tenantSettingsStore.reloadTenantSettings(),
    ]);
  }
}
