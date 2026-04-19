export type UpdateTenantSettingsRequest = {
  companyName: string;
  legalName: string;
  timezone: string;
  currency: string;
  contactEmail: string;
  contactPhone: string;
};

export type TenantSettingsDto = {
  companyName: string;
  legalName: string;
  timezone: string;
  currency: string;
  contactEmail: string;
  contactPhone: string;
};

export type TenantSettings = {
  companyName: string;
  legalName: string;
  timezone: string;
  currency: string;
  contactEmail: string;
  contactPhone: string;
};

export type TenantSettingsFormValue = {
  companyName: string;
  legalName: string;
  timezone: string;
  currency: string;
  contactEmail: string;
  contactPhone: string;
};

export type TenantSettingsResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};
