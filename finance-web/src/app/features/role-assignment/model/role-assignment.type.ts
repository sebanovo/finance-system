import { Role, RoleDto } from "../../roles/model/role.type";

export type AssignUserRolesRequest = {
  roleIds: string[];
};

export type UserRoleAssignmentDto = {
  userId: string;
  roles: RoleDto[];
};

export type UserRoleAssignment = {
  userId: string;
  roles: Role[];
};

export type UserRoleAssignmentFormValue = {
  roleIds: string[];
};

export type UserRoleAssignmentUserContext = {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
};

export type UserRoleAssignmentResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};
