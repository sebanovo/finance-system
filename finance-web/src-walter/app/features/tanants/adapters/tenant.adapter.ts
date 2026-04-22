import {
  CreateTenantRequest,
  Tenant,
  TenantDto,
  TenantUpsertFormValue,
} from "../models/tenant.type";

export function toTenant(dto: TenantDto): Tenant {
  return {
    id: dto.id,
    name: dto.name,
    slug: dto.slug,
    schemaName: dto.schemaName,
    status: dto.status,
    planId: dto.planId,
    isActive: dto.active,
    createdAt: new Date(dto.createdAt).toDateString(),
    updatedAt: new Date(dto.updatedAt).toDateString(),
  };
}

export function toTenants(dtos: TenantDto[]): Tenant[] {
  return dtos.map(toTenant);
}

export function toCreateTenantRequest(
  formValue: TenantUpsertFormValue
): CreateTenantRequest {
  return {
    name: formValue.name.trim(),
    slug: formValue.slug.trim(),
    planCode: formValue.planCode.trim(),
  };
}
