import { AuthMeData, AuthMeDto } from "../models/auth-request.type";
import {
  SignupData,
  SignupResponseDataDto,
} from "../models/auth-response.type";

export function toAuthMe(dto: AuthMeDto): AuthMeData {
  return {
    id: dto.id,
    email: dto.email,
    firstName: dto.firstName,
    lastName: dto.lastName,
    active: dto.active,

    roles: dto.roles ?? [],

    status: dto.status,
    tenantSlug: dto.tenantSlug,

    createdAt: dto.createdAt
      ? new Date(dto.createdAt).toISOString()
      : undefined,

    updatedAt: dto.updatedAt
      ? new Date(dto.updatedAt).toISOString()
      : undefined,
  };
}

export function toSignupData(dto: SignupResponseDataDto): SignupData {
  return {
    tenantId: dto.tenantId,
    tenantSlug: dto.tenantSlug,
    companyName: dto.companyName,
    adminEmail: dto.adminEmail,
    initialRole: dto.initialRole,
    currentPlanCode: dto.currentPlanCode,
    subscriptionStatus: dto.subscriptionStatus,

    trialExpiresAt: new Date(dto.trialExpiresAt).toDateString(),
    loginHint: dto.loginHint,
  };
}
