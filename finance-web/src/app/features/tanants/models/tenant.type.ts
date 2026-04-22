export type CreateTenantRequest = {
  name: string;
  slug: string;
  planCode: string;
};

export type TenantDto = {
  id: string;
  name: string;
  slug: string;
  schemaName: string;
  status: string;
  planId: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
};

export type Tenant = {
  id: string;
  name: string;
  slug: string;
  schemaName: string;
  status: string;
  planId: string;
  isActive: boolean;
  createdAt: string; // Date
  updatedAt: string; // Date
};

export type TenantFormValue = {
  name: string;
  slug: string;
  planCode: string;
};

export type TenantUpsertFormValue = {
  name: string;
  slug: string;
  planCode: string;
};

export type TenantResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};
