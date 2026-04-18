export type PermissionDto = {
  code: string;
  module: string;
  description: string;
};

export type Permission = {
  code: string;
  module: string;
  description: string;
};

export type PermissionResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};
