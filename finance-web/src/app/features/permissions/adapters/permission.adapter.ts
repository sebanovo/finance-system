import { Permission, PermissionDto } from "../model/permission.type";

export function toPermission(dto: PermissionDto): Permission {
  return {
    code: dto.code,
    module: dto.module,
    description: dto.description,
  };
}

export function toPermissions(dtos: PermissionDto[]): Permission[] {
  return dtos.map(toPermission);
}
