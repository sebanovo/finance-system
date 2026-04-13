export type Permission = {
  code: string;
  module: string;
  description: string;
};

export type UpdatePermissionRequest = {
  code: string;
  module: string;
  description: string;
};

export type PermissionUpsertFormValue = {
  code: string;
  module: string;
  description: string;
};
