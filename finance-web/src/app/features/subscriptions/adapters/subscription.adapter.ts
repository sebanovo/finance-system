import { Subscription, SubscriptionDto } from "../models/subscription.type";

export function toSubscription(dto: SubscriptionDto): Subscription {
  return {
    id: dto.id,
    tenantId: dto.tenantId,
    tenantName: dto.tenantName,
    tenantSlug: dto.tenantSlug,
    planId: dto.planId,
    planCode: dto.planCode,
    planName: dto.planName,
    planType: dto.planType,
    maxUsers: dto.maxUsers,
    maxRoles: dto.maxRoles,
    status: dto.status,
    isTrial: dto.trial,
    isCurrentSubscription: dto.currentSubscription,
    startedAt: new Date(dto.startedAt).toDateString(),
    expiresAt: new Date(dto.expiresAt).toDateString(),
    remainingDays: dto.remainingDays,
    createdAt: new Date(dto.createdAt).toDateString(),
    updatedAt: new Date(dto.updatedAt).toDateString(),
  };
}

export function toSubscriptions(dtos: SubscriptionDto[]): Subscription[] {
  return dtos.map(toSubscription);
}
