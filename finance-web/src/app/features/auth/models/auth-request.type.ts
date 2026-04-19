export type AuthMeDto = {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  active: boolean;

  roles: string[];

  status?: string;
  tenantSlug?: string;

  createdAt?: string;
  updatedAt?: string;
};

export type AuthMeData = {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  active: boolean;

  status?: string;
  tenantSlug?: string;
  roles: string[];
  createdAt?: string;
  updatedAt?: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginTenantRequest = {
  email: string;
  password: string;
  tenantSlug: string;
};
