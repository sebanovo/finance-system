import {
  CreatePlanRequest,
  Plan,
  PlanDto,
  PlanUpsertFormValue,
  UpdatePlanRequest,
} from "../models/plan.type";

export function toPlan(dto: PlanDto): Plan {
  return {
    id: dto.id,
    code: dto.code,
    name: dto.name,
    description: dto.description,
    maxUsers: dto.maxUsers,
    maxRoles: dto.maxRoles,
    planType: dto.planType,
    trialDays: dto.trialDays,
    isActive: dto.active,
    createdAt: new Date(dto.createdAt).toDateString(),
    updatedAt: new Date(dto.updatedAt).toDateString(),
  };
}

export function toPlans(dtos: PlanDto[]): Plan[] {
  return dtos.map(toPlan);
}

export function toCreatePlanRequest(
  formValue: PlanUpsertFormValue
): CreatePlanRequest {
  return {
    code: formValue.code.trim(),
    name: formValue.name.trim(),
    description: formValue.description.trim(),
    maxUsers: formValue.maxUsers,
    maxRoles: formValue.maxRoles,
    planType: formValue.planType,
    trialDays: formValue.trialDays,
  };
}

export function toUpdatePlanRequest(
  formValue: PlanUpsertFormValue
): UpdatePlanRequest {
  return {
    code: formValue.code.trim(),
    name: formValue.name.trim(),
    description: formValue.description.trim(),
    maxUsers: formValue.maxUsers,
    maxRoles: formValue.maxRoles,
    planType: formValue.planType,
    trialDays: formValue.trialDays,
  };
}
