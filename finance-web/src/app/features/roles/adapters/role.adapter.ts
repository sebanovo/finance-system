import {
  CreateRoleRequest,
  Role,
  RoleDto,
  RoleUpsertFormValue,
  UpdateRoleRequest,
} from "../model/role.type";

export function toRole(dto: RoleDto): Role {
  return {
    id: dto.id,
    name: dto.name,
    description: dto.description,
    isActive: dto.active,
    createdAt: new Date(dto.createdAt).toDateString(),
    permissionCodes: dto.permissionCodes,
  };
}

export function toRoles(dtos: RoleDto[]): Role[] {
  return dtos.map(toRole);
}

export function toCreateRoleRequest(
  formValue: RoleUpsertFormValue
): CreateRoleRequest {
  return {
    name: formValue.name.trim(),
    description: formValue.description.trim(),
    permissionCodes: formValue.permissionCodes,
  };
}

export function toUpdateRoleRequest(
  formValue: RoleUpsertFormValue
): UpdateRoleRequest {
  return {
    name: formValue.name.trim(),
    description: formValue.description.trim(),
    permissionCodes: formValue.permissionCodes,
  };
}
