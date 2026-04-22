import { Injectable } from "@angular/core";
import { TenantSettings } from "../../settings/models/setting.type";
import { Subscription } from "../../subscriptions/models/subscription.type";
import {
  DashboardAlert,
  DashboardCurrentSubscriptionOverview,
  DashboardMetric,
  DashboardSectionState,
  DashboardTenantOverview,
} from "../models/dashboard.type";

@Injectable({
  providedIn: "root",
})
export class DashboardService {
  createMetric(params: {
    label: string;
    value: number | string;
    helper: string;
    tone?: "default" | "success" | "warning";
    loading?: boolean;
    error?: string | null;
  }): DashboardMetric {
    return {
      label: params.label,
      value: params.value,
      helper: params.helper,
      tone: params.tone ?? "default",
      loading: params.loading ?? false,
      error: params.error ?? null,
    };
  }

  createSectionState<T>(params: {
    data: T | null;
    loading: boolean;
    error: string | null;
  }): DashboardSectionState<T> {
    return {
      data: params.data,
      loading: params.loading,
      error: params.error,
      isEmpty: !params.loading && !params.error && params.data === null,
    };
  }

  toTenantOverview(
    settings: TenantSettings | null
  ): DashboardTenantOverview | null {
    if (!settings) {
      return null;
    }

    return {
      companyName: settings.companyName,
      legalName: settings.legalName,
      timezone: settings.timezone,
      currency: settings.currency,
      contactEmail: settings.contactEmail,
      contactPhone: settings.contactPhone,
    };
  }

  toCurrentSubscriptionOverview(
    subscription: Subscription | null
  ): DashboardCurrentSubscriptionOverview | null {
    if (!subscription) {
      return null;
    }

    return {
      planName: subscription.planName,
      planCode: subscription.planCode,
      planType: subscription.planType,
      status: subscription.status,
      isTrial: subscription.isTrial,
      remainingDays: subscription.remainingDays,
      expiresAt: subscription.expiresAt,
      maxUsers: subscription.maxUsers,
      maxRoles: subscription.maxRoles,
    };
  }

  createAlerts(params: {
    usersError: boolean;
    rolesError: boolean;
    plansError: boolean;
    subscriptionsError: boolean;
    currentSubscriptionError: boolean;
    totalUsers: number;
    activeRoles: number;
    activePlans: number;
    currentSubscriptionsCount: number;
    currentSubscription: Subscription | null;
  }): DashboardAlert[] {
    const alerts: DashboardAlert[] = [];

    if (!params.usersError && params.totalUsers === 0) {
      alerts.push({
        id: "no-users",
        title: "No hay usuarios registrados",
        description:
          "Crea el primer usuario para comenzar a operar el sistema.",
        tone: "warning",
      });
    }

    if (!params.rolesError && params.activeRoles === 0) {
      alerts.push({
        id: "no-active-roles",
        title: "No hay roles activos",
        description:
          "Activa al menos un rol para permitir asignaciones de acceso.",
        tone: "critical",
      });
    }

    if (!params.plansError && params.activePlans === 0) {
      alerts.push({
        id: "no-active-plans",
        title: "No hay planes activos",
        description: "Activa un plan para que pueda ser asignado a tenants.",
        tone: "warning",
      });
    }

    if (
      !params.currentSubscriptionError &&
      params.currentSubscription &&
      params.currentSubscription.remainingDays <= 15
    ) {
      alerts.push({
        id: "subscription-expiring-soon",
        title: "La suscripción actual vence pronto",
        description: `Quedan ${params.currentSubscription.remainingDays} días antes de la expiración.`,
        tone:
          params.currentSubscription.remainingDays <= 7
            ? "critical"
            : "warning",
      });
    }

    if (!params.subscriptionsError && params.currentSubscriptionsCount === 0) {
      alerts.push({
        id: "no-current-subscriptions",
        title: "No hay suscripciones vigentes",
        description: "Verifica la asignación de planes para tenants activos.",
        tone: "critical",
      });
    }

    return alerts;
  }
}
