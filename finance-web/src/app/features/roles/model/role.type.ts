export type CreateRoleRequest = {
  name: string;
  description: string;
  permissionCodes: string[];
};

export type UpdateRoleRequest = {
  name: string;
  description: string;
  permissionCodes: string[];
};

export type RoleDto = {
  id: string;
  name: string;
  description: string;
  active: boolean;
  createdAt: string;
  permissionCodes: string[];
};

export type Role = {
  id: string;
  name: string;
  description: string;
  isActive: boolean;
  createdAt: string; // Date
  permissionCodes: string[];
};

export type RoleFormValue = {
  name: string;
  description: string;
  permissionCodes: string[];
};

export type RoleUpsertFormValue = {
  name: string;
  description: string;
  permissionCodes: string[];
};

export type RoleResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};
