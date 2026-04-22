export type AuthMeData = {
  id: string;
  email: string;
  firstName: string;
  lastName: string;

  active: boolean;
  status: string;

  tenantSlug: string;

  roles: string[];
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
