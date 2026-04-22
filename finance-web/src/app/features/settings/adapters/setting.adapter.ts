import {
  TenantSettings,
  TenantSettingsDto,
  TenantSettingsFormValue,
  UpdateTenantSettingsRequest,
} from "../models/setting.type";

export function toTenantSettings(dto: TenantSettingsDto): TenantSettings {
  return {
    companyName: dto.companyName,
    legalName: dto.legalName,
    timezone: dto.timezone,
    currency: dto.currency,
    contactEmail: dto.contactEmail,
    contactPhone: dto.contactPhone,
  };
}

export function toUpdateTenantSettingsRequest(
  formValue: TenantSettingsFormValue
): UpdateTenantSettingsRequest {
  return {
    companyName: formValue.companyName.trim(),
    legalName: formValue.legalName.trim(),
    timezone: formValue.timezone.trim(),
    currency: formValue.currency.trim(),
    contactEmail: formValue.contactEmail.trim(),
    contactPhone: formValue.contactPhone.trim(),
  };
}
