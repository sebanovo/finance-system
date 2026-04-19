import {
  AssignedSubscription,
  AssignedSubscriptionDto,
  AssignSubscriptionFormValue,
  AssignSubscriptionRequest,
} from "../model/subscription-assignment.type";

export function toAssignedSubscription(
  dto: AssignedSubscriptionDto
): AssignedSubscription {
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

export function toAssignSubscriptionRequest(
  tenantId: string,
  formValue: AssignSubscriptionFormValue
): AssignSubscriptionRequest {
  return {
    tenantId,
    planCode: formValue.planCode.trim(),
    overrideTrialDays:
      formValue.overrideTrialDays === null ||
      Number.isNaN(formValue.overrideTrialDays)
        ? null
        : formValue.overrideTrialDays,
  };
}
