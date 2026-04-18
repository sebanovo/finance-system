import { toRoles } from "../../roles/adapters/role.adapter";
import {
  AssignUserRolesRequest,
  UserRoleAssignment,
  UserRoleAssignmentDto,
  UserRoleAssignmentFormValue,
} from "../model/role-assignment.type";

export function toUserRoleAssignment(
  dto: UserRoleAssignmentDto
): UserRoleAssignment {
  return {
    userId: dto.userId,
    roles: toRoles(dto.roles),
  };
}

export function toAssignUserRolesRequest(
  formValue: UserRoleAssignmentFormValue
): AssignUserRolesRequest {
  return {
    roleIds: formValue.roleIds,
  };
}
